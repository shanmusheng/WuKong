package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.entity.CloudStepLeftEntity;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

/**
 * 聚形散气，隐身越久伤害越高
 */
public class CloudStepSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d191cc-f98f-10ed-a05b-0242ac114514");
    public static SkillDataManager.SkillDataKey<Integer> TRANSPARENT_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//隐身计时器
    public static SkillDataManager.SkillDataKey<Integer> CHARGING_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//加伤计时器
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//冷却计时器 TODO
    public static final int MAX_TIME = 200;//10s

    public StaticAnimation pre, post;

    public static Builder createCloudStep() {
        return new Builder().setCategory(WukongSkillCategories.SHEN_FA).setResource(Resource.NONE);
    }

    public CloudStepSkill(Builder builder) {
        super(builder);
        pre = builder.pre.get();
        post = builder.post.get();
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, TRANSPARENT_TIMER, 0);
        SkillDataRegister.register(manager, CHARGING_TIMER, 0);

        //不能拦截普攻事件，普攻事件已经滞后了
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event) -> {
            if(!event.getPlayerPatch().isLogicalClient()){
                //平A换成破隐
                if(event.getSkillContainer().getSkill().getCategory().equals(SkillCategories.BASIC_ATTACK)){
                    if(manager.getDataValue(TRANSPARENT_TIMER) > 10){
                        event.setCanceled(true);
                        event.getPlayerPatch().playAnimationSynchronized(post, 0.0F);
                        manager.setDataSync(TRANSPARENT_TIMER, 10, ((ServerPlayer) event.getPlayerPatch().getOriginal()));
                    }
                }
            }
            //重置加伤计时器
            if(event.getSkillContainer().getSkill().equals(this) && !event.getPlayerPatch().isLogicalClient()){
                manager.setDataSync(CHARGING_TIMER, MAX_TIME, ((ServerPlayer) event.getPlayerPatch().getOriginal()));
            }
        });

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID, (event) -> {
            //受伤则退出隐身
            if(manager.getDataValue(TRANSPARENT_TIMER) > 10){
                manager.setDataSync(TRANSPARENT_TIMER, 10, event.getPlayerPatch().getOriginal());
            }
        });

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event) -> {
            //根据隐身时间加伤（没有暴击率的概念...）
            int chargingTime = manager.getDataValue(CHARGING_TIMER);
            if(event.getDamageSource().getAnimation().equals(post)){
                double damageBoost = 1 + (0.5 * (MAX_TIME - chargingTime) / MAX_TIME);
                event.setAttackDamage((float) (damageBoost * event.getAttackDamage()));
            }
        });

        //有攻击则重置计时
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
            if(manager.getDataValue(TRANSPARENT_TIMER) > 10){
                manager.setDataSync(TRANSPARENT_TIMER, 10, event.getPlayerPatch().getOriginal());
            }
        });

    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_POST, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){

        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if(manager.getDataValue(TRANSPARENT_TIMER) > 0){
                manager.setDataSync(TRANSPARENT_TIMER, manager.getDataValue(TRANSPARENT_TIMER) - 1, serverPlayer);
            }
        }
    }

    /**
     * TODO 判断冷却
     */
    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.playSound(WuKongSounds.PERFECT_DODGE.get(), 0.0F, 0.0F);
        executer.playAnimationSynchronized(WukongAnimations.CLOUD_STEP_START, 0.15F);
        SkillContainer container = executer.getSkill(this);
        SkillDataManager dataManager = container.getDataManager();
        dataManager.setDataSync(TRANSPARENT_TIMER, MAX_TIME, executer.getOriginal());
        PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageParticle(executer.getOriginal().getId()));
        executer.getOriginal().getLevel().addFreshEntity(new CloudStepLeftEntity(executer));//召唤假身
    }

    public static class Builder extends Skill.Builder<CloudStepSkill> {

        protected StaticAnimationProvider pre, post;

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }
        public Builder setAnim(StaticAnimationProvider pre, StaticAnimationProvider post) {
            this.pre = pre;
            this.post = post;
            return this;
        }

    }

}

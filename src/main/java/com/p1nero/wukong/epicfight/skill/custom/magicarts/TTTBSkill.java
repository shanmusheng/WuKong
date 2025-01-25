package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

/**
 * 铜头铁臂
 */
public class TTTBSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d114cc-f98f-10ed-a05b-0242ac114514");
    public static SkillDataManager.SkillDataKey<Integer> TTTB_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//特效计时器 TODO
    public static SkillDataManager.SkillDataKey<Integer> SUCCESS_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//成功弹反后瞬放蓄力的计时器
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//冷却计时器
    public StaticAnimation anim, fail, end;
    public static final int MAX_COOLDOWN = 300;//15s

    public static Builder createTTTB() {
        return new Builder().setCategory(WukongSkillCategories.SHEN_FA).setResource(Resource.NONE);
    }


    public TTTBSkill(Builder builder) {
        super(builder);
        anim = builder.anim.get();
        fail = builder.fail.get();
        end = builder.end.get();
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, TTTB_TIMER, 0);
        SkillDataRegister.register(manager, SUCCESS_TIMER, 0);
        SkillDataRegister.register(manager, COOLDOWN_TIMER, 0);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
            //完美弹反判断
            AnimationPlayer animationPlayer = event.getPlayerPatch().getAnimator().getPlayerFor(null);
            if(animationPlayer.getAnimation().equals(anim)){
                if (animationPlayer.getElapsedTime() <= 1.2F) {
                    Entity entity = event.getDamageSource().getEntity();
                    ServerPlayer serverPlayer = event.getPlayerPatch().getOriginal();
                    serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), EpicFightSounds.CLASH, serverPlayer.getSoundSource(), 1.0F, 1.0F);
                    if(animationPlayer.getElapsedTime() >= 0.5F && animationPlayer.getElapsedTime() <= 1.0F){
                        serverPlayer.level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), EpicFightSounds.NEUTRALIZE_MOBS, serverPlayer.getSoundSource(), 1.0F, 1.0F);
                        manager.setDataSync(SUCCESS_TIMER, 20, serverPlayer);//此期间内可以秒放蓄力，具体在各个棍法里判断
                        if(entity != null){
                            Vec3 self = serverPlayer.position();
                            Vec3 target = entity.position();
                            Vec3 dir = target.subtract(self).normalize().scale(2);
                            entity.push(dir.x, 0, dir.z);//击退
                            entity.hurt(DamageSource.playerAttack(serverPlayer), event.getAmount() * 0.3F);//反弹伤害
                            LivingEntityPatch<?> patch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                            if (patch != null) {
                                patch.applyStun(StunType.NEUTRALIZE, 1.0F);
                            }
                        }
                        event.getPlayerPatch().playAnimationSynchronized(end, 0.15F);
                        serverPlayer.removeEffect(MobEffects.GLOWING);
                        serverPlayer.getLevel().sendParticles(EpicFightParticles.GROUND_SLAM.get(), serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 0, 1.0, 20.0, 0.5, 1);
//                    manager.setDataSync(TTTB_TIMER, 0, serverPlayer);
                    }
                } else {
                    event.getPlayerPatch().playAnimationSynchronized(fail, 0.15F);
                }
            }
        });
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){

        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if(manager.getDataValue(TTTB_TIMER) > 0){
                manager.setDataSync(TTTB_TIMER, manager.getDataValue(TTTB_TIMER) - 1, serverPlayer);
            }
            if(manager.getDataValue(SUCCESS_TIMER) > 0){
                manager.setDataSync(SUCCESS_TIMER, manager.getDataValue(SUCCESS_TIMER) - 1, serverPlayer);
            }
            if(manager.getDataValue(COOLDOWN_TIMER) > 0){
                manager.setDataSync(COOLDOWN_TIMER, manager.getDataValue(COOLDOWN_TIMER) - 1, serverPlayer);
            }
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && (executer.getOriginal().isCreative() || executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.playAnimationSynchronized(anim, 0.15F);
        executer.playSound(WuKongSounds.PERFECT_DODGE.get(), 0.0F, 0.0F);
        SkillContainer container = executer.getSkill(this);
        SkillDataManager dataManager = container.getDataManager();
        dataManager.setDataSync(COOLDOWN_TIMER, MAX_COOLDOWN, executer.getOriginal());
        executer.getOriginal().addEffect(new MobEffectInstance(MobEffects.GLOWING, 30));
    }

    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOLDOWN_TIMER) > 0;
    }

    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        float second = (container.getDataManager().getDataValue(COOLDOWN_TIMER) / 20.0F);
        GuiComponent.drawString(poseStack ,gui.font, String.format("%.1f", second), (int) (second > 10 ? (x + 3) : (x + 6)), (int) (y + 6), 16777215);
    }

    public static class Builder extends Skill.Builder<TTTBSkill> {

        protected StaticAnimationProvider anim, fail, end;

        public TTTBSkill.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public TTTBSkill.Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public TTTBSkill.Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public TTTBSkill.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }
        public TTTBSkill.Builder setAnim(StaticAnimationProvider anim, StaticAnimationProvider fail, StaticAnimationProvider end) {
            this.anim = anim;
            this.fail = fail;
            this.end = end;
            return this;
        }

    }
    
}

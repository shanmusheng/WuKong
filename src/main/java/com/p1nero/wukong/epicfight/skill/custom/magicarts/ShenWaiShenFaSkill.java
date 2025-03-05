package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.capability.entity.FakeWukongEntityPatch;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.WukongDamageSourceTags;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.HeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 复制轻棍终结式、蓄力重棍、斩棍式、江海翻、进尺
 */
public class ShenWaiShenFaSkill extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f11f-11ed-a05b-0282ac114513");
    public static final int MAX_COOLDOWN = 2400;//120s
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//冷却计时器
    public ShenWaiShenFaSkill(Builder builder) {
        super(builder);
    }

    public static ShenWaiShenFaSkill.Builder create() {
        return new ShenWaiShenFaSkill.Builder().setCategory(WukongSkillCategories.HAO_MAO).setResource(Resource.NONE);
    }

    public static void register(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER, true);//冷却计时器
        });
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, COOLDOWN_TIMER, 0);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (hurtEvent -> {
            if(hurtEvent.getPlayerPatch().getOriginal() == hurtEvent.getDamageSource().getEntity() || (hurtEvent.getDamageSource().getEntity() instanceof FakeWukongEntity fakeWukongEntity && fakeWukongEntity.getOwner() != null && hurtEvent.getPlayerPatch().getOriginal().getId() == fakeWukongEntity.getOwner().getId())){
                hurtEvent.setAmount(0);
                hurtEvent.setResult(AttackResult.ResultType.MISSED);
                hurtEvent.setCanceled(true);
            }
        }),10);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (actionEvent -> {
            StaticAnimation animation = actionEvent.getAnimation();
            ServerPlayerPatch executor = actionEvent.getPlayerPatch();
            Skill weaponInnate = executor.getSkill(SkillSlots.WEAPON_INNATE).getSkill();
            if(weaponInnate instanceof HeavyAttack heavyAttacks){
                if(heavyAttacks.getHeavyAttacks().contains(animation) || animation.equals(WukongAnimations.STAFF_AUTO5)){
                    executor.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                        for(int id : wkPlayer.getFakeWukongIds()){
                            if(executor.getOriginal().getLevel().getEntity(id) instanceof FakeWukongEntity fakeWukongEntity){
                                if(executor.getTarget() != null && fakeWukongEntity.distanceTo(executor.getTarget()) < 4){
                                    fakeWukongEntity.getLookControl().setLookAt(executor.getTarget());
                                    FakeWukongEntityPatch fakeWukongEntityPatch = EpicFightCapabilities.getEntityPatch(fakeWukongEntity, FakeWukongEntityPatch.class);
                                    fakeWukongEntityPatch.playAnimationSynchronized(animation, 0.15F);
                                }
                            }
                        }
                    });
                }
            };
        }));
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);

        SkillDataManager manager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){

        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if(manager.getDataValue(COOLDOWN_TIMER) > 0){
                manager.setDataSync(COOLDOWN_TIMER, manager.getDataValue(COOLDOWN_TIMER) - 1, serverPlayer);
            }
        }
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.getFakeWukongIds().clear());
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && (executer.getOriginal().isCreative() || executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executor, FriendlyByteBuf args) {
        super.executeOnServer(executor, args);
        executor.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.getFakeWukongIds().clear());
        executor.playAnimationSynchronized(WukongAnimations.SHEN_WAI_SHEN_FA, 0.0F);
        executor.playSound(WuKongSounds.FEN_SHEN.get(), 0.5F, 0.0F, 0.0F);
        executor.getSkill(this).getDataManager().setDataSync(COOLDOWN_TIMER, MAX_COOLDOWN, executor.getOriginal());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOLDOWN_TIMER) > 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        float second = (container.getDataManager().getDataValue(COOLDOWN_TIMER) / 20.0F);
        GuiComponent.drawString(poseStack ,gui.font, String.format("%.1f", second), (int) (second > 10 ? (x + 3) : (x + 6)), (int) (y + 6), 16777215);
    }

    public static class Builder extends Skill.Builder<ShenWaiShenFaSkill> {
        public Builder() {
        }

        public ShenWaiShenFaSkill.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public ShenWaiShenFaSkill.Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public ShenWaiShenFaSkill.Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public ShenWaiShenFaSkill.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

    }

}
package com.p1nero.wukong.epicfight.skill.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.capability.WKPlayer;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 共用冷却
 */
public abstract class CoolDownSkill extends Skill {

    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER, true);//冷却计时器
    public CoolDownSkill(Builder<? extends Skill> builder) {
        super(builder);
    }
    public static void register(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER, true);//冷却计时器
        });
    }
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataRegister.register(container.getDataManager(), COOLDOWN_TIMER, 0);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && (executer.getOriginal().isCreative() || executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0);
    }

    public abstract int getMaxCooldown();

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
}

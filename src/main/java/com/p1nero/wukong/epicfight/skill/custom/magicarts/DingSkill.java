package com.p1nero.wukong.epicfight.skill.custom.magicarts;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 定身术
 */
public class DingSkill extends Skill {
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//冷却计时器
    public static final int MAX_COOLDOWN_TIME = 600;//30s

    public DingSkill(Builder builder) {
        super(builder);
    }

    public static Builder createDing() {
        return new Builder().setCategory(WukongSkillCategories.QI_SHU).setResource(Skill.Resource.NONE);
    }
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, COOLDOWN_TIMER, 0);
    }
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.playAnimationSynchronized(WukongAnimations.DING, 0.15F);
        SkillContainer container = executer.getSkill(this);
        SkillDataManager dataManager = container.getDataManager();
        dataManager.setDataSync(COOLDOWN_TIMER, MAX_COOLDOWN_TIME, executer.getOriginal());
    }
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if (!container.getExecuter().isLogicalClient()) {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if(manager.getDataValue(COOLDOWN_TIMER) > 0){
                manager.setDataSync(COOLDOWN_TIMER, manager.getDataValue(COOLDOWN_TIMER) - 1, serverPlayer);
            }
        }
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
    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && (executer.getOriginal().isCreative() || executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0);
    }

    public static class Builder extends Skill.Builder<DingSkill> {

        protected StaticAnimationProvider preF, preB, postF, post;

        public DingSkill.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public DingSkill.Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public DingSkill.Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public DingSkill.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

    }
}

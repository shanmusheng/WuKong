package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 定身术
 */
public class DingSkill extends QiShuSkill {
    public static final int MAX_COOLDOWN_TIME = 600;//30s

    public DingSkill(Builder builder) {
        super(builder);
    }

    public static Builder createDing() {
        return new Builder().setCategory(WukongSkillCategories.QI_SHU).setResource(Skill.Resource.NONE);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.playAnimationSynchronized(WukongAnimations.DING, 0.15F);
    }

    @Override
    public int getMaxCooldown() {
        return MAX_COOLDOWN_TIME;
    }

    public static class Builder extends Skill.Builder<DingSkill> {

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

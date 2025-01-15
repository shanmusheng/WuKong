package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.epicfight.skill.custom.PillarHeavyAttack;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PillarLoopedActionAnimation extends ActionAnimation {

    public PillarLoopedActionAnimation(float convertTime, String path, Armature armature) {
        super(convertTime, path, armature);
    }

    public PillarLoopedActionAnimation(float convertTime, float postDelay, String path, Armature armature) {
        super(convertTime, postDelay, path, armature);
    }

    @Override
    public void tick(LivingEntityPatch<?> entityPatch) {
        super.tick(entityPatch);
        if(entityPatch instanceof ServerPlayerPatch patch){
            SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            if(manager.hasData(PillarHeavyAttack.IS_CHARGING) && manager.getDataValue(PillarHeavyAttack.IS_CHARGING)){
                AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                if(player.getElapsedTime() >= this.getTotalTime() - 0.05F){
                    player.setElapsedTime(0.01F);
                }
            }
        }
    }
}

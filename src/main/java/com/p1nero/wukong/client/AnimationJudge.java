package com.p1nero.wukong.client;

import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.HeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.PillarHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;

import java.util.Arrays;

/*
用来判断动画方便一些
 */

public record AnimationJudge() {

    static final StaticAnimation[] TWO_STAGE;
    static final StaticAnimation[] THREE_STAGE;
    static final StaticAnimation[] FOUR_STAGE;
    static {

        TWO_STAGE = new StaticAnimation[] {
                WukongAnimations.SMASH_CHARGED2,
                WukongAnimations.THRUST_CHARGED2,
                WukongAnimations.PILLAR_CHARGED2,
                WukongAnimations.PILLAR_PRE2,
        };
        THREE_STAGE = new StaticAnimation[]{
                WukongAnimations.SMASH_CHARGED3,
                WukongAnimations.THRUST_CHARGED3,
                WukongAnimations.PILLAR_CHARGED3,
                WukongAnimations.PILLAR_PRE3,
        };
        FOUR_STAGE = new StaticAnimation[]{
                WukongAnimations.SMASH_CHARGED4,
                WukongAnimations.THRUST_CHARGED4,
                WukongAnimations.PILLAR_CHARGED4,
                WukongAnimations.PILLAR_PRE4,
        };
    }
    public static boolean isTwo(StaticAnimation staticAnimation) {
        return Arrays.asList(TWO_STAGE).contains(staticAnimation);
    }
    public static boolean isThree(StaticAnimation staticAnimation) {
        return Arrays.asList(THREE_STAGE).contains(staticAnimation);
    }
    public static boolean isFour(StaticAnimation staticAnimation) {
        return Arrays.asList(FOUR_STAGE).contains(staticAnimation);
    }

    public static boolean isCharging(LocalPlayerPatch lpp) {
        return lpp.getSkill(SkillSlots.WEAPON_INNATE) != null && (
                (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(SmashHeavyAttack.IS_CHARGING) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SmashHeavyAttack.IS_CHARGING))
                        || (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(ThrustHeavyAttack.IS_CHARGING) && (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(ThrustHeavyAttack.IS_CHARGING)))
                        || (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(HeavyAttack.IS_CHARGING) && (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(HeavyAttack.IS_CHARGING)))
                        || (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(PillarHeavyAttack.IS_CHARGING) && (lpp.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(PillarHeavyAttack.IS_CHARGING))));
    }

    public static boolean changemode(StaticAnimation staticAnimation, LocalPlayerPatch lpp) {
        if ((Arrays.asList(FOUR_STAGE).contains(staticAnimation) || Arrays.asList(THREE_STAGE).contains(staticAnimation) || Arrays.asList(TWO_STAGE).contains(staticAnimation)) && (lpp.getEntityState().getLevel() != 3)) return true;
        else return isCharging(lpp);
    }
}

package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.epicfight.skill.custom.HeavyAttack;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;

import java.util.Arrays;

/**
 * 用来判断动画方便一些
 */
public record AnimationJudge() {

    static final StaticAnimation[] GLOW;
    static final StaticAnimation[] QIE;
    static final StaticAnimation[] TWO_STAGE;
    static final StaticAnimation[] THREE_STAGE;
    static final StaticAnimation[] FOUR_STAGE;

    static {
        GLOW = new StaticAnimation[]{
                WukongAnimations.SMASH_SPECIAL1,
                WukongAnimations.SMASH_SPECIAL2,
                WukongAnimations.SMASH_CHARGED1,
                WukongAnimations.THRUST_CHARGED1,
                WukongAnimations.SMASH_CHARGED2,
                WukongAnimations.THRUST_CHARGED2,
                WukongAnimations.SMASH_CHARGED3,
                WukongAnimations.THRUST_CHARGED3,
                WukongAnimations.SMASH_CHARGED4,
                WukongAnimations.THRUST_CHARGED4,
        };
        QIE = new StaticAnimation[]{
                WukongAnimations.SMASH_SPECIAL1,
                WukongAnimations.SMASH_SPECIAL2,
        };
        TWO_STAGE = new StaticAnimation[]{
                WukongAnimations.SMASH_CHARGED2,
                WukongAnimations.THRUST_CHARGED2,
        };
        THREE_STAGE = new StaticAnimation[]{
                WukongAnimations.SMASH_CHARGED3,
                WukongAnimations.THRUST_CHARGED3,
        };
        FOUR_STAGE = new StaticAnimation[]{
                WukongAnimations.SMASH_CHARGED4,
                WukongAnimations.THRUST_CHARGED4,
        };
    }

    public static boolean isGlow(StaticAnimation staticAnimation) {
        return Arrays.asList(GLOW).contains(staticAnimation);
    }

    public static boolean isQie(StaticAnimation staticAnimation) {
        return Arrays.asList(QIE).contains(staticAnimation);
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

    public static boolean isCharging(LocalPlayerPatch localPlayerPatch) {
        SkillDataManager manager = localPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
        return manager.hasData(HeavyAttack.IS_CHARGING) && manager.getDataValue(HeavyAttack.IS_CHARGING);
    }

    public static boolean changeMode(StaticAnimation staticAnimation, LocalPlayerPatch lpp) {
        if ((Arrays.asList(FOUR_STAGE).contains(staticAnimation) || Arrays.asList(THREE_STAGE).contains(staticAnimation) || Arrays.asList(TWO_STAGE).contains(staticAnimation)) && (lpp.getEntityState().getLevel() != 3))
            return true;
        else return isCharging(lpp);
    }
}

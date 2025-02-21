package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.*;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.*;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.StepSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

public class WukongSkills {
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill WUKONG_DODGE;

    public static Skill DING;
    public static Skill AN_SHEN_FA;
    public static Skill JU_XING_SAN_QI;
    public static Skill TONG_TOU_TIE_BI;
    public static Skill SHEN_WAI_SHEN_FA;

    public static void registerSkills() {

        SkillManager.register(WukongDodgeSkill::new, WukongDodgeSkill.createDodgeBuilder()
                        .setAnimations1(
                                () -> WukongAnimations.DODGE_F1,
                                () -> WukongAnimations.DODGE_B1,
                                () -> WukongAnimations.DODGE_L1,
                                () -> WukongAnimations.DODGE_R1
                        )
                        .setAnimations2(
                                () -> WukongAnimations.DODGE_F2,
                                () -> WukongAnimations.DODGE_B2,
                                () -> WukongAnimations.DODGE_L2,
                                () -> WukongAnimations.DODGE_R2
                        )
                        .setAnimations3(
                                () -> WukongAnimations.DODGE_F3,
                                () -> WukongAnimations.DODGE_B3,
                                () -> WukongAnimations.DODGE_L3,
                                () -> WukongAnimations.DODGE_R3
                        )
                        .setPerfectAnimations(
                                () -> WukongAnimations.DODGE_FP,
                                () -> WukongAnimations.DODGE_BP,
                                () -> WukongAnimations.DODGE_LP,
                                () -> WukongAnimations.DODGE_RP
                        ).setCreativeTab(null),
                WukongMoveset.MOD_ID, "dodge");

        SkillManager.register(StaffPassive::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");

        SkillManager.register(SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(() -> WukongAnimations.SMASH_CHARGING_PRE)
                        .setChargingAnimation(() -> WukongAnimations.SMASH_CHARGING_LOOP)
                        .setHeavyAttacks(
                                () -> WukongAnimations.SMASH_CHARGED0,
                                () -> WukongAnimations.SMASH_CHARGED1,
                                () -> WukongAnimations.SMASH_CHARGED2,
                                () -> WukongAnimations.SMASH_CHARGED3,
                                () -> WukongAnimations.SMASH_CHARGED4)
                        .setDeriveAnimations(
                                () -> WukongAnimations.SMASH_SPECIAL1,
                                () -> WukongAnimations.SMASH_SPECIAL2)
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY)
                , WukongMoveset.MOD_ID, "smash_charged");

        SkillManager.register(ThrustHeavyAttack::new, ThrustHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(() -> WukongAnimations.THRUST_PRE)
                        .setChargingAnimation(() -> WukongAnimations.THRUST_CHARGING)
                        .setHeavyAttacks(
                                () -> WukongAnimations.THRUST_CHARGED0,
                                () -> WukongAnimations.THRUST_CHARGED1,
                                () -> WukongAnimations.THRUST_CHARGED2,
                                () -> WukongAnimations.THRUST_CHARGED3,
                                () -> WukongAnimations.THRUST_CHARGED4)
                        .setDeriveAnimations(
                                () -> WukongAnimations.THRUST_RETREAT,
                                () -> WukongAnimations.THRUST_FOOTAGE,
                                () -> WukongAnimations.THRUST_JUESICK_LOOP,
                                () -> WukongAnimations.THRUST_JUESICK_END)
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY)
                , WukongMoveset.MOD_ID, "thrust_charged");

        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.SMASH).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "smash_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.THRUST).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "thrust_style");
//        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.PILLAR).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "pillar_style");

        SkillManager.register(CloudStepSkill::new, CloudStepSkill.createCloudStep().setCreativeTab(WukongItems.CREATIVE_MODE_TAB)
                .setAnim(() -> WukongAnimations.CLOUD_STEP_START, () -> WukongAnimations.CLOUD_STEP_START_BACKWARD, () -> WukongAnimations.CLOUD_STEP_END_FORWARD, () -> WukongAnimations.CLOUD_STEP_END_STOP), WukongMoveset.MOD_ID, "ju_xing_san_qi");
        SkillManager.register(TTTBSkill::new, TTTBSkill.createTTTB().setCreativeTab(WukongItems.CREATIVE_MODE_TAB)
                .setAnim(() -> WukongAnimations.TONG_TOU_TIE_BI, () -> WukongAnimations.TONG_TOU_TIE_BI_FAIL, () -> WukongAnimations.TONG_TOU_TIE_BI_END), WukongMoveset.MOD_ID, "tong_tou_tie_bi");
        SkillManager.register(DingSkill::new, DingSkill.createDing().setCreativeTab(WukongItems.CREATIVE_MODE_TAB),WukongMoveset.MOD_ID, "ding_shen_fa");
        SkillManager.register(ASFSkill::new, ASFSkill.create().setCreativeTab(WukongItems.CREATIVE_MODE_TAB),WukongMoveset.MOD_ID, "an_shen_fa");
        SkillManager.register(ShenWaiShenFaSkill::new, ShenWaiShenFaSkill.create().setCreativeTab(WukongItems.CREATIVE_MODE_TAB),WukongMoveset.MOD_ID, "shen_wai_shen_fa");
    }

    public static void BuildSkills(SkillBuildEvent event) {
        WUKONG_DODGE = event.build(WukongMoveset.MOD_ID, "dodge");
        STAFF_SPIN = event.build(WukongMoveset.MOD_ID, "staff_flower");

        SMASH_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "smash_charged");
        THRUST_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "thrust_charged");

        SMASH_STYLE = event.build(WukongMoveset.MOD_ID, "smash_style");
        THRUST_STYLE = event.build(WukongMoveset.MOD_ID, "thrust_style");
//        PILLAR_STYLE = event.build(WukongMoveset.MOD_ID, "pillar_style");

        JU_XING_SAN_QI = event.build(WukongMoveset.MOD_ID, "ju_xing_san_qi");
        TONG_TOU_TIE_BI = event.build(WukongMoveset.MOD_ID, "tong_tou_tie_bi");
        DING = event.build(WukongMoveset.MOD_ID, "ding_shen_fa");
        AN_SHEN_FA = event.build(WukongMoveset.MOD_ID, "an_shen_fa");
        SHEN_WAI_SHEN_FA = event.build(WukongMoveset.MOD_ID, "shen_wai_shen_fa");
    }

}

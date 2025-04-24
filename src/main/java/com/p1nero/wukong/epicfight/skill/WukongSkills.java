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
    // 定义各种技能对象
    public static Skill SMASH_STYLE_SHAN_MU;  // 自定义的技能
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill WUKONG_DODGE;

    // 魔法艺术技能
    public static Skill DING;
    public static Skill AN_SHEN_FA;
    public static Skill JU_XING_SAN_QI;
    public static Skill TONG_TOU_TIE_BI;
    public static Skill SHEN_WAI_SHEN_FA;

    // 注册所有技能
    public static void registerSkills() {

        // 注册闪避技能，设置不同方向的闪避动画
        SkillManager.register(WukongDodgeSkill::new, WukongDodgeSkill.createDodgeBuilder()
                        .setAnimations1(
                                () -> WukongAnimations.DODGE_F1, // 前闪避动画
                                () -> WukongAnimations.DODGE_B1, // 后闪避动画
                                () -> WukongAnimations.DODGE_L1, // 左闪避动画
                                () -> WukongAnimations.DODGE_R1  // 右闪避动画
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
                                () -> WukongAnimations.DODGE_FP,  // 完美闪避前动画
                                () -> WukongAnimations.DODGE_BP,  // 完美闪避后动画
                                () -> WukongAnimations.DODGE_LP,  // 完美闪避左动画
                                () -> WukongAnimations.DODGE_RP   // 完美闪避右动画
                        ).setCreativeTab(null),
                WukongMoveset.MOD_ID, "dodge");

        // 注册其他技能（如武器被动技能、重攻击技能等）
        SkillManager.register(StaffPassive::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");

        // 注册充能重攻击技能：Smash Heavy Attack
        SkillManager.register(SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(() -> WukongAnimations.SMASH_CHARGING_PRE) // 充能前动画
                        .setChargingAnimation(() -> WukongAnimations.SMASH_CHARGING_LOOP) // 充能循环动画
                        .setHeavyAttacks(
                                () -> WukongAnimations.SMASH_CHARGED0,
                                () -> WukongAnimations.SMASH_CHARGED1,
                                () -> WukongAnimations.SMASH_CHARGED2,
                                () -> WukongAnimations.SMASH_CHARGED3,
                                () -> WukongAnimations.SMASH_CHARGED4)
                        .setDeriveAnimations(
                                () -> WukongAnimations.SMASH_SPECIAL1, // 特殊攻击1
                                () -> WukongAnimations.SMASH_SPECIAL2) // 特殊攻击2
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY),  // 跳跃攻击重击
                WukongMoveset.MOD_ID, "smash_charged");

        // 注册充能重攻击技能：Thrust Heavy Attack
        SkillManager.register(ThrustHeavyAttack::new, ThrustHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(() -> WukongAnimations.THRUST_PRE) // 充能前动画
                        .setChargingAnimation(() -> WukongAnimations.THRUST_CHARGING) // 充能动画
                        .setHeavyAttacks(
                                () -> WukongAnimations.THRUST_CHARGED0,
                                () -> WukongAnimations.THRUST_CHARGED1,
                                () -> WukongAnimations.THRUST_CHARGED2,
                                () -> WukongAnimations.THRUST_CHARGED3,
                                () -> WukongAnimations.THRUST_CHARGED4)
                        .setDeriveAnimations(
                                () -> WukongAnimations.THRUST_RETREAT, // 后撤动画
                                () -> WukongAnimations.THRUST_FOOTAGE, // 镜头动画
                                () -> WukongAnimations.THRUST_JUESICK_LOOP, // 固定攻击循环动画
                                () -> WukongAnimations.THRUST_JUESICK_END) // 固定攻击结束动画
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY),
                WukongMoveset.MOD_ID, "thrust_charged");

        // 注册其他技能（如 Staff Stance、Cloud Step、TTTB 等）
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.SMASH).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "smash_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.THRUST).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "thrust_style");

        // 注册魔法艺术技能
        SkillManager.register(CloudStepSkill::new, CloudStepSkill.createCloudStep().setCreativeTab(WukongItems.CREATIVE_MODE_TAB)
                .setAnim(() -> WukongAnimations.CLOUD_STEP_START, () -> WukongAnimations.CLOUD_STEP_START_BACKWARD, () -> WukongAnimations.CLOUD_STEP_END_FORWARD, () -> WukongAnimations.CLOUD_STEP_END_STOP), WukongMoveset.MOD_ID, "ju_xing_san_qi");

        // 注册更多的技能
        SkillManager.register(TTTBSkill::new, TTTBSkill.createTTTB().setCreativeTab(WukongItems.CREATIVE_MODE_TAB)
                .setAnim(() -> WukongAnimations.TONG_TOU_TIE_BI, () -> WukongAnimations.TONG_TOU_TIE_BI_FAIL, () -> WukongAnimations.TONG_TOU_TIE_BI_END), WukongMoveset.MOD_ID, "tong_tou_tie_bi");

        // 注册其他技能，如 Ding、AnShenFa、ShenWaiShenFa
        SkillManager.register(DingSkill::new, DingSkill.createDing().setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "ding_shen_fa");
        SkillManager.register(ASFSkill::new, ASFSkill.create().setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "an_shen_fa");
        SkillManager.register(ShenWaiShenFaSkill::new, ShenWaiShenFaSkill.create().setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "shen_wai_shen_fa");
    }

    // 构建技能并赋值
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

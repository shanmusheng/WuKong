package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.*;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.StepSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.concurrent.atomic.AtomicInteger;

public class WukongSkills {
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill PILLAR_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill WUKONG_DODGE;
    public static Skill RED_TIDE_SKILL;
    public static Skill RED_TIDE_DODGE;
    public static Skill Ding;//定身术
    public static int getCurrentStack(Player player){
        AtomicInteger stack = new AtomicInteger(0);
        player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
            if(entityPatch instanceof PlayerPatch<?> patch){
                stack.set(patch.getSkill(SkillSlots.WEAPON_INNATE).getStack());
            }
        });
        return stack.get();
    }

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
                ).setCreativeTab(WukongItems.CREATIVE_MODE_TAB),
                WukongMoveset.MOD_ID, "dodge");
        SkillManager.register(StaffPassive::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");
        SkillManager.register(SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(()-> WukongAnimations.PILLAR_PRE0)
//                        .setChargePreAnimation(()-> WukongAnimations.SMASH_CHARGING_PRE)
                        .setChargingAnimation(()->WukongAnimations.SMASH_CHARGING_LOOP)
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
        SkillManager.register(PillarHeavyAttack::new, PillarHeavyAttack.createChargedAttack()
                        .setPreAnimations(()-> WukongAnimations.PILLAR_PRE0)
                        .setTransAnimations(()-> WukongAnimations.PILLAR_0_1)
                        .setHeavyAttacks(() -> WukongAnimations.PILLAR_CHARGED0)
                        .setDeriveAnimations(()-> WukongAnimations.PILLAR_PRE0, ()-> WukongAnimations.PILLAR_PRE0, ()-> WukongAnimations.PILLAR_PRE0, ()-> WukongAnimations.PILLAR_PRE0)
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY)
                , WukongMoveset.MOD_ID, "pillar_charged");
        SkillManager.register(ThrustHeavyAttack::new, ThrustHeavyAttack.createChargedAttack()
                .setChargePreAnimation(()-> WukongAnimations.THRUST_PRE)
                .setChargingAnimation(()->WukongAnimations.THRUST_CHARGING)
                .setAnimationProviders(
                        () -> WukongAnimations.THRUST_CHARGED0,
                        () -> WukongAnimations.THRUST_CHARGED1,
                        () -> WukongAnimations.THRUST_CHARGED2,
                        () -> WukongAnimations.THRUST_CHARGED3,
                        () -> WukongAnimations.THRUST_CHARGED4)
                .setDeriveAnimations(
                        () -> WukongAnimations.THRUST_DERIVE_PRE,
                        () -> WukongAnimations.THRUST_DERIVE2)
                .setCanChargeWhenMove(false)
                , WukongMoveset.MOD_ID, "thrust_charged");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.SMASH).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "smash_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.THRUST).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "thrust_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.PILLAR).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "pillar_style");

        SkillManager.register(RedTideHeavyAttack::new, WeaponInnateSkill.createWeaponInnateBuilder(), WukongMoveset.MOD_ID, "red_tide_heavy");
        SkillManager.register(StepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(WukongMoveset.MOD_ID, "cc/cc_dodge"), new ResourceLocation(WukongMoveset.MOD_ID, "cc/cc_dodge_b"), new ResourceLocation(WukongMoveset.MOD_ID, "cc/cc_dodge_l"), new ResourceLocation(WukongMoveset.MOD_ID, "cc/cc_dodge_r")), WukongMoveset.MOD_ID, "red_tide_dodge");
    }


    public static void BuildSkills(SkillBuildEvent event){
        WUKONG_DODGE = event.build(WukongMoveset.MOD_ID, "dodge");
        STAFF_SPIN = event.build(WukongMoveset.MOD_ID, "staff_flower");

        SMASH_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "smash_charged");
        THRUST_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "thrust_charged");
        PILLAR_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "pillar_charged");

        SMASH_STYLE = event.build(WukongMoveset.MOD_ID, "smash_style");
        THRUST_STYLE = event.build(WukongMoveset.MOD_ID, "thrust_style");
        PILLAR_STYLE = event.build(WukongMoveset.MOD_ID, "pillar_style");

        RED_TIDE_SKILL = event.build(WukongMoveset.MOD_ID, "red_tide_heavy");
        RED_TIDE_DODGE = event.build(WukongMoveset.MOD_ID, "red_tide_dodge");
    }

}

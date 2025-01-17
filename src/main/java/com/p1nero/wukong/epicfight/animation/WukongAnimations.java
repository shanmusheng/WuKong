package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.events.CameraAnim;
import com.p1nero.wukong.epicfight.animation.custom.*;
import com.p1nero.wukong.epicfight.skill.custom.PillarHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.RedTideHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.client.player.Input;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.DodgeLeft;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {

    public static StaticAnimation IDLE;
    public static StaticAnimation WALK;
    public static StaticAnimation RUN_F;
    public static StaticAnimation RUN;
    public static StaticAnimation DASH;
    public static StaticAnimation JUMP;
    public static StaticAnimation FALL;
    public static StaticAnimation JUMP_ATTACK_LIGHT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_HIT;
    public static StaticAnimation JUMP_ATTACK_HEAVY;
    public static StaticAnimation DODGE_F1;
    public static StaticAnimation DODGE_F2;
    public static StaticAnimation DODGE_F3;
    public static StaticAnimation DODGE_FP;
    public static StaticAnimation DODGE_B1;
    public static StaticAnimation DODGE_B2;
    public static StaticAnimation DODGE_B3;
    public static StaticAnimation DODGE_BP;
    public static StaticAnimation DODGE_L1;
    public static StaticAnimation DODGE_L2;
    public static StaticAnimation DODGE_L3;
    public static StaticAnimation DODGE_LP;
    public static StaticAnimation DODGE_R1;
    public static StaticAnimation DODGE_R2;
    public static StaticAnimation DODGE_R3;
    public static StaticAnimation DODGE_RP;
    //棍花
    public static StaticAnimation STAFF_SPIN_ONE_HAND_LOOP;
    public static StaticAnimation STAFF_SPIN_TWO_HAND_LOOP;

    //轻击 1~5
    public static StaticAnimation STAFF_AUTO1_DASH;
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;

    //劈棍
    //衍生 1 2
    public static StaticAnimation SMASH_SPECIAL1;
    public static StaticAnimation SMASH_SPECIAL2;
    public static StaticAnimation SMASH_CHARGING_PRE;
    public static StaticAnimation SMASH_CHARGING_LOOP;
    public static StaticAnimation SMASH_CHARGING_LOOP_STAND;
    //不同星级的重击
    public static StaticAnimation SMASH_CHARGED0;
    public static StaticAnimation SMASH_CHARGED1;
    public static StaticAnimation SMASH_CHARGED2;
    public static StaticAnimation SMASH_CHARGED3;
    public static StaticAnimation SMASH_CHARGED4;

    //戳棍
    //不同星级的重击
    public static StaticAnimation THRUST_PRE;
    public static StaticAnimation THRUST_CHARGING;
    public static StaticAnimation THRUST_CHARGED0;
    public static StaticAnimation THRUST_CHARGED1;
    public static StaticAnimation THRUST_CHARGED2;
    public static StaticAnimation THRUST_CHARGED3;
    public static StaticAnimation THRUST_CHARGED4;//四豆
    public static StaticAnimation THRUST_JUESICK_END;//搅棍结束
    public static StaticAnimation THRUST_JUESICK_START;//搅棍起始
    public static StaticAnimation THRUST_JUESICK_LOOP;//搅棍循环
    public static StaticAnimation THRUST_FOOTAGE;//进尺
    public static StaticAnimation THRUST_RETREAT;//退寸

    //立棍
    //衍生 1 2
    public static StaticAnimation PILLAR_DERIVE1_PRE;
    public static StaticAnimation PILLAR_DERIVE1_LOOP;
    public static StaticAnimation PILLAR_DERIVE1_POST;
    public static StaticAnimation PILLAR_DERIVE2;
    //劈下去
    public static StaticAnimation PILLAR_CHARGED0;
    public static StaticAnimation PILLAR_CHARGED1;
    public static StaticAnimation PILLAR_CHARGED2;
    public static StaticAnimation PILLAR_CHARGED3;
    public static StaticAnimation PILLAR_CHARGED4;
    //升起来
    public static StaticAnimation PILLAR_PRE0;
    public static StaticAnimation PILLAR_PRE1;
    public static StaticAnimation PILLAR_PRE2;
    public static StaticAnimation PILLAR_PRE3;
    public static StaticAnimation PILLAR_PRE4;

    //最高点循环， 0同1， 3同4
    public static StaticAnimation PILLAR_LOOP;

    //聚形散气
    public static StaticAnimation CLOUD_STEP_START;
    public static StaticAnimation CLOUD_STEP_END;

    //赤潮
    public static StaticAnimation RED_TIDE_IDLE;
    public static StaticAnimation RED_TIDE_WALK;
    public static StaticAnimation RED_TIDE_RUN;
    public static StaticAnimation RED_TIDE_JUMP;
    public static StaticAnimation RED_TIDE_JUMP_ATTACK;
    public static StaticAnimation RED_TIDE_AUTO1;
    public static StaticAnimation RED_TIDE_AUTO2;
    public static StaticAnimation RED_TIDE_AUTO3;
    public static StaticAnimation RED_TIDE_AUTO4;
    public static StaticAnimation RED_TIDE_DODGE;
    public static StaticAnimation RED_TIDE_DODGE_B;
    public static StaticAnimation RED_TIDE_DODGE_L;
    public static StaticAnimation RED_TIDE_DODGE_R;
    public static StaticAnimation RED_TIDE_SKILL_F;
    public static StaticAnimation RED_TIDE_SKILL;

    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(WukongMoveset.MOD_ID, WukongAnimations::build);
    }

    private static void build() {
        HumanoidArmature biped = Armatures.BIPED;

        //专治各种因为移动导致的动画取消
        AnimationEvent.TimePeriodEvent allStopMovement = AnimationEvent.TimePeriodEvent.create(0.00F, Float.MAX_VALUE, ((livingEntityPatch, staticAnimation, objects) -> {
            if (livingEntityPatch instanceof LocalPlayerPatch localPlayerPatch) {
                Input input = localPlayerPatch.getOriginal().input;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                localPlayerPatch.getOriginal().setSprinting(false);
            }
        }), AnimationEvent.Side.CLIENT);

        IDLE = new StaticAnimation(true, "biped/idle", biped);

        WALK = new StaticAnimation(true, "biped/walk", biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        RUN_F = new StaticAnimation(true, "biped/run", biped);

        RUN = new SelectiveAnimation((entityPatch) -> {
            Vec3 view = entityPatch.getOriginal().getViewVector(1.0F);
            Vec3 move = entityPatch.getOriginal().getDeltaMovement();
            double dot = view.dot(move);
            return dot < 0.0 ? 1 : 0;
        }, RUN_F, WALK);

        DASH = new StaticAnimation(true, "biped/dash", biped);

        JUMP = new StaticAnimation(0.15F, false, "biped/jump", biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        FALL = new StaticAnimation(0.15F, true, "biped/fall", biped);

        DODGE_F1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f1", 0.6F, 0.8F, biped);

        DODGE_B1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b1", 0.6F, 0.8F, biped);

        DODGE_R1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r1", 0.6F, 0.8F, biped);

        DODGE_L1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l1", 0.6F, 0.8F, biped);

        DODGE_F2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f2", 0.6F, 0.8F, biped);

        DODGE_B2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b2", 0.6F, 0.8F, biped);

        DODGE_R2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r2", 0.6F, 0.8F, biped);

        DODGE_L2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l2", 0.6F, 0.8F, biped);

        DODGE_F3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_f3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);

        DODGE_B3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_b3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);

        DODGE_R3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_r3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);

        DODGE_L3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_l3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true);

        DODGE_FP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_fp", 0.6F, 1.35F, biped, true);

        DODGE_BP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_bp", 0.6F, 1.35F, biped, true);

        DODGE_RP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_rp", 0.6F, 1.35F, biped, true);

        DODGE_LP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_lp", 0.6F, 1.35F, biped, true);

        STAFF_AUTO1_DASH = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR, "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                                //冲刺攻击重置普攻计数器
                                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
                            }
                        }), AnimationEvent.Side.SERVER));

        STAFF_AUTO1 = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR, "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));

        STAFF_AUTO2 = new BasicAttackAnimation(0.15F, 0.6667F, 0.875F, 0.875F, null, biped.toolR, "biped/auto_2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));

        STAFF_AUTO3 = new BasicAttackAnimation(0.15F, "biped/auto_3", biped,
                new AttackAnimation.Phase(0.0F, 0.25F, 0.4583F, 0.4583F, 0.4583F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)),
                new AttackAnimation.Phase(0.4583F, 0.4583F, 0.7083F, 0.7083F, 3.3333F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        STAFF_AUTO4 = new BasicAttackAnimation(0.15F, "biped/auto_4", biped,
                new AttackAnimation.Phase(0.0F, 0.1F, 0.2F, 0.2F, 0.2F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.2F, 0.2F, 0.4F, 0.4F, 0.4F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.4F, 0.4F, 0.6F, 0.6F, 0.6F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.6F, 0.6F, 0.8F, 0.8F, 0.8F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.8F, 1.0416F, 1.125F, 1.2583F, 2.5F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5F)))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.125F, ((livingEntityPatch, staticAnimation, objects) -> {
                            LivingEntity self = livingEntityPatch.getOriginal();
                            if (self.getMainHandItem().is(WukongItems.KANG_JIN.get()) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, self.getMainHandItem()) > 0) {
                                if (livingEntityPatch.getTarget() != null && self.level instanceof ServerLevel serverLevel) {
                                    EntityType.LIGHTNING_BOLT.spawn(serverLevel, null, null, livingEntityPatch.getTarget().getOnPos(), MobSpawnType.TRIGGERED, false, false);
                                }
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.125F, ((livingEntityPatch, staticAnimation, objects) -> {
                            LivingEntity self = livingEntityPatch.getOriginal();
                            if (self.getMainHandItem().is(WukongItems.KANG_JIN.get()) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, self.getMainHandItem()) > 0) {
                                //TODO 粒子缠身
                            }
                        }), AnimationEvent.Side.SERVER));
        STAFF_AUTO5 = new WukongScaleStaffAttackAnimation(0.01F, 0.9166F, 1.15F, 1.9833F, null, biped.toolR, "biped/auto_5", biped, 0.5F) {
            @Override
            public boolean isBasicAttackAnimation() {
                return true;
            }
        }
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.9833F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 1, 1)), AnimationEvent.Side.SERVER));

        JUMP_ATTACK_LIGHT = new WukongJumpAttackAnimation(0.10F, 0.13F, 0.40F, 0.50F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/jump_attack/jump_light_pre", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.45F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))//最多踹一个
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.10F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));

        JUMP_ATTACK_LIGHT_HIT = new ActionAnimation(0.15F, "biped/jump_attack/jump_light_hit", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.3F))
                .addState(EntityState.CAN_SKILL_EXECUTION, true)//为了可以用重击取消后摇
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));

        JUMP_ATTACK_HEAVY = new WukongScaleStaffAttackAnimation(0.01F, 0.54F, 0.67F, 1.25F, null, biped.toolR, "biped/jump_attack/jump_heavy", biped, 0.5F)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.67F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ROLL, 1, 1)), AnimationEvent.Side.SERVER));

        STAFF_SPIN_ONE_HAND_LOOP = new StaffSpinAttackAnimation(1.25F, biped, "biped/staff_spin/staff_spin_one_hand", 0.05F, false);

        STAFF_SPIN_TWO_HAND_LOOP = new StaffSpinAttackAnimation(0.83F, biped, "biped/staff_spin/staff_spin_two_hand", 0.08F, true);

        //劈start
        //前摇完自动接下一个动作
        SMASH_CHARGING_PRE = new ActionAnimation(0.15F, "biped/smash/smash_charge_pre", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(SMASH_CHARGING_LOOP_STAND);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER));

        SMASH_CHARGING_LOOP_STAND = new StaticAnimation(0.15F, true, "biped/smash/smash_charging", biped);

        SMASH_CHARGED0 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR, "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));

        SMASH_CHARGED1 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR, "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));

        SMASH_CHARGED2 = new WukongScaleStaffAttackAnimation(0.15F, 1.30F, 1.55F, 2.5F, WukongColliders.STACK_2, biped.toolR, "biped/smash/smash_heavy2", biped, 0.5F)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8.8F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F))
//                .newTimePair(0, 1.30F)
//                .addState(EntityState.TURNING_LOCKED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        List<AnimationEvent.TimeStampedEvent> sc2List = append(
                AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.reset(1.30F),
                        ScaleTime.of(1.15F, 1, 1.8F, 1),
                        ScaleTime.of(2.13F, 1, 1.8F, 1),
                        ScaleTime.reset(2.29F)
                )
        );
        sc2List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED2
                .addEvents(sc2List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED3 = new WukongScaleStaffAttackAnimation(0.15F, 1.792F, 1.958F, 2.667F, WukongColliders.STACK_3, biped.toolR, "biped/smash/smash_heavy3", biped, 0.7F)
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(6.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(11))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
//                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG,  Set.of(SourceTags.WEAPON_INNATE, SourceTags.GUARD_PUNCTURE))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F))
//                .newTimePair(0, 1.792F)
//                .addState(EntityState.TURNING_LOCKED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        List<AnimationEvent.TimeStampedEvent> sc3List = append(
                AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.reset(1.667F),
                        ScaleTime.of(1.392F, 1, 2.4F, 1),
                        ScaleTime.of(1.958F, 1, 2.4F, 1),
                        ScaleTime.of(2.667F, 1, 2F, 1),
                        ScaleTime.reset(2.8F)
                )
        );
        sc3List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        sc3List.add(AnimationEvent.TimeStampedEvent.create(1.125F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED3.addEvents(sc3List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED4 = new WukongScaleStaffAttackAnimation(0.15F, 2.63F, 2.8F, 3.3F, WukongColliders.STACK_4, biped.toolR, "biped/smash/smash_heavy4", biped, 0.9F)
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(10.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(8.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SOURCE_TAG, Set.of(SourceTags.WEAPON_INNATE, SourceTags.GUARD_PUNCTURE))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 2.75F))
//                .newTimePair(0, 2.63F)
//                .addState(EntityState.TURNING_LOCKED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.3F));
        List<AnimationEvent.TimeStampedEvent> sc4List = append(
                AnimationEvent.TimeStampedEvent.create(0.208F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(2.4167F, 1, 1, 1),
                        ScaleTime.of(2.5833F, 1F, 1.15F, 1F),
                        ScaleTime.of(2.6083F, 1.5F, 3.15F, 1.15F),
                        ScaleTime.of(3.3333F, 1.5F, 3.15F, 1.5F),
                        ScaleTime.of(3.5833F, 1, 1, 1)
                )
        );
        sc4List.add(AnimationEvent.TimeStampedEvent.create(2.7F, ((livingEntityPatch, staticAnimation, objects) -> {
            LivingEntity entity = livingEntityPatch.getOriginal();
            Vec3 viewVec = entity.getViewVector(1.0F);
            Vec3 hVec = viewVec.add(0, -viewVec.y, 0);
            Vec3 target = entity.position().add(hVec.normalize().scale(4)).add(0, -2, 0);
            LevelUtil.circleSlamFracture(entity, entity.level, target, 3.0);
        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED4.addEvents(sc4List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_SPECIAL1 = new WukongScaleStaffAttackAnimation(0.15F, 0.63F, 0.75F, 1.20F, null, biped.toolR, "biped/smash/smash_special1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .newTimePair(0.01F, 0.4F)
                .addState(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.BLOCKED)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        dataManager.setDataSync(SmashHeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                        dataManager.setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, true, serverPlayerPatch.getOriginal());
                        dataManager.setDataSync(SmashHeavyAttack.IS_SPECIAL_ATTACK_SUCCESS, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, false, serverPlayerPatch.getOriginal());
                                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_SPECIAL_ATTACK_SUCCESS, false, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER)
                )
                .addEvents(
                        append(
                                AnimationEvent.TimeStampedEvent.create(0.4F, ((livingEntityPatch, anim, obj) -> {
                                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, false, serverPlayerPatch.getOriginal());
                                    }
                                }), AnimationEvent.Side.SERVER),
                                getScaleEvents(
                                        ScaleTime.of(0.625F, 1, 1.4F, 1),
                                        ScaleTime.of(1.125F, 1, 1.4F, 1),
                                        ScaleTime.reset(1.25F)
                                )
                        ).toArray(new AnimationEvent.TimeStampedEvent[0])
                );

        SMASH_SPECIAL2 = new WukongScaleStaffAttackAnimation(0.15F, 1.04F, 1.71F, 2.30F, null, biped.toolR, "biped/smash/smash_special2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.04F))
                .newTimePair(0.01F, 1.71F)
                .addState(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.MISSED)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(
                        append(
                                AnimationEvent.TimeStampedEvent.create(0.042F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                                getScaleEvents(
                                        ScaleTime.of(0.083F, 1, 1.8F, 1),
                                        ScaleTime.of(1.458F, 1, 1.8F, 1),
                                        ScaleTime.reset(1.460F)
                                )
                        ).toArray(new AnimationEvent.TimeStampedEvent[0])
                );
        //劈end

        //立start
        PILLAR_LOOP = new PillarLoopedActionAnimation(0.15F, "biped/pillar/loop", biped);

        PILLAR_PRE0 = new ActionAnimation(0.15F, "biped/pillar/up0", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.8333F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(PillarHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

        PILLAR_PRE1 = new ActionAnimation(0.15F, "biped/pillar/up1", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.8333F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

        PILLAR_PRE2 = new ActionAnimation(0.15F, "biped/pillar/up2", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.8333F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

        PILLAR_PRE3 = new ActionAnimation(0.15F, "biped/pillar/up3", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.8333F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

        PILLAR_PRE4 = new ActionAnimation(0.15F, "biped/pillar/up4", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.8333F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(PILLAR_LOOP);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

        PILLAR_CHARGED0 = new WukongScaleStaffAttackAnimation(0.15F, 0.366666F, 0.46666F, 1.3F, WukongColliders.STACK_0_1, biped.toolR, "biped/pillar/stick_heavy_a0", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.26666F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.1F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.PERFECT_DODGE.get(), 1, 1)), AnimationEvent.Side.SERVER));

        PILLAR_CHARGED1 = new WukongScaleStaffAttackAnimation(0.15F, 0.366666F, 0.46666F, 1.3F, WukongColliders.STACK_0_1, biped.toolR, "biped/pillar/stick_heavy_a1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.75F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.26666F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.3F))
                .addEvents(append(
                        AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.PERFECT_DODGE.get(), 1, 1)), AnimationEvent.Side.SERVER),
                        getScaleEvents(
                                ScaleTime.of(0F, 1, 1.5F, 1F, 0F, 0.7F, 0F),
                                ScaleTime.of(1.96666F, 1, 1F, 1F, 0F, 0F, 0F),
                                ScaleTime.reset(1.96666F)
                        )).toArray(new AnimationEvent.TimeStampedEvent[0])
                );

        PILLAR_CHARGED2 = new WukongScaleStaffAttackAnimation(0.15F, 0.433333F, 0.53333f, 1.3F, WukongColliders.STACK_2, biped.toolR, "biped/pillar/stick_heavy_a2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.75f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 0.26666F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addEvents(append(
                        AnimationEvent.TimeStampedEvent.create(0F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                        getScaleEvents(
                                ScaleTime.of(0F, 1, 2.2F, 1F, 0F, 0.9F, 0F),
                                ScaleTime.of(2.6333F, 1, 1F, 1F, 0F, 0F, 0F),
                                ScaleTime.reset(2.6333F)
                        )).toArray(new AnimationEvent.TimeStampedEvent[0])
                );

        PILLAR_CHARGED3 = new WukongScaleStaffAttackAnimation(0.15F, 3.3F, 5F, 4.5F, WukongColliders.STACK_3, biped.toolR, "biped/pillar/stick_heavy_a3", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(7.25f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 3.2F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entityPatch, speed, elapsedTime) -> {
                    if (elapsedTime > 3.2F && elapsedTime < 3.4f) {
                        return 3F * speed;
                    }
                    return 1.2F * speed;
                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        PILLAR_CHARGED4 = new BasicMultipleAttackAnimation(0.15F, "biped/pillar/stick_heavy_a5", biped,
                new AttackAnimation.Phase(0.0F, 1.3666F, 1.5666F, 5.93333F, 1.6666F, biped.toolR, WukongColliders.STACK_4)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)),
                new AttackAnimation.Phase(1.6666F, 2.6666F, 2.7666F, 5.93333F, 2.8333F, biped.toolR, WukongColliders.STACK_4)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)),
                new AttackAnimation.Phase(2.8333F, 3f, 3.16666F, 5.93333F, 3.26666F, biped.toolR, WukongColliders.STACK_4)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2f)),
                new AttackAnimation.Phase(3.26666F, 3.933f, 4F, 5.93333F, 5.93333F, biped.toolR, WukongColliders.STACK_4)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(13f)))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0F, 4.4666F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> {
                    if (elapsedTime > 1.6333F && elapsedTime < 2.2f) {
                        return 3F * speed;
                    }
                    return 1.2F * speed;
                })
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        List<AnimationEvent.TimeStampedEvent> scList5 = append(
                AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(0F, 1, 1F, 1F, 0F, 0F, 0F),
                        ScaleTime.of(1.3333F, 1, 1.2F, 1F, 0F, -0.2F, 0F),
                        ScaleTime.of(2.13333F, 1, 2.0F, 1F, 0F, -1.2F, 0F),
                        ScaleTime.reset(5.93333F)
                )
        );
        scList5.add(AnimationEvent.TimeStampedEvent.create(1.5666F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, 0F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 3D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(2.8333F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 2D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(3.066F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 4D, 0.01F));
        scList5.add(AnimationEvent.TimeStampedEvent.create(3.966666F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, -5F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 7D, 0.01F));
        PILLAR_CHARGED4.addEvents(scList5.toArray(new AnimationEvent.TimeStampedEvent[0]));

        //立end

        //戳start
        THRUST_CHARGING = new StaticAnimation(true, "biped/thrust/thrust_xuli_loop", biped)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addStateRemoveOld(EntityState.INACTION, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));

        THRUST_PRE = new ActionAnimation(0F, "biped/thrust/thrust_xuli_start", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 2.8F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(THRUST_CHARGING);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER));
        ;


        THRUST_CHARGED0 = new WukongScaleStaffAttackAnimation(0F, 0.5F, 0.733F, 2.766F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_heavy0", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));

        THRUST_CHARGED1 = new WukongScaleStaffAttackAnimation(0F, 0.5F, 0.733F, 2.766F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_heavy1", biped, 0.2F)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(4F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));

        THRUST_CHARGED2 = new WukongScaleStaffAttackAnimation(0F, 0.6F, 0.733F, 2.766F, WukongColliders.THRUST_STACK_2, biped.toolR, "biped/thrust/thrust_heavy2", biped, 0.3F)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(6.25F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));
        List<AnimationEvent.TimeStampedEvent> charged2 = append(
                AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(0F, 1, 1F, 1F),
                        ScaleTime.of(0.2F, 1, 1.5F, 1F, 0F, -1F, 0F),
                        ScaleTime.of(1F, 1, 2F, 1F, 0F, -1F, 0F),
                        ScaleTime.reset(1.333F)
                )
        );
        THRUST_CHARGED2.addEvents(charged2.toArray(new AnimationEvent.TimeStampedEvent[0]));

        THRUST_CHARGED3 = new WukongScaleStaffAttackAnimation(0F, 0.56F, 0.833F, 3.33F, WukongColliders.THRUST_STACK_3, biped.toolR, "biped/thrust/thrust_heavy3", biped, 0.4F)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        List<AnimationEvent.TimeStampedEvent> charged3 = append(
                AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(0F, 1, -1F, 1F),
                        ScaleTime.of(0.266F, 1, 2F, 1F, 0F, -1F, 0F),
                        ScaleTime.of(0.333F, 1, 2.5F, 1F, 0F, -1F, 0F),
                        ScaleTime.of(0.5F, 1, 3F, 1F, 0F, -1F, 0F),
                        ScaleTime.of(1F, 1, 3F, 1F, 0F, -1F, 0F),
                        ScaleTime.of(1.9F, 1, 1F, 1F, 0F, -1F, 0F),
                        ScaleTime.reset(2F)
                )
        );
        THRUST_CHARGED3.addEvents(charged3.toArray(new AnimationEvent.TimeStampedEvent[0]));

        THRUST_CHARGED4 = new WukongScaleStaffAttackAnimation(0.0F, 0.5F, 0.6F, 2.4F, WukongColliders.THRUST_STACK_4, biped.toolR, "biped/thrust/thrust_heavy4", biped, 0.7F)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.6F))
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(8))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(50F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.2F, 0.766F))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, speed, elapsedTime) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (dataManager.hasData(ThrustHeavyAttack.FENGCHUANHUA_TIMER) && dataManager.getDataValue(ThrustHeavyAttack.FENGCHUANHUA_TIMER) > 0 && elapsedTime < 0.5F) {
                            return 1.5F;//有凤穿花前摇播快一点
                        }
                    }
                    return 1.3F;
                }));
        List<AnimationEvent.TimeStampedEvent> charged4 = append(
                AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.of(0F, 1, 1F, 1F),
                        ScaleTime.of(0.8F, 1, 2.3F, 1F, 0F, -1.7F, 0F),
                        ScaleTime.of(1.0F, 1F, 4F, 1F, 0F, -1.7F, 0F),
                        ScaleTime.of(1.1666F, 1F, 4F, 1F, 0F, -1.7F, 0F),
                        ScaleTime.reset(1.8F)
                )
        );
//        charged4.add(AnimationEvent.TimeStampedEvent.create(0.833F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -5F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 1D, 0.01F));
//        charged4.add(AnimationEvent.TimeStampedEvent.create(0.933F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -8F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 2D, 0.01F));
//        charged4.add(AnimationEvent.TimeStampedEvent.create(1.1666F, yesman.epicfight.gameasset.Animations.ReusableSources.FRACTURE_GROUND_SIMPLE, AnimationEvent.Side.CLIENT).params(new Vec3f(0F, 0F, -13F), yesman.epicfight.gameasset.Armatures.BIPED.rootJoint, 3D, 0.01F));
        THRUST_CHARGED4.addEvents(charged4.toArray(new AnimationEvent.TimeStampedEvent[0]));

        //退寸
        THRUST_RETREAT = new ActionAnimation(0.15F, 2.0F, "biped/thrust/thrust_retreat", biped) {
            public void begin(LivingEntityPatch<?> entityPatch) {
                super.begin(entityPatch);
                if (!entityPatch.isLogicalClient()) {
                    entityPatch.getOriginal().getLevel().addFreshEntity(new DodgeLeft(entityPatch));
                }
            }

            public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
                super.end(entityPatch, nextAnimation, isEnd);
                if (entityPatch.isLogicalClient() && entityPatch instanceof LocalPlayerPatch localPlayerPatch) {
                    localPlayerPatch.changeModelYRot(0.0F);
                }
            }
        }
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .newTimePair(0.7F, Float.MAX_VALUE)//0.5s后才可以进尺
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
                .newTimePair(0.0F, 0.7F)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.6F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.REPEATING_DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER), AnimationEvent.TimeStampedEvent.create(0.15F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 0.0F, 0.0F)), AnimationEvent.Side.SERVER));

        //进尺
        THRUST_FOOTAGE = new WukongScaleStaffAttackAnimation(0.15F, 0.8F, 1.0F, 1.5F, WukongColliders.STACK_3, biped.toolR, "biped/thrust/thrust_footage", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.92F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {}))//覆盖掉
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
//                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 0.0F, 0.0F)), AnimationEvent.Side.SERVER))
                .addEvents(append(AnimationEvent.TimeStampedEvent.create(1.4F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.REPEATING_DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER), getScaleEvents(
                        ScaleTime.reset(0.0F),
                        ScaleTime.of(1.2F, 1, 1F, 1F,0F, 2.0f, 0F),
                        ScaleTime.of(1.3F, 1, 1.4F, 1F, 0F, 2.3F, 0F),
                        ScaleTime.of(1.5F, 1, 1.4F, 1F, 0F, 2.5F, 0F),
                        ScaleTime.reset(1.8F)
                )).toArray(new AnimationEvent.TimeStampedEvent[0]));

        THRUST_JUESICK_LOOP = new WukongScaleStaffAttackAnimation(0.15F, "biped/thrust/thrust_juesick_loop", biped,
                new AttackAnimation.Phase(0.0F, 0F, 0.333F, 1.333F, 0.333F, biped.toolR, WukongColliders.THRUST_JUESICK_LOOP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)),
                new AttackAnimation.Phase(0.333F, 0.333F, 0.666F, 1.333F, 0.666F, biped.toolR, WukongColliders.THRUST_JUESICK_LOOP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)),
                new AttackAnimation.Phase(0.666F, 0.666F, 1F, 1.333F, 1F, biped.toolR, WukongColliders.THRUST_JUESICK_LOOP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)),
                new AttackAnimation.Phase(1F, 1F, 1.333F, 1.333F, 1.333F, biped.toolR, WukongColliders.THRUST_JUESICK_LOOP)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.672f)))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.getDataValue(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN)) {
                            serverPlayerPatch.reserveAnimation(THRUST_JUESICK_LOOP);
                            serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.CAN_FIRST_DERIVE, true, serverPlayerPatch.getOriginal());
                            serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                        }
                    }
                }), AnimationEvent.Side.SERVER), AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.isLogicalClient()) {
                        CameraAnim.zoomOut(20);
                    }
                }), AnimationEvent.Side.CLIENT))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof LocalPlayerPatch localPlayerPatch && !localPlayerPatch.isTargetLockedOn()) {
                        CameraAnim.zoomIn(new Vec3f(-1.0F, 0.0F, 1.25F), 20);
                    }
                }), AnimationEvent.Side.CLIENT))
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.0F, 1, 1.2F, 1, 0F, 1.3F, 0F),
                        ScaleTime.of(1.32F, 1, 1.2F, 1, 0F, 1.3F, 0F)
                ));

        THRUST_JUESICK_START = new BasicAttackAnimation(0.15F, 0.866F, 1.0F, 1.6666F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_juesick_start", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.68f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.7F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(THRUST_JUESICK_LOOP);
                }), AnimationEvent.Side.SERVER));

        THRUST_JUESICK_END = new WukongScaleStaffAttackAnimation(0F, 0F, 0F, 0.9f, null, biped.toolR, "biped/thrust/thrust_juesick_end", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        //戳end

        //聚形散气
        CLOUD_STEP_START = new ActionAnimation(0.15F, 0.6F, "biped/magicarts/jxsq_start", biped);

        CLOUD_STEP_END = new AttackAnimation(0.15F, 0.6F, 0.15F, 0.8F, 1.6F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/magicarts/jxsq_end", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_UPDATE_TIME, TimePairList.create(0.0F,0.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_DEST_LOCATION_BEGIN)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_DEST_LOCATION)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_GET, MoveCoordFunctions.WORLD_COORD)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.99F));

        //赤潮
        RED_TIDE_IDLE = new StaticAnimation(true, "cc/cc_idle", biped);

        RED_TIDE_WALK = new StaticAnimation(true, "cc/cc_walk", biped);

        RED_TIDE_RUN = new StaticAnimation(true, "cc/cc_run", biped);

        RED_TIDE_JUMP = new StaticAnimation(true, "cc/cc_jump", biped);

        RED_TIDE_DODGE = new AttackAnimation(0.15F, 0.2916F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolL, "cc/cc_dodge", biped)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .newTimePair(0.0F, 0.5F)//无敌时间
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (source -> AttackResult.ResultType.MISSED))
                .newTimePair(0.0F, 1.5F)//禁用技能，播完才能
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addState(EntityState.LOCKON_ROTATE, true)
                .addState(EntityState.MOVEMENT_LOCKED, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof PlayerPatch<?> patch) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(RedTideHeavyAttack.COUNTER)) {
                            manager.setData(RedTideHeavyAttack.COUNTER, Config.DERIVE_CHECK_TIME.get().intValue());
                        }
                    }
                }), AnimationEvent.Side.BOTH));//无敌时间
        RED_TIDE_DODGE_B = new AttackAnimation(0.15F, 0.2916F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolL, "cc/cc_dodge_b", biped)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .newTimePair(0.0F, 0.5F)//无敌时间
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (source -> AttackResult.ResultType.MISSED))
                .newTimePair(0.0F, 1.5F)//禁用技能，播完才能
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addState(EntityState.LOCKON_ROTATE, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof PlayerPatch<?> patch) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(RedTideHeavyAttack.COUNTER)) {
                            manager.setData(RedTideHeavyAttack.COUNTER, Config.DERIVE_CHECK_TIME.get().intValue());
                        }
                    }
                }), AnimationEvent.Side.BOTH));//无敌时间
        RED_TIDE_DODGE_L = new AttackAnimation(0.15F, 0.2916F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolL, "cc/cc_dodge_l", biped)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .newTimePair(0.0F, 0.5F)//无敌时间
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (source -> AttackResult.ResultType.MISSED))
                .newTimePair(0.0F, 1.5F)//禁用技能，播完才能
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addState(EntityState.LOCKON_ROTATE, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof PlayerPatch<?> patch) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(RedTideHeavyAttack.COUNTER)) {
                            manager.setData(RedTideHeavyAttack.COUNTER, Config.DERIVE_CHECK_TIME.get().intValue());
                        }
                    }
                }), AnimationEvent.Side.BOTH));//无敌时间
        RED_TIDE_DODGE_R = new AttackAnimation(0.15F, 0.2916F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolL, "cc/cc_dodge_r", biped)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .newTimePair(0.0F, 0.5F)//无敌时间
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (source -> AttackResult.ResultType.MISSED))
                .newTimePair(0.0F, 1.5F)//禁用技能，播完才能
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addState(EntityState.LOCKON_ROTATE, true)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof PlayerPatch<?> patch) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(RedTideHeavyAttack.COUNTER)) {
                            manager.setData(RedTideHeavyAttack.COUNTER, Config.DERIVE_CHECK_TIME.get().intValue());
                        }
                    }
                }), AnimationEvent.Side.BOTH));//无敌时间
        RED_TIDE_SKILL_F = new AttackAnimation(0.15F, 1.5F, 1.5F, 1.7F, 3, null, biped.toolL, "cc/cc_skill_f", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.0F, 1.133F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.9F));
        RED_TIDE_SKILL = new AttackAnimation(0.15F, 1.17F, 1.17F, 1.83F, 2.3F, null, biped.toolL, "cc/cc_skill", biped)
                .addProperty(AnimationProperty.AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        RED_TIDE_AUTO1 = new BasicAttackAnimation(0.15F, 0.6F, 1, 1.3F, null, biped.toolL, "cc/cc_auto1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        RED_TIDE_AUTO2 = new BasicAttackAnimation(0.15F, 1.17F, 1.5F, 2, null, biped.toolL, "cc/cc_auto2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        RED_TIDE_AUTO3 = new BasicMultipleAttackAnimation(0.15F, "cc/cc_auto3", biped,
                new AttackAnimation.Phase(0.0F, 0.33F, 0.53F, 0.63F, 0.63F, biped.toolR, null),
                new AttackAnimation.Phase(0.63F, 0.63F, 0.9F, 1.0F, 1.0F, biped.toolR, null),
                new AttackAnimation.Phase(1, 1.0F, 1.2F, 2.16F, 2.16F, biped.toolR, null))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        RED_TIDE_AUTO4 = new BasicMultipleAttackAnimation(0.15F, "cc/cc_auto4", biped,
                new AttackAnimation.Phase(0.0F, 0.3F, 0.77F, 0.77F, 0.77F, biped.toolR, null),
                new AttackAnimation.Phase(0.77F, 0.77F, 0.93F, 1.23F, 1.23F, biped.toolR, null),
                new AttackAnimation.Phase(1.23F, 1.23F, 1.5F, 2, 2, biped.toolR, null))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        RED_TIDE_JUMP_ATTACK = new BasicMultipleAttackAnimation(0.15F, "cc/cc_jump_attack", biped,
                new AttackAnimation.Phase(0.0F, 1, 2, 2.33F, 2.33F, biped.toolR, WukongColliders.JUMP_ATTACK_LIGHT),
                new AttackAnimation.Phase(2.33F, 2.33F, 2.5F, 3, 3, biped.toolR, null))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);

    }

    public static void addItemEffectTimer(ServerPlayer serverPlayer, int leftTime) {
        serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
            if (capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                serverPlayer.getMainHandItem().getOrCreateTag().putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, leftTime);
            }
        }));
    }

    public static List<AnimationEvent.TimeStampedEvent> append(AnimationEvent.TimeStampedEvent e, AnimationEvent.TimeStampedEvent... oldArr) {
        List<AnimationEvent.TimeStampedEvent> list = new ArrayList<>(List.of(oldArr));
        list.add(e);
        return list;
    }

    /**
     * 添加物品缩放，并插值
     * 用途：{@link com.p1nero.wukong.mixin.ItemRendererMixin}
     */
    public static AnimationEvent.TimeStampedEvent[] getScaleEvents(ScaleTime... ticks) {
        int lastTick = ticks[ticks.length - 1].tick;
        AnimationEvent.TimeStampedEvent[] timeStampedEvents = new AnimationEvent.TimeStampedEvent[lastTick];
        ticks = interpolate(ticks, lastTick);
        timeStampedEvents[0] = AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
            if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                return;
            }
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
            tag.putBoolean("WK_shouldTranslateItem", false);
        }), AnimationEvent.Side.CLIENT);
        timeStampedEvents[lastTick - 1] = AnimationEvent.TimeStampedEvent.create(0.05F * lastTick, ((livingEntityPatch, staticAnimation, objects) -> {
            if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                return;
            }
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
            tag.putBoolean("WK_shouldTranslateItem", false);
        }), AnimationEvent.Side.CLIENT);
        for (int i = 1; i < lastTick - 1; i++) {
            float sx = ticks[i].sx;
            float sy = ticks[i].sy;
            float sz = ticks[i].sz;
            float tx = ticks[i].tx;
            float ty = ticks[i].ty;
            float tz = ticks[i].tz;
            timeStampedEvents[i] = AnimationEvent.TimeStampedEvent.create(0.05F * i, ((livingEntityPatch, staticAnimation, objects) -> {
                if (!WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                    return;
                }
                CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
                tag.putBoolean("WK_shouldScaleItem", true);
                tag.putBoolean("WK_shouldTranslateItem", true);
                tag.putFloat("WK_XScale", sx);
                tag.putFloat("WK_YScale", sy);
                tag.putFloat("WK_ZScale", sz);
                tag.putFloat("WK_XTranslation", tx);
                tag.putFloat("WK_YTranslation", ty);
                tag.putFloat("WK_ZTranslation", tz);
            }), AnimationEvent.Side.CLIENT);
        }
        return timeStampedEvents;
    }

    /**
     * 插值处理
     *
     * @param scaleTimes 需要插值的时间点，按tick算！
     * @param lastTick   最后一个tick，将对0~lastTick的每个tick插值处理
     * @return 插值后的数组
     */
    public static ScaleTime[] interpolate(ScaleTime[] scaleTimes, int lastTick) {
        ScaleTime[] results = new ScaleTime[lastTick + 1];

        // Fill known values
        for (ScaleTime scaleTime : scaleTimes) {
            if (scaleTime.tick <= lastTick) {
                results[scaleTime.tick] = scaleTime;
            }
        }

        // Perform linear interpolation
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                // Find the two surrounding points
                ScaleTime before = null;
                ScaleTime after = null;

                for (int j = i - 1; j >= 0; j--) {
                    if (results[j] != null) {
                        before = results[j];
                        break;
                    }
                }

                for (int j = i + 1; j <= lastTick; j++) {
                    if (results[j] != null) {
                        after = results[j];
                        break;
                    }
                }

                if (before != null && after != null) {
                    // Linear interpolation
                    float t = (float) (i - before.tick) / (after.tick - before.tick);
                    float sx = before.sx + t * (after.sx - before.sx);
                    float sy = before.sy + t * (after.sy - before.sy);
                    float sz = before.sz + t * (after.sz - before.sz);
                    float tx = before.tx + t * (after.tx - before.tx);
                    float ty = before.ty + t * (after.ty - before.ty);
                    float tz = before.tz + t * (after.tz - before.tz);
                    results[i] = new ScaleTime(i, sx, sy, sz, tx, ty, tz);
                }
            }
        }

        // Fill in nulls with the closest known value (forward filling)
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                if (i > 0) {
                    results[i] = results[i - 1]; // Copy the last known value
                } else {
                    results[i] = new ScaleTime(i, 1, 1, 1, 0, 0, 0);
                }
            }
        }

        return results;
    }

    public record ScaleTime(int tick, float sx, float sy, float sz, float tx, float ty, float tz) {
        public static ScaleTime of(float time, float x, float y, float z) {
            return new ScaleTime(((int) (time * 20)), x, y, z, 0, 0, 0);
        }

        public static ScaleTime of(float time, float sx, float sy, float sz, float x, float y, float z) {
            return new ScaleTime(((int) (time * 20)), sx, sy, sz, x, y, z);
        }

        public static ScaleTime reset(float time) {
            return new ScaleTime(((int) (time * 20)), 1, 1, 1, 0, 0, 0);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
                if (capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
                    CompoundTag mainHandItem = serverPlayer.getMainHandItem().getOrCreateTag();
                    mainHandItem.putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, Math.max(0, mainHandItem.getInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY) - 1));
                }
            }));
        }
    }

}

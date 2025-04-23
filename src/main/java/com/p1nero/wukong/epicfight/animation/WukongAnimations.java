package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.events.CameraAnim;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.effects.WuKongEffects;
import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.animation.custom.*;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.player.Input;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
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
    // 定义静态动画字段，用于存储各种角色动作的动画对象
    public static StaticAnimation IDLE; // 角色静止动画
    public static StaticAnimation WALK; // 角色行走动画
    public static StaticAnimation RUN_F; // 角色跑步动画（前进）
    public static StaticAnimation RUN; // 角色跑步动画
    public static StaticAnimation DASH; // 角色冲刺动画
    public static StaticAnimation JUMP; // 角色跳跃动画
    public static StaticAnimation FALL; // 角色下落动画
    public static StaticAnimation JUMP_ATTACK_LIGHT; // 轻型跳跃攻击动画
    public static StaticAnimation JUMP_ATTACK_LIGHT_HIT; // 轻型跳跃攻击命中动画
    public static StaticAnimation JUMP_ATTACK_HEAVY; // 重型跳跃攻击动画

    // 闪避相关动画（不同方向的闪避动作）
    public static StaticAnimation DODGE_F1; // 前闪避动画1
    public static StaticAnimation DODGE_F2; // 前闪避动画2
    public static StaticAnimation DODGE_F3; // 前闪避动画3
    public static StaticAnimation DODGE_FP; // 前闪避动画（特殊）

    public static StaticAnimation DODGE_B1; // 后闪避动画1
    public static StaticAnimation DODGE_B2; // 后闪避动画2
    public static StaticAnimation DODGE_B3; // 后闪避动画3
    public static StaticAnimation DODGE_BP; // 后闪避动画（特殊）

    public static StaticAnimation DODGE_L1; // 左闪避动画1
    public static StaticAnimation DODGE_L2; // 左闪避动画2
    public static StaticAnimation DODGE_L3; // 左闪避动画3
    public static StaticAnimation DODGE_LP; // 左闪避动画（特殊）

    public static StaticAnimation DODGE_R1; // 右闪避动画1
    public static StaticAnimation DODGE_R2; // 右闪避动画2
    public static StaticAnimation DODGE_R3; // 右闪避动画3
    public static StaticAnimation DODGE_RP; // 右闪避动画（特殊）

    // 棍花相关动画（单手和双手旋转棍子）
    public static StaticAnimation STAFF_SPIN_ONE_HAND_LOOP; // 单手旋转棍子动画
    public static StaticAnimation STAFF_SPIN_TWO_HAND_LOOP; // 双手旋转棍子动画

    // 轻击动作1~5（基本的攻击动作，按顺序从1到5）
    public static StaticAnimation STAFF_AUTO1_DASH; // 轻击1：冲刺状态下的轻击动画
    public static StaticAnimation STAFF_AUTO1; // 轻击1动画
    public static StaticAnimation STAFF_AUTO2; // 轻击2动画
    public static StaticAnimation STAFF_AUTO3; // 轻击3动画
    public static StaticAnimation STAFF_AUTO4; // 轻击4动画
    public static StaticAnimation STAFF_AUTO5; // 轻击5动画

    // 劈棍相关动画
    // SMASH_SPECIAL1 和 SMASH_SPECIAL2 是劈棍的特殊动作动画
    public static StaticAnimation SMASH_SPECIAL1; // 劈棍特殊动作1
    public static StaticAnimation SMASH_SPECIAL2; // 劈棍特殊动作2

    // 劈棍充能动画
    public static StaticAnimation SMASH_CHARGING_PRE; // 劈棍充能前摇动画
    public static StaticAnimation SMASH_CHARGING_LOOP; // 劈棍充能循环动画
    public static StaticAnimation SMASH_CHARGING_LOOP_STAND; // 劈棍充能站立动画

    // 劈棍的不同星级重击动画（随着充能增加，重击的威力和效果不同）
    public static StaticAnimation SMASH_CHARGED0; // 重击（0星）
    public static StaticAnimation SMASH_CHARGED1; // 重击（1星）
    public static StaticAnimation SMASH_CHARGED2; // 重击（2星）
    public static StaticAnimation SMASH_CHARGED3; // 重击（3星）
    public static StaticAnimation SMASH_CHARGED4; // 重击（4星）

    // 戳棍相关动画
    // THRUST_PRE：戳棍的预备动作，充能开始前的准备阶段
    // THRUST_CHARGING：戳棍充能动画
    // 不同星级的戳棍重击（不同充能级别的戳棍攻击）
    public static StaticAnimation THRUST_PRE; // 戳棍预备动画
    public static StaticAnimation THRUST_CHARGING; // 戳棍充能动画
    public static StaticAnimation THRUST_CHARGED0; // 戳棍重击（0星）
    public static StaticAnimation THRUST_CHARGED1; // 戳棍重击（1星）
    public static StaticAnimation THRUST_CHARGED2; // 戳棍重击（2星）
    public static StaticAnimation THRUST_CHARGED3; // 戳棍重击（3星）
    public static StaticAnimation THRUST_CHARGED4; // 戳棍重击（4星）

    // 戳棍相关附加动作
    public static StaticAnimation THRUST_JUESICK_END; // 戳棍的结束动画
    public static StaticAnimation THRUST_JUESICK_START; // 戳棍的起始动画
    public static StaticAnimation THRUST_JUESICK_LOOP; // 戳棍的循环动画（搅棍）
    public static StaticAnimation THRUST_FOOTAGE; // 戳棍进尺动画
    public static StaticAnimation THRUST_RETREAT; // 戳棍退寸动画

    // 定身术相关动画
    public static StaticAnimation DING; // 定身术动画

    // 安身法相关动画
    public static StaticAnimation AN_SHEN_FA; // 安身法动画

    // 聚形散气相关动画（包含不同方向的移动效果）
    public static StaticAnimation CLOUD_STEP_START; // 聚形散气开始动画
    public static StaticAnimation CLOUD_STEP_START_BACKWARD; // 聚形散气后退开始动画
    public static StaticAnimation CLOUD_STEP_END_STOP; // 聚形散气结束停止动画
    public static StaticAnimation CLOUD_STEP_END_FORWARD; // 聚形散气结束前进动画

    // 铜头铁臂相关动画
    public static StaticAnimation TONG_TOU_TIE_BI; // 铜头铁臂动画
    public static StaticAnimation TONG_TOU_TIE_BI_END; // 铜头铁臂结束动画
    public static StaticAnimation TONG_TOU_TIE_BI_FAIL; // 铜头铁臂失败动画

    // 身外身法相关动画
    public static StaticAnimation SHEN_WAI_SHEN_FA; // 身外身法动画
    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        // 注册所有动画到事件中，这样它们就能在游戏中使用
        event.getRegistryMap().put(WukongMoveset.MOD_ID, WukongAnimations::build); // 使用MOD_ID作为键，注册build方法来初始化动画
    }

    private static void build() {
        // 获取角色的骨架（HumanoidArmature），这是一个代表人物动作的结构，通常用于二足动物或人类角色
        HumanoidArmature biped = Armatures.BIPED; // 使用Armatures中的BIPED骨架模型

        // 创建一个“专治各种因为移动导致的动画取消”的事件
        // 这个事件用于防止角色在执行动画时受到玩家移动控制的影响（例如：当角色在执行某个动画时不应该继续移动）
        AnimationEvent.TimePeriodEvent allStopMovement = AnimationEvent.TimePeriodEvent.create(0.00F, Float.MAX_VALUE, ((livingEntityPatch, staticAnimation, objects) -> {
            // 如果是本地玩家Patch
            if (livingEntityPatch instanceof LocalPlayerPatch localPlayerPatch) {
                // 获取玩家的输入，设置所有输入为零，这样角色就不会移动
                Input input = localPlayerPatch.getOriginal().input;
                input.forwardImpulse = 0.0F; // 前进力量设为0
                input.leftImpulse = 0.0F; // 向左的力量设为0
                input.down = false; // 向下不激活
                input.up = false; // 向上不激活
                input.left = false; // 向左不激活
                input.right = false; // 向右不激活
                input.jumping = false; // 不跳跃
                input.shiftKeyDown = false; // 不按下Shift
                localPlayerPatch.getOriginal().setSprinting(false); // 不冲刺
            }
        }), AnimationEvent.Side.CLIENT); // 这是客户端特有的事件

        // 定义静止（待机）动画，IDLE代表角色处于待机状态时播放的动画
        IDLE = new StaticAnimation(true, "biped/idle", biped); // biped/idle是动画文件的路径，表示待机动画

        // 行走动画，播放速度调整为1.2倍
        WALK = new StaticAnimation(true, "biped/walk", biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F)); // 设置播放速度为1.2倍

        // 正常奔跑动画
        RUN_F = new StaticAnimation(true, "biped/run", biped); // biped/run是奔跑动画的路径

        // 跑步动画，采用 SelectiveAnimation，根据玩家的视角和移动方向来选择合适的动画
        RUN = new SelectiveAnimation((entityPatch) -> {
            // 获取角色的视角向量和移动向量
            Vec3 view = entityPatch.getOriginal().getViewVector(1.0F);
            Vec3 move = entityPatch.getOriginal().getDeltaMovement();
            double dot = view.dot(move); // 计算视角和移动向量的点积
            return dot < 0.0 ? 1 : 0; // 如果角色正在后退（视角和移动方向相反），播放跑步动画，否则播放行走动画
        }, RUN_F, WALK); // RUN_F代表向后的奔跑动画，WALK代表行走动画

        // 冲刺动画，表示角色快速冲刺的动作
        DASH = new StaticAnimation(true, "biped/dash", biped); // biped/dash是冲刺动画的路径

        // 跳跃动画，播放速度调整为1.2倍
        JUMP = new StaticAnimation(0.15F, false, "biped/jump", biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F)); // 跳跃动画，播放速度为1.2倍

        // 下落动画，角色从空中掉落时播放的动画
        FALL = new StaticAnimation(0.15F, true, "biped/fall", biped); // biped/fall是下落动画的路径

        // 以下是闪避动作动画，包括前后左右等不同方向的闪避动作

        DODGE_F1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f1", 0.6F, 0.8F, biped); // 前向闪避1
        DODGE_B1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b1", 0.6F, 0.8F, biped); // 后向闪避1
        DODGE_R1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r1", 0.6F, 0.8F, biped); // 右向闪避1
        DODGE_L1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l1", 0.6F, 0.8F, biped); // 左向闪避1

        // 第二组闪避动画（更高难度或更复杂的动作）
        DODGE_F2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f2", 0.6F, 0.8F, biped); // 前向闪避2
        DODGE_B2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_b2", 0.6F, 0.8F, biped); // 后向闪避2
        DODGE_R2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_r2", 0.6F, 0.8F, biped); // 右向闪避2
        DODGE_L2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_l2", 0.6F, 0.8F, biped); // 左向闪避2

        // 第三组闪避动画（极端动作或特殊技能闪避）
        DODGE_F3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_f3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true); // 前向闪避3（带垂直移动）
        DODGE_B3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_b3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true); // 后向闪避3（带垂直移动）
        DODGE_R3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_r3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true); // 右向闪避3（带垂直移动）
        DODGE_L3 = new WukongDodgeAnimation(0.1F, 0.6F, "biped/dodge/dodge_l3", 0.6F, 1.35F, biped).addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true); // 左向闪避3（带垂直移动）

        // 特殊闪避动画（例如，快速闪避或带有效果的闪避）
        DODGE_FP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_fp", 0.6F, 1.35F, biped, true); // 前向特殊闪避
        DODGE_BP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_bp", 0.6F, 1.35F, biped, true); // 后向特殊闪避
        DODGE_RP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_rp", 0.6F, 1.35F, biped, true); // 右向特殊闪避
        DODGE_LP = new WukongDodgeAnimation(0.1F, 0.63F, "biped/dodge/dodge_lp", 0.6F, 1.35F, biped, true); // 左向特殊闪避

        // 定义轻击（STAFF_AUTO1到STAFF_AUTO5）的动画，代表一系列的普通攻击动作
        STAFF_AUTO1_DASH = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR, "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)) // 轻击伤害修正
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F)) // 速度修正
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            // 处理开始事件，重置攻击计数器
                            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
                            }
                        }), AnimationEvent.Side.SERVER)); // 在服务器端进行计数器重置

        STAFF_AUTO1 = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR, "biped/auto_1", biped)
                // 创建轻击动画，动画时间为0.15秒，接下来的参数表示该动画的各个时段（如攻击前摇、攻击中段等）的持续时间
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                // 设置攻击阶段的伤害修正，0.9倍伤害
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                // 设置此动画不可取消（在动画执行时玩家无法移动）
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        // 设置播放速度为1.8倍，即加速播放此动画

        STAFF_AUTO2 = new BasicAttackAnimation(0.15F, 0.6667F, 0.875F, 0.875F, null, biped.toolR, "biped/auto_2", biped)
                // 创建第二个轻击动画，时间和动画路径参数类似于STAFF_AUTO1
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                // 设置攻击阶段的伤害修正，伤害提高为1.25倍
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                // 设置此动画不可取消（在动画执行时玩家无法移动）
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        // 设置播放速度为1.8倍，即加速播放此动画

        STAFF_AUTO3 = new BasicAttackAnimation(0.15F, "biped/auto_3", biped,
                // 创建第三个轻击动画，这次使用攻击的不同阶段来定义动作
                new AttackAnimation.Phase(0.0F, 0.25F, 0.4583F, 0.4583F, 0.4583F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        // 第一个阶段的伤害修正，保持原本的伤害
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)), // 设置最大攻击次数为4
                new AttackAnimation.Phase(0.4583F, 0.4583F, 0.7083F, 0.7083F, 3.3333F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(4.0F)))
                // 第二个阶段的伤害修正，保持原本的伤害，并允许最多4次攻击
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                // 设置此动画不可取消（在动画执行时玩家无法移动）
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        // 设置播放速度为1.2倍，即加速播放此动画

        STAFF_AUTO4 = new BasicAttackAnimation(0.15F, "biped/auto_4", biped,
                // 创建第四个轻击动画，包含多个阶段以细化动作
                new AttackAnimation.Phase(0.0F, 0.1F, 0.2F, 0.2F, 0.2F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        // 第一个阶段的伤害修正，减少至原本的0.5倍
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD), // 播放挥棒声音
                new AttackAnimation.Phase(0.2F, 0.2F, 0.4F, 0.4F, 0.4F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        // 第二个阶段的伤害修正，继续减少伤害至0.5倍
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD), // 播放挥棒声音
                new AttackAnimation.Phase(0.4F, 0.4F, 0.6F, 0.6F, 0.6F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        // 第三个阶段的伤害修正，继续减少伤害至0.5倍
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD), // 播放挥棒声音
                new AttackAnimation.Phase(0.6F, 0.6F, 0.8F, 0.8F, 0.8F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        // 第四个阶段的伤害修正，继续减少伤害至0.5倍
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD), // 播放挥棒声音
                new AttackAnimation.Phase(0.8F, 1.0416F, 1.125F, 1.2583F, 2.5F, biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        // 最后的阶段增加伤害修正至原本的1倍
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5F)))
                // 增加击中时的冲击修正，提升5倍
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                // 设置击中时的控制效果为“击倒”
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                // 设置此动画不可取消（在动画执行时玩家无法移动）
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        // 设置播放速度为1.2倍，即加速播放此动画

        STAFF_AUTO5 = new WukongScaleStaffAttackAnimation(0.01F, 0.9166F, 1.15F, 1.9833F, null, biped.toolR, "biped/auto_5", biped, 0.5F) {
            @Override
            public boolean isBasicAttackAnimation() {
                return true;
                // 返回是否为基本攻击动画，STAFF_AUTO5是基本攻击动画
            }
        }
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                // 设置伤害修正为3倍
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                // 设置控制效果为“长时间击倒”
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                // 播放大挥棒声音
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                // 设置击中时的冲击修正为2倍
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                // 设置此动画不可取消（在动画执行时玩家无法移动）
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                // 设置动画可以垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.9833F))
                // 设置动画在特定时段内不受重力影响
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F))
                // 设置播放速度为1.2倍，即加速播放此动画
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 1, 1)), AnimationEvent.Side.SERVER));
        // 在动画开始时播放声音“ENTITY_MOVE”，该事件只在服务器端执行

        JUMP_ATTACK_LIGHT = new WukongJumpAttackAnimation(0.10F, 0.13F, 0.40F, 0.50F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/jump_attack/jump_light_pre", biped)
                // 创建轻型跳跃攻击动画，动画的各个时段分别为 0.10F、0.13F、0.40F 和 0.50F，对应不同的动画阶段
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.45F))
                // 设置攻击阶段的伤害修正，伤害增加1.45倍
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                // 设置此动画最大击打次数为1，即每次跳跃攻击只能进行一次攻击
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.10F))
                // 设置动画播放期间不受重力影响的时长，从0.01秒到0.10秒
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        // 设置播放速度为1.8倍，加速播放此动画

        JUMP_ATTACK_LIGHT_HIT = new ActionAnimation(0.15F, "biped/jump_attack/jump_light_hit", biped)
                // 创建轻型跳跃攻击击中时的动画，持续时间为0.15秒
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                // 设置该动画允许垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.3F))
                // 设置动画播放期间不受重力影响的时长，从0.01秒到0.3秒
                .addState(EntityState.CAN_SKILL_EXECUTION, true)
                // 设置该动画允许技能执行，即可以使用重击取消后摇
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        // 设置播放速度为正常速度（1倍）

        JUMP_ATTACK_HEAVY = new WukongScaleStaffAttackAnimation(0.01F, 0.54F, 0.67F, 1.25F, null, biped.toolR, "biped/jump_attack/jump_heavy", biped, 0.5F)
                // 创建重型跳跃攻击动画，设定其时间和其他参数
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                // 设置攻击阶段的控制效果为“长时间击倒”
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                // 设置攻击时的挥棒声音为“WHOOSH_BIG”
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                // 设置击中时的冲击修正，提升2倍
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                // 设置该动画可以进行垂直移动
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.67F))
                // 设置动画播放期间不受重力影响的时长，从0.01秒到0.67秒
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                // 设置播放速度为正常速度（1倍）
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ROLL, 1, 1)), AnimationEvent.Side.SERVER));
        // 在动画开始时播放“ROLL”声音，服务器端执行

        STAFF_SPIN_ONE_HAND_LOOP = new StaffSpinAttackAnimation(1.25F, biped, "biped/staff_spin/staff_spin_one_hand", 0.05F, false)
                // 创建单手棍花旋转攻击动画，旋转速度为1.25倍，动画持续时间为0.05秒，禁用某些动作
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        // 设置播放速度为正常速度（1倍）

        STAFF_SPIN_TWO_HAND_LOOP = new StaffSpinAttackAnimation(0.83F, biped, "biped/staff_spin/staff_spin_two_hand", 0.08F, true)
                // 创建双手棍花旋转攻击动画，旋转速度为0.83倍，动画持续时间为0.08秒，启用某些动作
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        // 设置播放速度为正常速度（1倍）

        //劈start
        //前摇完自动接下一个动作
        // 劈棍技能动画
        // SMASH_CHARGING_PRE：为劈棍的蓄力前摇阶段，准备重击。
        SMASH_CHARGING_PRE = new ActionAnimation(0.15F, "biped/smash/smash_charge_pre", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    // 蓄力结束后，切换为充能持续动画
                    livingEntityPatch.reserveAnimation(SMASH_CHARGING_LOOP_STAND);
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        // 设置技能数据同步，表示重击正在充能
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(
                        // 在不同时间戳播放声音（如"WHOOSH_ROD"）
                        AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER));
        // SMASH_CHARGING_LOOP_STAND：重击充能持续阶段，角色站立不动并持续蓄力。
        SMASH_CHARGING_LOOP_STAND = new StaticAnimation(0.15F, true, "biped/smash/smash_charging", biped);
// SMASH_CHARGED0：代表重击（0星），充能达到一定程度后发动重击。
        SMASH_CHARGED0 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR, "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F)) // 设置击中效果
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.6F))// 设置伤害值
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)// 垂直移动效果
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)// 不允许在攻击过程中取消移动
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)// 不在链接中移动
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F))// 无重力时间
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))// 设置播放速度
                .addEvents(allStopMovement)// 停止所有移动
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);// 播放击地声
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
                            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch ) {
                                SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                                if (manager.hasData(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK)) {
                                    manager.setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, false, serverPlayerPatch.getOriginal());
                                    manager.setDataSync(SmashHeavyAttack.IS_SPECIAL_ATTACK_SUCCESS, false, serverPlayerPatch.getOriginal());
                                }
                            }
                        }), AnimationEvent.Side.SERVER)
                )
                .addEvents(
                        append(
                                AnimationEvent.TimeStampedEvent.create(0.4F, ((livingEntityPatch, anim, obj) -> {
                                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch && serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().hasData(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK)) {
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
                        ScaleTime.of(0.8F, 1, 2.3F, 1F, 0F, -1.5F, 0F),
                        ScaleTime.of(1.0F, 1F, 4F, 1F, 0F, -1.5F, 0F),
                        ScaleTime.of(1.1666F, 1F, 4F, 1F, 0F, -1.5F, 0F),
                        ScaleTime.reset(1.8F)
                )
        );

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
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.6F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.REPEATING_DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER), AnimationEvent.TimeStampedEvent.create(0.15F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 0.0F, 0.0F)), AnimationEvent.Side.SERVER));

        //进尺
        THRUST_FOOTAGE = new WukongScaleStaffAttackAnimation(0.15F, 0.8F, 1.0F, 1.5F, WukongColliders.THRUST_FOOTAGE, biped.toolR, "biped/thrust/thrust_footage", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.92F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOC_TARGET)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, ((dynamicAnimation, pose, livingEntityPatch, v, v1) -> {
                }))//覆盖掉
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if(manager.hasData(ThrustHeavyAttack.DODGE_SUCCESS) && manager.getDataValue(ThrustHeavyAttack.DODGE_SUCCESS)){
                            manager.setDataSync(ThrustHeavyAttack.DODGE_SUCCESS, false, serverPlayerPatch.getOriginal());//重置是否无敌的判断
                        }
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(append(AnimationEvent.TimeStampedEvent.create(1.4F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(ThrustHeavyAttack.REPEATING_DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER), getScaleEvents(
                        ScaleTime.reset(0.0F),
                        ScaleTime.of(1.2F, 1, 1F, 1F, 0F, 2.0f, 0F),
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
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN) && manager.getDataValue(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN)) {
                            serverPlayerPatch.reserveAnimation(THRUST_JUESICK_LOOP);
                        }
                    }
                }), AnimationEvent.Side.SERVER), AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if (livingEntityPatch.isLogicalClient()) {
                        CameraAnim.zoomOut();
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

        THRUST_JUESICK_START = new BasicAttackAnimation(0.15F, 0.866F, 1.0F, 1.2F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_juesick_start", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.68f))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.7F))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.reserveAnimation(THRUST_JUESICK_LOOP)), AnimationEvent.Side.SERVER));

        THRUST_JUESICK_END = new WukongScaleStaffAttackAnimation(0F, 0F, 0F, 0.9f, null, biped.toolR, "biped/thrust/thrust_juesick_end", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        //戳end
        //定
        DING = (new SpecialActionAnimation(0.1F, 0.14F, "biped/magicarts/stop", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOCROT_TARGET).addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_LOCROT_TARGET)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, (self, entitypatch, speed, elapsedTime) -> 0.75f)
                .addProperty(AnimationProperty.StaticAnimationProperty.TIME_STAMPED_EVENTS, new AnimationEvent.TimeStampedEvent[] {
                        AnimationEvent.TimeStampedEvent.create(0.0F, (livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.DING.get(),1.0f,1.0f,1.0f), AnimationEvent.TimeStampedEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.06F, (livingEntityPatch, staticAnimation, objects) -> {
                            LivingEntity attackTarget = livingEntityPatch.getTarget();
                            if (attackTarget != null) {
                                LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(attackTarget, LivingEntityPatch.class);
                                if (ep != null) {
                                    //躲定身
                                    if ((ep.getAnimator().getPlayerFor(null).getAnimation() instanceof DodgeAnimation | ep.getAnimator().getPlayerFor(null).getAnimation() instanceof LongHitAnimation)) {
                                        ep.playSound(EpicFightSounds.ROLL,1.0f,0.8f,1.2f);
                                        return;
                                    }
                                }
                                if (attackTarget.getLevel() instanceof ServerLevel serverLevel) {
                                    serverLevel.sendParticles(WuKongParticles.DING.get(), attackTarget.getX(), attackTarget.getEyeY() + 1, attackTarget.getZ(), 1, 0, 0, 0, 0);
                                }
                                attackTarget.addEffect(new MobEffectInstance(WuKongEffects.DING.get(), 120, 0));
                                attackTarget.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120, 0));
                            }
                        }, AnimationEvent.TimeStampedEvent.Side.SERVER)
                });
        //安身法
        AN_SHEN_FA = new ActionAnimation(0.01F, "biped/magicarts/asf", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL,true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE,true)
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT,true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 2.1F));
        //聚形散气
        CLOUD_STEP_START = new ActionAnimation(0.15F, 0.6F, "biped/magicarts/jxsq_start", biped)
                .newTimePair(0.0F, 0.5F)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.MISSED);

        CLOUD_STEP_START_BACKWARD = new ActionAnimation(0.15F, 0.6F, "biped/magicarts/jxsq_start_s", biped)
                .newTimePair(0.0F, 0.5F)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.MISSED);

        CLOUD_STEP_END_STOP = new AttackAnimation(0.15F, 0.6F, 0.15F, 0.7F, 1.6F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/magicarts/jxsq_end", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_UPDATE_TIME, TimePairList.create(0.0F, 0.4F))
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_DEST_LOCATION_BEGIN)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_DEST_LOCATION)
                .addProperty(AnimationProperty.ActionAnimationProperty.COORD_GET, MoveCoordFunctions.WORLD_COORD)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        CLOUD_STEP_END_FORWARD = new AttackAnimation(0.15F, 0.6F, 0.15F, 0.7F, 1.6F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR, "biped/magicarts/jxsq_end_f", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_ON_LINK, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));

        //铜头铁臂
        TONG_TOU_TIE_BI = new ActionAnimation(0.15F, 1.6333F, "biped/magicarts/tttb", biped)
                .newTimePair(0.0F, 1.633F)
                .addStateRemoveOld(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.BLOCKED)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

        TONG_TOU_TIE_BI_END = new ActionAnimation(0.15F, 0, "biped/magicarts/tttb_end", biped);

        TONG_TOU_TIE_BI_FAIL = new LongHitAnimation(0.15F, "biped/magicarts/tttb_fail", biped);
        //身外身法
        SHEN_WAI_SHEN_FA = new ActionAnimation(0.15F, 3.3F, "biped/magicarts/haomao_fenshen", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(3.2F, ((livingEntityPatch, staticAnimation, objects) -> {
                    Vec3 startPos = livingEntityPatch.getTarget() == null ? livingEntityPatch.getOriginal().position() : livingEntityPatch.getTarget().position();
                    Vec3 particleOrigin = startPos.subtract(0, 1, 0);
                    if(livingEntityPatch.getOriginal() instanceof ServerPlayer serverPlayer){
                    ServerLevel serverLevel = serverPlayer.getLevel();
                    int particleCount = 7;
                    float radius = 3F;
                        for (int i = 0; i < particleCount; i++) {
                            float angle = (float) i / particleCount * (float) Math.PI * 2;
                            float xOffset = radius * (float) Math.cos(angle);
                            float zOffset = radius * (float) Math.sin(angle);
                            Vec3 particlePos = particleOrigin.add(xOffset, 0, zOffset);
                            serverLevel.sendParticles(ParticleTypes.POOF, particlePos.x, particlePos.y + 2, particlePos.z, 20, 0, 0, 0, 0.1);
                            livingEntityPatch.playSound(SoundEvents.GENERIC_EXPLODE, 0.5F, 0.0F, 0.0F);
                            FakeWukongEntity fakeWukongEntity = new FakeWukongEntity(serverPlayer);
                            fakeWukongEntity.setPos(particleOrigin.add(xOffset, 1, zOffset));
                            serverPlayer.getLevel().addFreshEntity(fakeWukongEntity);
                            serverPlayer.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.addFakeWukongId(fakeWukongEntity.getId()));

                        }
                    }
                }), AnimationEvent.Side.SERVER))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

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

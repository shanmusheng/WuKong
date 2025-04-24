package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.KongQiMoveset;
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
import net.minecraft.client.player.Input;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
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

import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = KongQiMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KongqiAnimations {
    // 定义静态动画字段，用于存储各种角色动作的动画对象
    public static StaticAnimation IDLE; // 角色静止动画
    public static StaticAnimation WALK; // 角色行走动画
    public static StaticAnimation RUN_F; // 角色跑步动画（前进）
    public static StaticAnimation RUN; // 角色跑步动画
    public static StaticAnimation DASH; // 角色冲刺动画
    public static StaticAnimation JUMP; // 角色跳跃动画
    public static StaticAnimation FALL; // 角色下落动画

    public static StaticAnimation KONGQI_PING_A; // 空气平a
    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        // 注册所有动画到事件中，这样它们就能在游戏中使用
        event.getRegistryMap().put(KongQiMoveset.MOD_ID, KongqiAnimations::build); // 使用MOD_ID作为键，注册build方法来初始化动画
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

        // 定义空气平a的动画，代表一系列的普通攻击动作
//        convertTime是过渡时间 一个动画到下一个动画的自动补帧时间
//        antic 伤害开始时间
//        contact  伤害停止时间
//        recovery 后摇停止时间
//        collider 碰撞箱大小
        //biped.toolR指定哪一个模型在攻击
        //path 模型路径 动画路径
        KONGQI_PING_A = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR, "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F)) // 轻击伤害修正
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F)) // 速度修正
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            // 处理开始事件，重置攻击计数器
                            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
                            }
                        }), AnimationEvent.Side.SERVER)); // 在服务器端进行计数器重置
    }
}

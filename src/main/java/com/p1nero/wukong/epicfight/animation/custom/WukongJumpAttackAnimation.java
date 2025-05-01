package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 这段代码实现了一个自定义的跳跃攻击动画类 WukongJumpAttackAnimation，
 * 它继承了 BasicAttackAnimation，并添加了攻击命中后执行后跳的动作。
 * 该动画类包含多个阶段，处理动画的不同时间点，并触发相应的动作和事件。
 */
public class WukongJumpAttackAnimation extends BasicAttackAnimation {

    // 构造函数，根据不同参数构建动画
    public WukongJumpAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    /**
     * 打击时的逻辑处理。在攻击命中时触发后跳的动画，并播放相应的声音和粒子效果。
     * @param entityPatch 当前玩家或敌人的状态
     */
    protected void attackTick(LivingEntityPatch<?> entityPatch) {
        // 获取动画播放器
        AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(this);
        float elapsedTime = player.getElapsedTime();  // 当前动画时间
        float prevElapsedTime = player.getPrevElapsedTime();  // 上一帧的动画时间
        EntityState state = this.getState(entityPatch, elapsedTime);  // 当前帧的状态
        EntityState prevState = this.getState(entityPatch, prevElapsedTime);  // 上一帧的状态
        Phase phase = this.getPhaseByTime(elapsedTime);  // 当前动画阶段

        // 判断是否需要执行后跳：如果当前处于攻击状态，并且是第一次攻击
        if (state.getLevel() == 1 && !state.turningLocked() && entityPatch instanceof MobPatch<?> mobpatch) {
            mobpatch.getOriginal().getNavigation().stop();  // 停止敌人的移动
            entityPatch.getOriginal().attackAnim = 2.0F;  // 设置攻击动画的状态
            LivingEntity target = entityPatch.getTarget();  // 获取攻击目标
            if (target != null) {
                entityPatch.rotateTo(target, entityPatch.getYRotLimit(), false);  // 朝向目标
            }
        }

        // 判断攻击阶段，播放相应的音效并执行碰撞检测
        if (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2) {
            // 判断动画状态是否发生变化，如果有变化，播放声音并移除受伤实体
            if (!prevState.attacking() || phase != this.getPhaseByTime(prevElapsedTime) && (state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2)) {
                entityPatch.playSound(this.getSwingSound(entityPatch, phase), 0.0F, 0.0F);
                entityPatch.removeHurtEntities();  // 移除已受伤的实体
            }

            this.hurtCollidingEntities(entityPatch, prevElapsedTime, elapsedTime, prevState, state, phase);  // 进行伤害判定

            // 如果有命中的目标，播放后跳动画
            if(!entityPatch.getCurrenltyAttackedEntities().isEmpty()){
                entityPatch.playAnimationSynchronized(WukongAnimations.JUMP_ATTACK_LIGHT_HIT, 0.15F);
            }
        }
    }

    /**
     * 返回是否为基础攻击动画。此处返回 false，因为这是一个跳跃攻击动画，而非基础攻击。
     * @return 是否为基础攻击动画
     */
    @Override
    public boolean isBasicAttackAnimation() {
        return false;
    }
}
//代码解析：
//        类继承与构造函数：
//
//        WukongJumpAttackAnimation 继承自 BasicAttackAnimation，并通过多个构造函数支持不同类型的参数设置。构造函数的参数包括动画的各个阶段时间（如准备时间、接触时间、恢复时间等）、碰撞器、路径和骨架等。
//
//        attackTick 方法：
//
//        attackTick 是动画每一帧的核心方法，处理攻击的具体逻辑。它会根据动画的当前状态来判断是否执行后跳动作，并在攻击时触发相应的音效和动画。
//
//        state.getLevel() 判断攻击的当前阶段，mobpatch.getOriginal().getNavigation().stop() 停止敌人的移动，entityPatch.rotateTo(target, entityPatch.getYRotLimit(), false) 控制玩家朝向目标。
//
//        hurtCollidingEntities 进行碰撞检测，如果攻击命中目标，则播放后跳的动画。
//
//        播放动画与音效：
//
//        entityPatch.playSound(this.getSwingSound(entityPatch, phase), 0.0F, 0.0F) 在特定阶段播放攻击的音效。
//
//        entityPatch.playAnimationSynchronized(WukongAnimations.JUMP_ATTACK_LIGHT_HIT, 0.15F) 播放后跳攻击的动画。
//
//        isBasicAttackAnimation 方法：
//
//        覆盖 BasicAttackAnimation 类的 isBasicAttackAnimation 方法，返回 false，表示该动画不是基础攻击动画，而是一个自定义的跳跃攻击动画。
//
//        总结：
//        该动画类是为 Wukong 提供的一个自定义跳跃攻击动画，它在攻击命中时触发后跳动作，并结合动画、音效和粒子效果，增强了游戏中的战斗体验。通过 attackTick 方法，判断攻击的不同阶段，并执行相应的操作，如播放音效、进行伤害判定、播放跳跃攻击动画等。

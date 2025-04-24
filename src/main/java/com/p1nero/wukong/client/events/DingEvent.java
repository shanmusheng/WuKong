package com.p1nero.wukong.client.events;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber  // 标记类作为事件订阅者，处理游戏中的事件
public class DingEvent {

    // 订阅LivingEntity的更新事件，每次生物状态更新时触发
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        execute(event, event.getEntityLiving());  // 调用执行方法，处理生物状态
    }

    // 这个方法是外部调用的接口，传入一个实体并执行相关逻辑
    public static void execute(Entity entity) {
        execute(null, entity);  // 调用内部方法，传入null作为事件参数
    }

    // 核心执行方法，处理生物状态和效果
    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null) {
            return;  // 如果实体为空，则不进行任何操作
        }

        // 如果实体是生物，并且拥有"钉住"效果（Ding效果），则执行以下操作
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(WuKongEffects.DING.get())) {

            // 如果实体在服务器端，则发送粒子效果（Glow和WaxOff效果）
            if (entity.getLevel() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.GLOW, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 0, 0, 10); // Glow粒子
                serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 0, 0, 10); // WaxOff粒子
            }

            // 如果生物的生命值为0或更低，移除Ding效果
            if (livingEntity.getHealth() <= 0.0f) {
                livingEntity.removeEffect(WuKongEffects.DING.get());
            }

            // 获取生物的EpicFight能力（EntityPatch），用以管理生物的状态
            LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);

            // 锁定生物的所有移动与动画状态
            livingEntity.setDeltaMovement(0, 0, 0);  // 停止生物的移动
            livingEntity.setSprinting(false);  // 停止冲刺
            livingEntity.setSpeed(0.0f);  // 设置速度为0
            livingEntity.animationSpeed = 0.0f;  // 设置动画速度为0
            livingEntity.xxa = 0.0f;  // 设置X轴速度为0
            livingEntity.yya = 0.0f;  // 设置Y轴速度为0
            livingEntity.zza = 0.0f;  // 设置Z轴速度为0

            // 如果EntityPatch存在，更新实体状态，锁定转向和移动
            if (ep != null) {
                ep.getEntityState().setState(EntityState.ATTACKING, false);  // 禁止攻击
                ep.getEntityState().setState(EntityState.LOCKON_ROTATE, true);  // 锁定旋转
                ep.getEntityState().setState(EntityState.MOVEMENT_LOCKED, true);  // 锁定移动
                ep.getEntityState().setState(EntityState.TURNING_LOCKED, true);  // 锁定转向
                ep.getEntityState().setState(EntityState.UPDATE_LIVING_MOTION, false);  // 不更新生物运动

                // 同步Animator的实体状态
                ep.getAnimator().getEntityState().setState(EntityState.ATTACKING, false);
                ep.getAnimator().getEntityState().setState(EntityState.LOCKON_ROTATE, true);
                ep.getAnimator().getEntityState().setState(EntityState.MOVEMENT_LOCKED, true);
                ep.getAnimator().getEntityState().setState(EntityState.TURNING_LOCKED, true);
                ep.getAnimator().getEntityState().setState(EntityState.UPDATE_LIVING_MOTION, false);
            }

            // 如果加载了Citadel模组，则执行动画调整 炎魔你也跑不了
            if (ModList.get().isLoaded("citadel")) {
                if (entity instanceof IAnimatedEntity iAnimatedEntity) {
                    // 减少动画的tick数，减缓动画速度，进一步冻结状态
                    iAnimatedEntity.setAnimationTick(iAnimatedEntity.getAnimationTick() - 1);
                }
            }
        }
    }
}

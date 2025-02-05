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

@Mod.EventBusSubscriber
public class DingEvent {
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        execute(event, event.getEntityLiving());
    }

    public static void execute(Entity entity) {
        execute(null, entity);
    }

    private static void execute(@Nullable Event event, Entity entity) {
        if (entity == null) {
            return;
        }
        //原版生物不许动
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(WuKongEffects.DING.get())) {
            if (entity.getLevel() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.GLOW, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 0, 0, 10);
                serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getZ(), 1, 0, 0, 0, 10);
            }
            if (livingEntity.getHealth() <= 0.0f) livingEntity.removeEffect(WuKongEffects.DING.get());
            LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
            livingEntity.setDeltaMovement(0, 0, 0);
            livingEntity.setSprinting(false);
            livingEntity.setSpeed(0.0f);
            livingEntity.animationSpeed = 0.0f;
            livingEntity.xxa = 0.0f;
            livingEntity.yya = 0.0f;
            livingEntity.zza = 0.0f;
            if (ep != null) {
                ep.getEntityState().setState(EntityState.ATTACKING, false);
                ep.getEntityState().setState(EntityState.LOCKON_ROTATE, true);
                ep.getEntityState().setState(EntityState.MOVEMENT_LOCKED, true);
                ep.getEntityState().setState(EntityState.TURNING_LOCKED, true);
                ep.getEntityState().setState(EntityState.UPDATE_LIVING_MOTION, false);
                ep.getAnimator().getEntityState().setState(EntityState.ATTACKING, false);
                ep.getAnimator().getEntityState().setState(EntityState.LOCKON_ROTATE, true);
                ep.getAnimator().getEntityState().setState(EntityState.MOVEMENT_LOCKED, true);
                ep.getAnimator().getEntityState().setState(EntityState.TURNING_LOCKED, true);
                ep.getAnimator().getEntityState().setState(EntityState.UPDATE_LIVING_MOTION, false);
            }
            //炎魔你也跑不了
            if (ModList.get().isLoaded("citadel")) {
                if (entity instanceof IAnimatedEntity iAnimatedEntity) {
                    iAnimatedEntity.setAnimationTick(iAnimatedEntity.getAnimationTick() - 1);
                }
            }
        }
    }
}
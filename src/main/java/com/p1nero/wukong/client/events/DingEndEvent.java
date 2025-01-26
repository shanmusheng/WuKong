package com.p1nero.wukong.client.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DingEndEvent {
    //释放粒子
    public static void execute(Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.GLOW, entity.getX(), entity.getY() + 1, entity.getZ(), 50, 0, 0, 0, 10);
        if (level instanceof ServerLevel serverLevel) serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getZ(), 50, 0, 0, 0, 10);
        entity.aiStep();
        entity.tick();
        LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
        if (ep != null) {
            if (ep.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation) {
                staticAnimation.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> entity.getPersistentData().getFloat("animation")));
            }
        }
    }
}

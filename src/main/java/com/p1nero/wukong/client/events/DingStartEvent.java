package com.p1nero.wukong.client.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DingStartEvent {
    public static void execute(LivingEntity entity, LevelAccessor world) {
        LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
        if (ep != null) {
            if (ep.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && staticAnimation.getProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER).isPresent()) {
//                entity.getPersistentData().putFloat("animation", staticAnimation.getProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER).get().modify(staticAnimation, ep, 1, 1));

                staticAnimation.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 0.0F));

            }
        }
    }
}

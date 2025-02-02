package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.Mass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = ActionAnimation.class,remap = false)
public class ActionAnimationMixin {
    @Inject(method = "move",at = @At("HEAD"), cancellable = true)
    protected void wukong$move(LivingEntityPatch<?> entitypatch, DynamicAnimation animation, CallbackInfo ci) {
        if (entitypatch.getOriginal().hasEffect(WuKongEffects.DING.get())) {
            ci.cancel();
        }
    }
}

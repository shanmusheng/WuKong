package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = MovementAnimation.class,remap = false)
public class MovementAnimationMixin {
    //不许动
    @Inject(method = "getPlaySpeed",at = @At("HEAD"), cancellable = true)
    public void getPlaySpeed(LivingEntityPatch<?> entitypatch, CallbackInfoReturnable<Float> cir) {
        if (entitypatch.getOriginal().hasEffect(WuKongEffects.DING.get())) cir.setReturnValue(0.0f);
    }
}

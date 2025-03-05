package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class,remap = false)
public abstract class AnimationPlayerMixin {
    @Shadow
    public DynamicAnimation getAnimation() {
        return null;
    }
    @Inject(method = "tick",at = @At("HEAD"), cancellable = true)
    public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
        if (entitypatch.getOriginal().hasEffect(WuKongEffects.DING.get())) {
            ci.cancel();
        }
    }
}

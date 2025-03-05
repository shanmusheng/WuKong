
package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob extends LivingEntity {
    //不许动
    protected MixinMob(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(at = @At("HEAD"), method = "isNoAi()Z", cancellable = true)
    public void wukong$isNoAi(CallbackInfoReturnable<Boolean> callback) {
        if (hasEffect(WuKongEffects.DING.get())) {
            callback.setReturnValue(true);
        }
    }
    @Inject(at = @At("HEAD"), method = "tickHeadTurn", cancellable = true)
    private void wukong$tickHeadTurn(float p_21538_, float p_21539_, CallbackInfoReturnable<Float> callback) {
        if (hasEffect(WuKongEffects.DING.get())) {
            callback.setReturnValue(0.0f);
        }
    }
    @Inject(at = @At("HEAD"), method = "createBodyControl", cancellable = true)
    protected void wukong$createBodyControl(CallbackInfoReturnable<BodyRotationControl> cir) {
        if (hasEffect(WuKongEffects.DING.get())) {
            cir.setReturnValue(null);
        }
    }
}


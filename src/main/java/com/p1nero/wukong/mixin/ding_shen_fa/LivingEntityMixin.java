package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    //不许动
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    private void wukong$hurt(DamageSource p_21016_, float p_21017_, CallbackInfoReturnable<Boolean> cir) {
        if (p_21016_.getDirectEntity() instanceof LivingEntity livingEntity) {
            if ((livingEntity.hasEffect(WuKongEffects.DING.get()))) cir.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"),method = "getSpeed",cancellable = true)
    public void wukong$getSpeed(CallbackInfoReturnable<Float> cir) {
        if (level.getEntity(getId()) instanceof LivingEntity livingEntity && livingEntity.hasEffect(WuKongEffects.DING.get())) cir.setReturnValue(0.0f);
    }


}

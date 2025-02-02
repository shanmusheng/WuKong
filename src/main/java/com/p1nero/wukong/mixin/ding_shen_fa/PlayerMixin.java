package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public abstract class PlayerMixin extends LivingEntity {
    //不许动
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(at = @At("HEAD"),method = "getSpeed",cancellable = true)
    public void wukong$getSpeed(CallbackInfoReturnable<Float> cir) {
        if (level.getEntity(getId()) instanceof LivingEntity livingEntity && livingEntity.hasEffect(WuKongEffects.DING.get())) cir.setReturnValue(0.0f);
    }
}

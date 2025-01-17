package com.p1nero.wukong.mixin;

import com.p1nero.wukong.client.events.CameraAnim;
import net.minecraft.client.CameraType;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;

@Mixin(value = RenderEngine.class, remap = false)
public class RenderEngineMixin {
    @Inject(method = "correctCamera", at = @At("HEAD"), cancellable = true)
    private void injectCorrectCamera(EntityViewRenderEvent.CameraSetup event, float partialTicks, CallbackInfo ci){
        if (CameraAnim.isAiming()) {
            ci.cancel();
        }
    }

    @Inject(method = "setRangedWeaponThirdPerson", at = @At("HEAD"), cancellable = true)
    private void injectSetRangedWeaponThirdPerson(EntityViewRenderEvent.CameraSetup event, CameraType pov, double partialTicks, CallbackInfo ci){
        if (CameraAnim.isAiming()) {
            ci.cancel();
        }
    }

}

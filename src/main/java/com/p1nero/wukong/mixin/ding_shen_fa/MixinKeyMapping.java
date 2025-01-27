package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {KeyMapping.class},priority = 999999999)
public class MixinKeyMapping {
    //不许动
    @Shadow
    boolean isDown;

    @Inject(at = @At("HEAD"), method = "isDown()Z", cancellable = true)
    public void isDown(CallbackInfoReturnable<Boolean> callback) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            if (player.hasEffect(WuKongEffects.DING.get())) {
                callback.setReturnValue(false);
                callback.cancel();
                if (isDown) isDown = false;
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "consumeClick()Z", cancellable = true)
    public void consumeClick(CallbackInfoReturnable<Boolean> callback) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            if (player.hasEffect(WuKongEffects.DING.get())) {
                callback.setReturnValue(false);
                callback.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "matches", cancellable = true)
    public void matches(int key, int scancode, CallbackInfoReturnable<Boolean> callback) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            if (player.hasEffect(WuKongEffects.DING.get())) {
                callback.setReturnValue(false);
                callback.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "matchesMouse", cancellable = true)
    public void matchesMouse(int button, CallbackInfoReturnable<Boolean> callback) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null) {
            if (player.hasEffect(WuKongEffects.DING.get())) {
                callback.setReturnValue(false);
                callback.cancel();
            }
        }
    }
}

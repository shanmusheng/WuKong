package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KeyboardHandler.class,priority = 999999999)
public class MixinKeyboardHandler {
    //不许动
    @Inject(at = @At("HEAD"), method = "keyPress(JIIII)V", cancellable = true)
    public void keyPress(long screen, int key, int scanCode, int action, int modifier, CallbackInfo callback) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null) {
            LocalPlayer player = mc.player;
            if (player != null) {
                if (player.hasEffect(WuKongEffects.DING.get())) {
                    callback.cancel();
                }
            }
        }
    }
}
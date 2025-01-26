package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class,priority = 999999999)
public class MixinMouseHandler {
	//不许动
	@Inject(at=@At("HEAD"), method = "onPress", cancellable = true)
	private void onPress(long screen, int button, int action, int mods, CallbackInfo callback) {
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

	@Inject(at=@At("HEAD"), method = "onMove", cancellable = true)
	public void onMove(long p_91562_, double p_91563_, double p_91564_, CallbackInfo callback) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player != null && mc.screen == null) {
			if (player.hasEffect(WuKongEffects.DING.get())) {
				callback.cancel();
			}
		}
	}
}

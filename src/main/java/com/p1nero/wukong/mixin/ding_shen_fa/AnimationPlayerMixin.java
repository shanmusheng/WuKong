package com.p1nero.wukong.mixin.ding_shen_fa;

import com.p1nero.wukong.effects.WuKongEffects;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = AnimationPlayer.class,remap = false)
public abstract class AnimationPlayerMixin {
    @Shadow
    private float elapsedTime;
    @Shadow
    private float prevElapsedTime;
    @Shadow
    private boolean reversed;
    @Shadow
    public DynamicAnimation getAnimation() {
        return null;
    }
    @Shadow
    private boolean isEnd;
    @Shadow
    private DynamicAnimation play;
    @Shadow 	public boolean isReversed() {
        return this.reversed;
    }
    /**
     *
     *
     */
//    @Overwrite
//    public void tick(LivingEntityPatch<?> entitypatch) {
//        if (!entitypatch.getOriginal().hasEffect(WuKongEffects.DING.get())) {
//            this.prevElapsedTime = this.elapsedTime;
//            float playbackSpeed = this.getAnimation().getPlaySpeed(entitypatch);
//            AnimationProperty.PlaybackTimeModifier playSpeedModifier = (AnimationProperty.PlaybackTimeModifier) this.getAnimation().getRealAnimation().getProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER).orElse(null);
//            if (playSpeedModifier != null) {
//                playbackSpeed = playSpeedModifier.modify(this.getAnimation(), entitypatch, playbackSpeed, this.elapsedTime);
//            }
//
//            this.elapsedTime += 0.05F * playbackSpeed * (this.isReversed() && this.getAnimation().canBePlayedReverse() ? -1.0F : 1.0F);
//            AnimationProperty.PlaybackTimeModifier playTimeModifier = (AnimationProperty.PlaybackTimeModifier) this.getAnimation().getRealAnimation().getProperty(AnimationProperty.StaticAnimationProperty.ELAPSED_TIME_MODIFIER).orElse(null);
//            if (playTimeModifier != null) {
//                this.elapsedTime = playTimeModifier.modify(this.getAnimation(), entitypatch, playbackSpeed, this.elapsedTime);
//            }
//
//
//            if (this.elapsedTime >= this.play.getTotalTime()) {
//                if (this.play.isRepeat()) {
//                    this.prevElapsedTime = 0.0F;
//                    this.elapsedTime %= this.play.getTotalTime();
//                } else {
//                    this.elapsedTime = this.play.getTotalTime();
//                    this.isEnd = true;
//                }
//            } else if (this.elapsedTime < 0.0F) {
//                if (this.play.isRepeat()) {
//                    this.prevElapsedTime = this.play.getTotalTime();
//                    this.elapsedTime += this.play.getTotalTime();
//                } else {
//                    this.elapsedTime = 0.0F;
//                    this.isEnd = true;
//                }
//            }
//        }
//    }
    @Inject(method = "tick",at = @At("HEAD"), cancellable = true)
    public void tick(LivingEntityPatch<?> entitypatch, CallbackInfo ci) {
        if (entitypatch.getOriginal().hasEffect(WuKongEffects.DING.get())) {
            ci.cancel();
        }
    }
}

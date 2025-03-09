package com.p1nero.wukong.mixin;

import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(TrailParticle.class)
public abstract class TrailParticleMixin extends TextureSheetParticle {
    @Final
    @Shadow(remap = false)
    private LivingEntityPatch<?> entitypatch;

    protected TrailParticleMixin(ClientLevel p_108323_, double p_108324_, double p_108325_, double p_108326_) {
        super(p_108323_, p_108324_, p_108325_, p_108326_);
    }

    /**
     * 发光？？
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lyesman/epicfight/client/particle/TrailParticle;getLightColor(F)I"))
    private int wukong$render(TrailParticle instance, float v) {
        if (EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)) {
            return LightTexture.FULL_BRIGHT;
        }
        BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }
}

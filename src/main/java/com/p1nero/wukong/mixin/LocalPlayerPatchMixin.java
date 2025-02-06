package com.p1nero.wukong.mixin;


import com.p1nero.wukong.entity.FakeWukongEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

//小猴不会被锁
@Mixin(value = LocalPlayerPatch.class,remap = false)
public class LocalPlayerPatchMixin {

    @Shadow
    private LivingEntity rayTarget;
    @Inject(method = "clientTick",at = @At("TAIL"))
    public void tick(LivingEvent.LivingUpdateEvent event, CallbackInfo ci) {
        if (rayTarget instanceof FakeWukongEntity) {
            rayTarget = null;
        }
    }
}

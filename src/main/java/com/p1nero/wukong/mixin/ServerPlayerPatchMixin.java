package com.p1nero.wukong.mixin;

import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 退寸成功后一段时间内部耗耐力
 */
@Mixin(value = ServerPlayerPatch.class, remap = false)
public abstract class ServerPlayerPatchMixin extends PlayerPatch<ServerPlayer> {
    @Inject(method = "consumeStamina", at = @At(value = "HEAD"), cancellable = true)
    private void inject(float amount, CallbackInfoReturnable<Boolean> cir){
        SkillDataManager manager = this.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
        if(manager.hasData(ThrustHeavyAttack.DODGE_SUCCESS_TIMER) && manager.getDataValue(ThrustHeavyAttack.DODGE_SUCCESS_TIMER) > 0){
            cir.setReturnValue(false);
        }
    }
    @Shadow
    private LivingEntity attackTarget;
    @Inject(method = "setAttackTarget",at = @At("TAIL"))
    public void tick(LivingEntity entity, CallbackInfo ci) {
        if (entity instanceof FakeWukongEntity) {
            attackTarget = null;
        }
    }
}

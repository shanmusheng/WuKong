package com.p1nero.wukong.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * 金箍棒缩放
 */
@Mixin(value = RenderItemBase.class,remap = false)
public class RenderItemBaseMixin {
    @Inject(method = "renderItemInHand", at = @At("HEAD"))
    private void inject(ItemStack stack, LivingEntityPatch<?> entitypatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight, CallbackInfo ci){
        stack.getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent(capabilityItem -> {
            if(capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF) && stack.hasTag()/*防止多余nbt*/){
                CompoundTag tag = stack.getOrCreateTag();
                if(tag.getBoolean("WK_shouldScaleItem")){
                    poseStack.scale(tag.getFloat("WK_XScale"), tag.getFloat("WK_YScale"), tag.getFloat("WK_ZScale"));
                }
            }
        });
    }
}

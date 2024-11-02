package com.p1nero.wukong.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * 在副手渲染赤潮
 */
@OnlyIn(Dist.CLIENT)
public class RenderRedTide extends RenderItemBase {
    @Override
    public void renderItemInHand(ItemStack stack, LivingEntityPatch<?> entityPatch, InteractionHand hand, HumanoidArmature armature, OpenMatrix4f[] poses, MultiBufferSource buffer, PoseStack poseStack, int packedLight) {
        OpenMatrix4f modelMatrix = this.getCorrectionMatrix(stack, entityPatch, hand);
        Joint holdingHand = armature.toolL;
        OpenMatrix4f jointTransform = poses[holdingHand.getId()];
        modelMatrix.mulFront(jointTransform);
        poseStack.pushPose();
        this.mulPoseStack(poseStack, modelMatrix);
        ItemTransforms.TransformType transformType = ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
        Minecraft.getInstance().getItemInHandRenderer().renderItem(entityPatch.getOriginal(), stack, transformType, true, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}

package com.p1nero.wukong.mixin;

import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

/**
 * 实现虚化
 */
@Mixin(value = PatchedLivingEntityRenderer.class, remap = false)
public class PatchedLivingEntityRendererMixin {
    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/PoseStack;IF)V", at = @At(value = "INVOKE", target = "Lyesman/epicfight/api/client/model/AnimatedMesh;drawModelWithPose(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IFFFFILyesman/epicfight/api/model/Armature;[Lyesman/epicfight/api/utils/math/OpenMatrix4f;)V"), index = 6)
    private float modifyAlpha(float alpha) {
        if (Minecraft.getInstance().player != null) {
            LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(Minecraft.getInstance().player, LocalPlayerPatch.class);
            if (patch == null) {
                return alpha;
            }
            SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null) {
                SkillDataManager manager = container.getDataManager();
                if (manager.hasData(ThrustHeavyAttack.TRANSPARENT_TIMER) && manager.getDataValue(ThrustHeavyAttack.TRANSPARENT_TIMER) > 0) {
                    int current = manager.getDataValue(ThrustHeavyAttack.TRANSPARENT_TIMER);
                    float ratio = current / (float) ThrustHeavyAttack.MAX_TRANSPARENT_TIMER;
                    // 判断在哪个区间内并计算 alpha
                    if (ratio >= 0.8) {
                        // [1, 0.8] => alpha 从 0.9 渐变到 0.4
                        return (float) (1 - 0.8 * (1 - ratio) / 0.2);
                    } else if (ratio <= 0.2) {
                        // [0.2, 0] => alpha 从 0.4 渐变到 0.9
                        return (float) (0.2 + 0.8 * ((0.2 - ratio) / 0.2));
                    } else {
                        return 0.2F;
                    }
                }
            }
        }
        return alpha;
    }
}

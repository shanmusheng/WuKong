package com.p1nero.wukong.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.AnimationJudge;
import com.p1nero.wukong.item.JinGuBang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class JinGuBangRenderer extends GeoItemRenderer<JinGuBang> {
    private final Minecraft mc = Minecraft.getInstance();
    LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);

    public JinGuBangRenderer() {
        super(new AnimatedGeoModel<>() {
            @Override
            public ResourceLocation getModelLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "geo/item/jingubang.geo.json");
            }

            @Override
            public ResourceLocation getTextureLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang.png");
            }

            @Override
            public ResourceLocation getAnimationFileLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang.animation.json");
            }
        });

    }

    @Override
    public void render(GeoModel model, JinGuBang jinGuBang, float partialTicks, RenderType type, PoseStack matrixStackIn, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //美化
        alpha = 250f/255f;
        packedLightIn = 0xf000ff;
        if ((lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.isTwo(staticAnimation) && (lpp.getEntityState().getLevel() != 3))
        || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 2)) {
            green = 152f/225f;
            blue = 24f/225f;
            red = 1f;
        }
        if ((lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.isThree(staticAnimation) && (lpp.getEntityState().getLevel() != 3))
                || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 3)) {
            green = 127f/225f;
            blue = 39f/225f;
            red = 1f;
        }
        if ((lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.isFour(staticAnimation) && (lpp.getEntityState().getLevel() != 3))
                || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 4)) {
            green = 48/225f;
            blue = 33/225f;
            red = 1f;
        }
        super.render(model, jinGuBang, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }


}

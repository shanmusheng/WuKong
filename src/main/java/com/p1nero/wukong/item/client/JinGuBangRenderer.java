package com.p1nero.wukong.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.animation.AnimationJudge;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.item.JinGuBang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class JinGuBangRenderer extends GeoItemRenderer<JinGuBang> {

    public JinGuBangRenderer() {
        super(new AnimatedGeoModel<>() {
            private int tickCount = 0;
            private final ResourceLocation[] textures = {
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang1.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang2.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang3.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang4.png")
            };

            @Override
            public ResourceLocation getModelLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "geo/item/jingubang.geo.json");
            }

            @Override
            public ResourceLocation getTextureLocation(JinGuBang jinGuBang) {
                final Minecraft mc = Minecraft.getInstance();
                if(mc.player == null){
                    return textures[0];
                }
                tickCount = (tickCount + 1) % 4;
                return textures[tickCount];

            }


            @Override
            public ResourceLocation getAnimationFileLocation(JinGuBang jinGuBang) {
                final Minecraft mc = Minecraft.getInstance();
                LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
                if (lpp != null && (((lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.isGlow(staticAnimation)) && (lpp.getEntityState().getLevel() != 3)) || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() >= 1))) {
                    return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang_charge.animation.json");
                } else {
                    return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang.animation.json");
                }
            }
        });

    }

    @Override
    public void render(GeoModel model, JinGuBang jinGuBang, float partialTicks, RenderType type, PoseStack matrixStackIn, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //美化
        final Minecraft mc = Minecraft.getInstance();
        LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
        if(lpp != null && lpp.getEntityState().getLevel() != 3 && lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation){
            if (AnimationJudge.isQie(staticAnimation) && (lpp.getEntityState().getLevel() != 3)) {
                green = 222f/255f;
                blue = 200f/255f;
                red = 1.0f;
            }
            if (AnimationJudge.isTwo(staticAnimation) && (lpp.getEntityState().getLevel() != 3) || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 2)) {
                green = 152f/225f;
                blue = 24f/225f;
                red = 1f;
            }
            if (AnimationJudge.isThree(staticAnimation) && (lpp.getEntityState().getLevel() != 3) || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 3)) {
                green = 97f/225f;
                blue = 39f/225f;
                red = 1f;
            }
            if (AnimationJudge.isFour(staticAnimation) && (lpp.getEntityState().getLevel() != 3) || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() == 4)) {
                green = 48/225f;
                blue = 33/225f;
                red = 1f;
            }
        }
        
        super.render(model, jinGuBang, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, 0xf000ff, packedOverlayIn, red, green, blue, alpha);
    }


}

package com.p1nero.wukong.item.client;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.animation.AnimationJudge;
import com.p1nero.wukong.item.JinGuBang;
import com.p1nero.wukong.item.LostSword;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class LostSwordRenderer extends GeoItemRenderer<LostSword> {
    public LostSwordRenderer() {
        super(new AnimatedGeoModel<>() {
            private int tickCount = 0;
            private final ResourceLocation[] textures = {
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang1.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang2.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang3.png"),
                    new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang/jingubang4.png")
            };
            @Override
            public ResourceLocation getAnimationFileLocation(LostSword animatable) {
                final Minecraft mc = Minecraft.getInstance();
                LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
                if (lpp != null && (((lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.isGlow(staticAnimation)) && (lpp.getEntityState().getLevel() != 3)) || (AnimationJudge.isCharging(lpp) && lpp.getSkill(SkillSlots.WEAPON_INNATE).getStack() >= 1))) {
                    return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang_charge.animation.json");
                } else {
                    return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang.animation.json");
                }
            }

            @Override
            public ResourceLocation getModelLocation(LostSword object) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "geo/item/jingubang.geo.json");

            }

            @Override
            public ResourceLocation getTextureLocation(LostSword object) {
                final Minecraft mc = Minecraft.getInstance();
                if(mc.player == null){
                    return textures[0];
                }
                tickCount = (tickCount + 1) % 4;
                return textures[tickCount];
            }
        });
    }
}

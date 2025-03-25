package com.p1nero.wukong.item.client;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.item.DaShengArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DashengArmorModel extends AnimatedGeoModel<DaShengArmorItem> {
    @Override
    public ResourceLocation getModelLocation(DaShengArmorItem daShengArmorItem) {
        return new ResourceLocation(WukongMoveset.MOD_ID, "geo/item/dasheng.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DaShengArmorItem daShengArmorItem) {
        return new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/dasheng/dasheng_all.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DaShengArmorItem daShengArmorItem) {
        return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/dasheng.animation.json");
    }
}

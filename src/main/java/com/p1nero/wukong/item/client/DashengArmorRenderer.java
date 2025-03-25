package com.p1nero.wukong.item.client;

import com.p1nero.wukong.item.DaShengArmorItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class DashengArmorRenderer extends GeoArmorRenderer<DaShengArmorItem> {
    public DashengArmorRenderer() {
        super(new DashengArmorModel());
        this.headBone = "bipedHead";
        this.bodyBone = "bipedBody";
        this.rightArmBone = "bipedRightArm";
        this.leftArmBone = "bipedLeftArm";
        this.rightLegBone = "armorRightLeg";
        this.leftLegBone = "armorLeftLeg";
        this.rightBootBone = "armorRightBoot";
        this.leftBootBone = "armorLeftBoot";
    }
}
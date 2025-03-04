package com.p1nero.wukong.epicfight;

import yesman.epicfight.world.damagesource.SourceTag;

public enum WukongDamageSourceTags implements SourceTag {
    FAKE_WUKONG;
    final int id;

    WukongDamageSourceTags() {
        this.id = SourceTag.ENUM_MANAGER.assign(this);
    }
    @Override
    public int universalOrdinal() {
        return id;
    }
}

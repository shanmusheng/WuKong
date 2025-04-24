package com.p1nero.wukong.epicfight;

import yesman.epicfight.world.damagesource.SourceTag;

public enum KongQiDamageSourceTags implements SourceTag {
    FAKE_KONGQI;  // 定义了一个虚假的“KONGQI”伤害来源标签，表示一个特殊的伤害类型

    // 存储该伤害源的唯一ID
    final int id;

    // 构造函数，初始化时为该伤害源分配一个唯一的ID
    KongQiDamageSourceTags() {
        this.id = SourceTag.ENUM_MANAGER.assign(this);  // 使用枚举管理器分配一个唯一的ID
    }

    // 返回该伤害源的唯一标识符
    @Override
    public int universalOrdinal() {
        return id;  // 返回分配的唯一ID
    }
}

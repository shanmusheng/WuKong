package com.p1nero.wukong.epicfight;

import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.SkillCategory;

/**
 * 自定义的技能类别枚举，代表悟空的技能类型。
 * 这些技能类别包括棍势、气术、神法和豪毛等。
 * 每个技能类别具有保存、同步、可修改等特性。
 */
public enum WukongSkillCategories implements SkillCategory {

    // 棍势技能类别，用于棍法相关的技能
    STAFF_STYLE(true, true, true),

    // 气术技能类别，用于悟空的气功技能
    QI_SHU(true, true, true),

    // 神法技能类别，用于悟空的神秘法术技能
    SHEN_FA(true, true, true),

    // 豪毛技能类别，用于悟空的豪毛技能
    HAO_MAO(true, true, true);

    // 是否需要保存该技能类别的状态
    final boolean save;

    // 是否需要同步该技能类别的状态
    final boolean sync;

    // 是否可以修改该技能类别（即是否可以学习）
    final boolean modifiable;

    // 每个技能类别的唯一标识符，用于管理技能类别
    final int id;

    /**
     * 构造函数，用于初始化技能类别的属性。
     *
     * @param ShouldSave 是否需要保存该技能类别的状态
     * @param ShouldSync 是否需要同步该技能类别的状态
     * @param Modifiable 是否可以修改该技能类别（即是否可学习）
     */
    WukongSkillCategories(boolean ShouldSave, boolean ShouldSync, boolean Modifiable) {
        this.modifiable = Modifiable;  // 是否可修改
        this.save = ShouldSave;        // 是否需要保存
        this.sync = ShouldSync;        // 是否需要同步
        this.id = SkillCategory.ENUM_MANAGER.assign(this);  // 获取唯一标识符id
    }

    /**
     * 判断该技能类别的状态是否需要保存
     *
     * @return 是否需要保存
     */
    @Override
    public boolean shouldSave() {
        return this.save;
    }

    /**
     * 判断该技能类别的状态是否需要同步
     *
     * @return 是否需要同步
     */
    @Override
    public boolean shouldSynchronize() {
        return this.sync;
    }

    /**
     * 判断该技能类别是否可以学习（是否可以修改）
     *
     * @return 是否可以修改
     */
    @Override
    public boolean learnable() {
        return this.modifiable;
    }

    /**
     * 获取该技能类别的通用序号，唯一标识该类别
     *
     * @return 技能类别的唯一标识符
     */
    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
//详细注释说明：
//        WukongSkillCategories 枚举:
//
//        这个枚举定义了悟空模组中自定义的技能类别，代表了不同的技能类型（如棍势、气术等）。
//
//        每个技能类别有 save、sync 和 modifiable 三个属性，控制技能类别的行为。
//
//        枚举值:
//
//        STAFF_STYLE：棍法技能类别。
//
//        QI_SHU：气术技能类别。
//
//        SHEN_FA：神法技能类别。
//
//        HAO_MAO：豪毛技能类别。
//
//        构造函数:
//
//        枚举的构造函数用于初始化每个技能类别的特性。ShouldSave 表示是否需要保存技能状态，ShouldSync 表示是否需要同步状态，Modifiable 表示该技能是否可修改（即可学习）。
//
//        方法:
//
//        shouldSave()：返回是否需要保存该技能类别的状态。
//
//        shouldSynchronize()：返回是否需要同步该技能类别的状态。
//
//        learnable()：返回是否可以修改该技能类别（即是否可以学习）。
//
//        universalOrdinal()：返回该技能类别的唯一标识符，用于标识不同技能类别。
//
//        总结：
//        该类通过枚举的方式组织了悟空模组中的不同技能类别，并为每个类别定义了是否保存、同步和是否可学习的属性。通过这些属性，技能类别的行为可以被灵活控制，并且每个技能类别都有一个唯一的标识符。

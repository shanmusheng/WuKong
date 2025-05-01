package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.KongQiMoveset;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.*;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.*;
import com.p1nero.wukong.item.WukongItems;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;

//技能注册 注:平A攻击和技能是分开的
public class KongqiSkills {
    // 定义各种技能对象
    public static Skill SMASH_STYLE_SHAN_MU;  // 自定义的技能

    public static void registerSkills() {
        // 注册 Skill.createBuilder()是技能构造器 决定了 setCategory技能类型(也影响技能书的小图标):WEAPON_PASSIVE 武器被动技能   setResource资源消耗:NONE
        SkillManager.register(StaffPassive::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "shanmu");

         }
    public static void BuildSkills(SkillBuildEvent event) {
        SMASH_STYLE_SHAN_MU = event.build(WukongMoveset.MOD_ID, "shanmu");

    }

}

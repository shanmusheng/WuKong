package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class RedTideHeavyAttack extends WeaponInnateSkill {

    public static final SkillDataManager.SkillDataKey<Integer> COUNTER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//有效重击时间计数器

    public RedTideHeavyAttack(Builder<?> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getDataManager().registerData(COUNTER);
        super.onInitiate(container);
    }

    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        if(executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(COUNTER) > 0 && (executer.getSkill(SkillSlots.WEAPON_INNATE).isFull() || executer.getOriginal().isCreative())){
            executer.playAnimationSynchronized(WukongAnimations.RED_TIDE_SKILL_F, 0.0F);
        } else {
            executer.playAnimationSynchronized(WukongAnimations.RED_TIDE_SKILL, 0.0F);
        }
        super.executeOnServer(executer, args);
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }


    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(container.getDataManager().hasData(COUNTER)){
            container.getDataManager().setData(COUNTER, Math.max(0, container.getDataManager().getDataValue(COUNTER) - 1));
        } else {
            container.getDataManager().registerData(COUNTER);
        }
    }
}

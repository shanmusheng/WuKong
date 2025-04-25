package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 无敌时间缩短到后摇结束
 */
public class WukongDodgeAnimation extends DodgeAnimation {
    public WukongDodgeAnimation(float convertTime, String path, float width, float height, Armature armature) {
        super(convertTime, path, width, height, armature);
    }
    public WukongDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature) {
        this(convertTime, delayTime, path, width, height, armature, false);
    }
    public WukongDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature, boolean isPerfect) {
        super(convertTime, delayTime, path, width, height, armature);
        //蓄力中闪避如果没有完美闪避则清空棍势
        this.addEvents(AnimationEvent.TimeStampedEvent.create(delayTime, ((livingEntityPatch, staticAnimation, objects) -> {
            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch && WukongWeaponCategories.isWeaponValid(livingEntityPatch)){
                serverPlayerPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                    SkillContainer weaponInnate = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                    //立棍要进行完美闪避的判断
                    if(weaponInnate.getDataManager().hasData(SmashHeavyAttack.IS_CHARGING) && weaponInnate.getDataManager().getDataValue(SmashHeavyAttack.IS_CHARGING) && !wkPlayer.isPerfectDodge() && !isPerfect){
                        weaponInnate.getSkill().setConsumptionSynchronize(serverPlayerPatch, 1);
                        weaponInnate.getSkill().setStackSynchronize(serverPlayerPatch, 0);
                    }
                    if(weaponInnate.getDataManager().hasData(SmashHeavyAttack.IS_CHARGING)){
                        weaponInnate.getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, false, serverPlayerPatch.getOriginal());//无论如何都要中断蓄力
                    }
                    //戳棍中断蓄力
                    if(weaponInnate.getDataManager().hasData(ThrustHeavyAttack.IS_CHARGING) && weaponInnate.getDataManager().getDataValue(ThrustHeavyAttack.IS_CHARGING)){
                        weaponInnate.getSkill().setConsumptionSynchronize(serverPlayerPatch, 1);
                        weaponInnate.getSkill().setStackSynchronize(serverPlayerPatch, 0);
                        weaponInnate.getDataManager().setDataSync(ThrustHeavyAttack.IS_CHARGING, false, serverPlayerPatch.getOriginal());
                    }
                });
            }
        }), AnimationEvent.Side.SERVER));
        this.stateSpectrumBlueprint.clear()
                .newTimePair(0.0F, delayTime)
                    .addState(EntityState.TURNING_LOCKED, true)//不能转向
                    .addState(EntityState.MOVEMENT_LOCKED, true)//不能移动
                    .addState(EntityState.UPDATE_LIVING_MOTION, false)//不能更新动作
                    .addState(EntityState.CAN_BASIC_ATTACK, false)//不能普功
                    .addState(EntityState.CAN_SKILL_EXECUTION, false)//不能释放技能
                    .addState(EntityState.INACTION, true)//不能切换物品
                .newTimePair(0.0F, delayTime)//区别就在这里，把闪避时间缩短到后摇时间 delayTime 后摇结束时间
                    .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR);//ATTACK_RESULT 受击状态
    }

    /**
     * 触发完美闪避才改状态
     */
    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        if(entityPatch instanceof ServerPlayerPatch serverPlayerPatch){
            serverPlayerPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                wkPlayer.setPerfectDodge(false);
            });
            SkillContainer weaponInnate = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            //允许凤穿花
            if(weaponInnate.getDataManager().hasData(ThrustHeavyAttack.FENGCHUANHUA_TIMER)){
                weaponInnate.getDataManager().setDataSync(ThrustHeavyAttack.FENGCHUANHUA_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
            }
        }
    }

}

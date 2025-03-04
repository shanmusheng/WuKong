package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.client.events.CameraAnim;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 尝试修改动画播放的move lock
 * 后面直接监听输入事件取消input了。。
 */
public class StaffSpinAttackAnimation extends AttackAnimation {
    private final boolean isTwoHand;
    public StaffSpinAttackAnimation(float end, HumanoidArmature biped, String path, float damageMultiplier, boolean isTwoHand){
        super(0.10F, path, biped,
                        new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, end, 0.26F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, end, 0.51F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, end, 0.76F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, end, end, biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)));
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));
        this.newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, true)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        this.isTwoHand = isTwoHand;
        if(isTwoHand){
            this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        }
    }

    protected Vec3 getCoordVector(LivingEntityPatch<?> entityPatch, DynamicAnimation dynamicAnimation) {
        Vec3 vec3 = super.getCoordVector(entityPatch, dynamicAnimation);
        if (!isTwoHand) {
            vec3 = vec3.scale(0.0);
        }

        return vec3;
    }

    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        if(entityPatch.isLogicalClient()){
            if(isTwoHand && entityPatch instanceof LocalPlayerPatch localPlayerPatch && !localPlayerPatch.isTargetLockedOn()){
                CameraAnim.zoomIn(new Vec3f(-1.0F, 0.0F, 1.25F), 20);
            }
        }
        super.begin(entityPatch);
    }

    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(entityPatch instanceof ServerPlayerPatch serverPlayerPatch && WukongWeaponCategories.isWeaponValid(entityPatch)){
            SkillDataManager manager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager();
            if(serverPlayerPatch.hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue()) && manager.getDataValue(StaffPassive.STAFF_SPIN_KEY_PRESSED)){
                boolean twoHand = manager.getDataValue(StaffPassive.W_PRESSED);
                entityPatch.reserveAnimation(twoHand ? WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP : WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP);
                serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
            }

        }
        if(entityPatch.isLogicalClient() && entityPatch instanceof LocalPlayerPatch && CameraAnim.isAiming()){
            CameraAnim.zoomOut(20);//保险
        }
    }

}

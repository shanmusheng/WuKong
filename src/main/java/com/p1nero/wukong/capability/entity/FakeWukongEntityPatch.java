package com.p1nero.wukong.capability.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.WukongDamageSourceTags;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class FakeWukongEntityPatch extends HumanoidMobPatch<FakeWukongEntity> {
    public static final CombatBehaviors.Builder<HumanoidMobPatch<?>> WK_STAFF = CombatBehaviors.<HumanoidMobPatch<?>>builder()
            .newBehaviorSeries(
                    CombatBehaviors.BehaviorSeries.<HumanoidMobPatch<?>>builder().weight(100.0F).canBeInterrupted(false).looping(false)
                            .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(WukongAnimations.STAFF_AUTO1).withinEyeHeight().withinDistance(0.0D, 2.5D))
                            .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(WukongAnimations.STAFF_AUTO2).withinEyeHeight().withinDistance(0.0D, 2.5D))
                            .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(WukongAnimations.STAFF_AUTO3).withinEyeHeight().withinDistance(0.0D, 2.5D))
                            .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(WukongAnimations.STAFF_AUTO4).withinEyeHeight().withinDistance(0.0D, 2.5D))
            );

    public FakeWukongEntityPatch() {
        super(Faction.VILLAGER);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initAnimator(ClientAnimator clientAnimator) {
        animator.addLivingAnimation(LivingMotions.IDLE, WukongAnimations.IDLE);
        animator.addLivingAnimation(LivingMotions.WALK, WukongAnimations.RUN);
        animator.addLivingAnimation(LivingMotions.CHASE, WukongAnimations.RUN);
        animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_COMMON_NEUTRALIZED);
    }

    @Override
    public void updateMotion(boolean b) {
        super.commonAggressiveMobUpdateMotion(b);
    }

    protected void setWeaponMotions() {
        this.weaponAttackMotions = Maps.newHashMap();
        this.weaponAttackMotions.put(WukongWeaponCategories.WK_STAFF, ImmutableMap.of(WukongStyles.SMASH, WK_STAFF));
    }

    @Override
    public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
        AttackResult result = super.attack(damageSource, target, hand);
        if(result.resultType.dealtDamage()){
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getEntityPatch(this.getOriginal().getOwner(), ServerPlayerPatch.class);
            if(serverPlayerPatch != null){
                SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                float value = container.getResource() + result.damage * Config.FAKE_ENTITY_DAMAGE_RATE.get().floatValue();
                container.getSkill().setConsumptionSynchronize(serverPlayerPatch, value);
            }
        }
        return result;
    }

    /**
     * 伤害源应来自主人
     */
    @Override
    public EpicFightDamageSource getDamageSource(StaticAnimation animation, InteractionHand hand) {
        EpicFightDamageSource newEpicFightDamage;
        if(this.getOriginal().getOwner() != null){
            newEpicFightDamage = EpicFightCapabilities.getEntityPatch(this.getOriginal().getOwner(), PlayerPatch.class).getDamageSource(animation, hand);
        } else {
            newEpicFightDamage = super.getDamageSource(animation, hand);
        }
        newEpicFightDamage.addTag(WukongDamageSourceTags.FAKE_WUKONG);
        return newEpicFightDamage;
    }

    @Override
    public float getModifiedBaseDamage(float baseDamage) {
        return baseDamage * Config.FAKE_ENTITY_DAMAGE_RATE.get().floatValue();
    }

    @Override
    public boolean isTeammate(Entity entityIn) {
        if(entityIn instanceof FakeWukongEntity fakeWukongEntity && fakeWukongEntity.getOwner() == this.getOriginal().getOwner()){
            return false;
        }
        if(entityIn.equals(this.getOriginal().getOwner())){
            return false;
        }
        return super.isTeammate(entityIn);
    }
}
package com.p1nero.wukong.capability.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.p1nero.wukong.entity.FakeWukongEntity;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
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
                            .nextBehavior(CombatBehaviors.Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(WukongAnimations.STAFF_AUTO5).withinEyeHeight().withinDistance(0.0D, 2.5D))
            );

    public FakeWukongEntityPatch() {
        super(Faction.UNDEAD);
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

    /**
     * 伤害源应来自主人
     */
    @Override
    public EpicFightDamageSource getDamageSource(StaticAnimation animation, InteractionHand hand) {
        if(this.getOriginal().getOwner() != null){
            EpicFightDamageSource damageSource = EpicFightDamageSource.commonEntityDamageSource("player", this.original.getOwner(), animation);
            damageSource.setImpact(this.getImpact(hand));
            damageSource.setArmorNegation(this.getArmorNegation(hand));
            damageSource.setHurtItem(this.getOriginal().getOwner().getItemInHand(hand));
            return damageSource;
        }
        return super.getDamageSource(animation, hand);
    }

    @Override
    public float getModifiedBaseDamage(float baseDamage) {
        return baseDamage * 0.5F;
    }

}
package com.p1nero.wukong.epicfight.animation.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.*;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class LoopedActionAnimation extends StaticAnimation {
    public LoopedActionAnimation(String path, Armature armature) {
        super(true, path, armature);
    }

//    @OnlyIn(Dist.CLIENT)
//    public Layer.Priority getPriority() {
//        return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Priority.LOWEST);
//    }


    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        entityPatch.updateEntityState();
        if (entityPatch.isLogicalClient()) {
            entityPatch.getClientAnimator().resetMotion();
            entityPatch.getClientAnimator().resetCompositeMotion();
            entityPatch.getClientAnimator().getPlayerFor(this).setReversed(false);
        }

        entityPatch.cancelAnyAction();
        if (this.getProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT).orElse(false) && !entityPatch.isLogicalClient()) {
            entityPatch.getOriginal().setDeltaMovement(0.0, entityPatch.getOriginal().getDeltaMovement().y, 0.0);
        }

        entityPatch.correctRotation();
        MoveCoordFunctions.MoveCoordSetter moveCoordSetter = this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_BEGIN).orElse(MoveCoordFunctions.RAW_COORD);
        moveCoordSetter.set(this, entityPatch, entityPatch.getArmature().getActionAnimationCoord());

    }

    public void tick(LivingEntityPatch<?> entityPatch) {
        super.tick(entityPatch);
        entityPatch.getOriginal().animationSpeed = 0.0F;
        this.move(entityPatch, this);
    }

    @Override
    public void linkTick(LivingEntityPatch<?> entityPatch, DynamicAnimation linkAnimation) {
        this.move(entityPatch, linkAnimation);
    }

    protected void move(LivingEntityPatch<?> entityPatch, DynamicAnimation animation) {
        LivingEntity livingentity = entityPatch.getOriginal();
        Vec3 vec3 = this.getCoordVector(entityPatch, animation);
        livingentity.move(MoverType.SELF, vec3);
    }

    public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
        if (this.getProperty(AnimationProperty.ActionAnimationProperty.COORD).isEmpty()) {
            JointTransform jt = pose.getOrDefaultTransform("Root");
            Vec3f jointPosition = jt.translation();
            OpenMatrix4f toRootTransformApplied = entitypatch.getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
            OpenMatrix4f toOrigin = OpenMatrix4f.invert(toRootTransformApplied, null);
            Vec3f worldPosition = OpenMatrix4f.transform3v(toRootTransformApplied, jointPosition, null);
            worldPosition.x = 0.0F;
            worldPosition.y = Math.min(worldPosition.y, 0.0F);
            worldPosition.z = 0.0F;
            OpenMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
            jointPosition.x = worldPosition.x;
            jointPosition.y = worldPosition.y;
            jointPosition.z = worldPosition.z;
        }

        super.modifyPose(animation, pose, entitypatch, time, partialTicks);
    }

    protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
        AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
        TimePairList coordUpdateTime = this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_UPDATE_TIME).orElse(null);
        boolean isCoordUpdateTime = true;
        if (coordUpdateTime != null && !coordUpdateTime.isTimeInPairs(player.getElapsedTime())) {
            isCoordUpdateTime = false;
        }

        MoveCoordFunctions.MoveCoordSetter moveCoordsetter = isCoordUpdateTime ? this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_SET_TICK).orElse(null) : MoveCoordFunctions.RAW_COORD;
        TransformSheet rootCoord;
        if (moveCoordsetter != null) {
            rootCoord = animation instanceof LinkAnimation ? animation.getCoord() : entitypatch.getArmature().getActionAnimationCoord();
            moveCoordsetter.set(animation, entitypatch, rootCoord);
        }

        if (animation instanceof LinkAnimation) {
            rootCoord = animation.getCoord();
        } else {
            rootCoord = entitypatch.getArmature().getActionAnimationCoord();
            if (rootCoord == null) {
                rootCoord = animation.getCoord();
            }
        }

        MoveCoordFunctions.MoveCoordGetter moveGetter = isCoordUpdateTime ? this.getProperty(AnimationProperty.ActionAnimationProperty.COORD_GET).orElse(MoveCoordFunctions.DIFF_FROM_PREV_COORD) : MoveCoordFunctions.DIFF_FROM_PREV_COORD;
        Vec3f move = moveGetter.get(animation, entitypatch, rootCoord);
        LivingEntity livingentity = entitypatch.getOriginal();
        Vec3 motion = livingentity.getDeltaMovement();

        livingentity.setDeltaMovement(motion.x, 0.0, motion.z);
        return move.toDoubleVector();
    }
}

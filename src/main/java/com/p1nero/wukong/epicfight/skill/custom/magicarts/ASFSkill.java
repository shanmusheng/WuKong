package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 安身法
 */
public class ASFSkill extends Skill {
    public static SkillDataManager.SkillDataKey<Integer> COOLDOWN_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//冷却计时器
    public static SkillDataManager.SkillDataKey<Integer> EXIST_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//存在计时器
    public static SkillDataManager.SkillDataKey<Float> X = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
    public static SkillDataManager.SkillDataKey<Float> Y = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
    public static SkillDataManager.SkillDataKey<Float> Z = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
    public static final int MAX_COOLDOWN_TIME = 1000;//50s
    public static final int MAX_EXIST_TIME = 510;//25.5s
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0198ac114510");

    public ASFSkill(Builder builder) {
        super(builder);
    }

    public static ASFSkill.Builder create() {
        return new ASFSkill.Builder().setCategory(WukongSkillCategories.QI_SHU).setResource(Skill.Resource.NONE);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, COOLDOWN_TIMER, 0);
        SkillDataRegister.register(manager, EXIST_TIMER, 0);
        SkillDataRegister.register(manager, X, 0.0f);
        SkillDataRegister.register(manager, Y, 0.0f);
        SkillDataRegister.register(manager, Z, 0.0f);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        executer.playSound(WuKongSounds.AN_SHEN.get(),1.0F, 0.0F, 0.0F);
        executer.playAnimationSynchronized(WukongAnimations.AN_SHEN_FA,0.0F);
        executer.getOriginal().setHealth((float) (executer.getOriginal().getMaxHealth() * 0.25 + executer.getOriginal().getHealth()));
        SkillContainer container = executer.getSkill(this);
        SkillDataManager dataManager = container.getDataManager();
        dataManager.setDataSync(COOLDOWN_TIMER, MAX_COOLDOWN_TIME, executer.getOriginal());
        dataManager.setDataSync(EXIST_TIMER, MAX_EXIST_TIME, executer.getOriginal());
        dataManager.setDataSync(X, (float)executer.getOriginal().getX(), executer.getOriginal());
        dataManager.setDataSync(Y, (float)executer.getOriginal().getY(), executer.getOriginal());
        dataManager.setDataSync(Z, (float)executer.getOriginal().getZ(), executer.getOriginal());
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
                if (event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation() == WukongAnimations.AN_SHEN_FA) {
                    event.setResult(AttackResult.ResultType.MISSED);
                }
        });
    }
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        Vec3 playerPos = new Vec3(manager.getDataValue(X),manager.getDataValue(Y),manager.getDataValue(Z));
        if (!container.getExecuter().isLogicalClient()) {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            if(manager.getDataValue(COOLDOWN_TIMER) > 0){
                manager.setDataSync(COOLDOWN_TIMER, manager.getDataValue(COOLDOWN_TIMER) - 1, serverPlayer);
            }
            if(manager.getDataValue(EXIST_TIMER) > 0){
                manager.setDataSync(EXIST_TIMER, manager.getDataValue(EXIST_TIMER) - 1, serverPlayer);
            }
            if (manager.getDataValue(EXIST_TIMER) > 0) {
                createFireCircles(serverPlayer, playerPos,manager);
                createRepelCircle(serverPlayer, playerPos,manager);
            }
        }
    }
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return container.getDataManager().getDataValue(COOLDOWN_TIMER) > 0;
    }
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        poseStack.pushPose();
        poseStack.translate(0, (float)gui.getSlidingProgression(), 0);
        RenderSystem.setShaderTexture(0, getSkillTexture());
        GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
        float second = (container.getDataManager().getDataValue(COOLDOWN_TIMER) / 20.0F);
        GuiComponent.drawString(poseStack ,gui.font, String.format("%.1f", second), (int) (second > 10 ? (x + 3) : (x + 6)), (int) (y + 6), 16777215);
    }
    @Override
    public boolean canExecute(PlayerPatch<?> executer) {
        return super.canExecute(executer) && (executer.getOriginal().isCreative() || executer.getSkill(this).getDataManager().getDataValue(COOLDOWN_TIMER) <= 0);
    }

    public static class Builder extends Skill.Builder<ASFSkill> {

        protected StaticAnimationProvider preF, preB, postF, post;

        public ASFSkill.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public ASFSkill.Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public ASFSkill.Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public ASFSkill.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

    }
    private void createFireCircles(ServerPlayer player,Vec3 position,SkillDataManager dataManager) {
        if (!(player.getCommandSenderWorld() instanceof ServerLevel level)) {
            return;
        }
        double radius = 4.7;
        int particleCount = 360;
        for (int i = 0; i < particleCount; i++) {
            if (dataManager.getDataValue(EXIST_TIMER) <= 0) {
                break;
            }
            double angle = i * (Math.PI * 2 / particleCount);
            double x = position.x + radius * Math.cos(angle);
            double z = position.z + radius * Math.sin(angle);
            double y = position.y + 0;
            level.sendParticles(ParticleTypes.FLAME, x, y, z, 10, 0, 0, 0, 0);

        }
    }
    public void  createRepelCircle(ServerPlayer player,Vec3 position,SkillDataManager dataManager) {
        double range = 4.7;  // 设定范围（4.7米）
        double knockbackStrength = 0.24;  // 击退强度（控制击退的远近）
        List<LivingEntity> nearbyEntities = player.getLevel().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(150), entity -> entity != player && entity.isAlive());
        List<Player> nearbyPlayer = player.getLevel().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(150), Player::isAlive);
        for (Player player1 : nearbyPlayer) {
            if (dataManager.getDataValue(EXIST_TIMER) <= 0) {
                break;
            }
            ServerPlayerPatch pp = EpicFightCapabilities.getEntityPatch(player1, ServerPlayerPatch.class);
            SkillContainer container = pp.getSkill(SkillSlots.WEAPON_INNATE);
            float value = container.getResource() + 1;
            Vec3 monsterPos = player.position();
            double deltaX = monsterPos.x - position.x;
            double deltaY = monsterPos.y - position.y;
            double deltaZ = monsterPos.z - position.z;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance <= range) {
                pp.getSkill(SkillSlots.WEAPON_INNATE).getSkill().setConsumptionSynchronize(pp, value);
            }
        }
        for (LivingEntity entity : nearbyEntities) {
            if (dataManager.getDataValue(EXIST_TIMER) <= 0) {
                break;
            }
            Vec3 monsterPos = entity.position();
            double deltaX = monsterPos.x - position.x;
            double deltaY = monsterPos.y - position.y;
            double deltaZ = monsterPos.z - position.z;
            if (entity instanceof Monster) {
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
                if (distance <= range) {
                    entity.setSecondsOnFire(1);
                    if (Math.abs(deltaZ) > Math.abs(deltaX)) {
                        if (deltaZ > 0) {
                            entity.push(0, 0, knockbackStrength);
                        } else {
                            entity.push(0, 0, -knockbackStrength);
                        }
                    } else {
                        if (deltaX > 0) {
                            entity.push(knockbackStrength, 0, 0);
                        } else {
                            entity.push(-knockbackStrength, 0, 0);
                        }
                    }
                }
            }
        }
    }
}

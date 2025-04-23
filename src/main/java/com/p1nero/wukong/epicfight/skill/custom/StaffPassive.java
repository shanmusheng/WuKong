package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.animation.custom.StaffSpinAttackAnimation;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketRelay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 实现棍花技能和闪避相关功能。
 * 包括棍花技能触发、技能动画、格挡、能量消耗和恢复等。
 */
public class StaffPassive extends Skill {

    // 存储技能数据的键，记录棍花技能是否按下，以及 W 键是否按下
    public static SkillDataManager.SkillDataKey<Boolean> STAFF_SPIN_KEY_PRESSED;
    public static SkillDataManager.SkillDataKey<Boolean> W_PRESSED;
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac191981"); // 用于事件监听的唯一标识符

    // 注册技能数据键
    public static void register(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 创建布尔型数据键，用于记录是否按下了棍花键和 W 键
            STAFF_SPIN_KEY_PRESSED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            W_PRESSED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
        });
    }

    // 构造函数
    public StaffPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    /**
     * 在技能初始化时调用，设置初始状态。
     */
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();

        // 注册棍花技能按键和 W 键按键状态
        SkillDataRegister.register(manager, STAFF_SPIN_KEY_PRESSED, false);
        SkillDataRegister.register(manager, W_PRESSED, false);

        // 自动学习并设置闪避技能为悟空的闪避技能
        Skill dodge = container.getExecuter().getSkill(SkillSlots.DODGE).getSkill();
        if (dodge != WukongSkills.WUKONG_DODGE) {
            // 如果当前技能不是悟空的闪避技能，则替换为悟空的闪避
            container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(WukongSkills.WUKONG_DODGE);
            container.getExecuter().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer ->
                    wkPlayer.setLastDodgeSkill(dodge == null ? "" : dodge.toString()));
        }

        // 在棍花技能期间禁止玩家移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            // 如果玩家处于战斗模式并且按下了棍花技能键，则禁用玩家的所有移动输入
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.STAFF_FLOWER.isDown()) {
                Input input = event.getMovementInput();
                // 禁止所有移动操作
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
                clientPlayer.setSprinting(false); // 禁止冲刺
                clientPlayer.sprintTriggerTime = -1; // 重置冲刺时间
                Minecraft mc = Minecraft.getInstance();
                ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false); // 禁用冲刺键
            }
        }));

        // 监听伤害事件，检查是否需要格挡
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            // 如果当前动画是棍花攻击动画且伤害来源可以被格挡
            if (event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation() instanceof StaffSpinAttackAnimation &&
                    (canBeBlocked(event.getDamageSource().getDirectEntity()) || event.getDamageSource().isProjectile())) {
                // 判断是否可以格挡
                if (!isBlocked(event.getDamageSource(), event.getPlayerPatch().getOriginal())) {
                    // 不可以格挡
                    return;
                }
                event.setCanceled(true); // 取消攻击事件，表示格挡成功
                event.setResult(AttackResult.ResultType.BLOCKED); // 设置为格挡结果

                // 格挡后更新格挡者和攻击者的状态
                LivingEntityPatch<?> attackerPatch = (LivingEntityPatch<?>) EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                if (attackerPatch != null) {
                    attackerPatch.setLastAttackEntity(event.getPlayerPatch().getOriginal());
                }
                Entity directEntity = event.getDamageSource().getDirectEntity();
                LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>) EpicFightCapabilities.getEntityPatch(directEntity, LivingEntityPatch.class);
                if (entityPatch != null) {
                    entityPatch.onAttackBlocked(event.getDamageSource(), event.getPlayerPatch());
                }

                // 显示格挡特效
                showBlockedEffect(event.getPlayerPatch(), event.getDamageSource().getDirectEntity());
                // 恢复能量
                SkillContainer skillContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                Skill skill = skillContainer.getSkill();
                if (skill != null) {
                    skillContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), skillContainer.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
            }
        }));

        // 伤害处理后增加棍势，提升技能能量
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (dealtDamageEvent -> {
            StaticAnimation animation = dealtDamageEvent.getDamageSource().getAnimation();
            // 判断是否是棍花攻击，如果是则增加能量
            if (animation.equals(WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP) || animation.equals(WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP)) {
                SkillContainer skillContainer = dealtDamageEvent.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                Skill skill = skillContainer.getSkill();
                if (skill != null) {
                    skillContainer.getSkill().setConsumptionSynchronize(dealtDamageEvent.getPlayerPatch(), skillContainer.getResource() + Config.CHARGING_SPEED.get().floatValue() * 4.5F);
                }
            }
        }));

        // 替换默认的闪避技能为悟空的闪避技能
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event -> {
            PlayerPatch<?> executer = event.getPlayerPatch();
            Skill ordinalSkill = event.getSkillContainer().getSkill();
            // 如果当前技能不是闪避，则跳过
            if (!ordinalSkill.getCategory().equals(SkillCategories.DODGE)) {
                return;
            }
            int dodgeId = event.getSkillContainer().getSlotId();
            if (executer.isLogicalClient()) {
                // 如果玩家没有使用悟空的闪避技能且体力足够，则替换为悟空的闪避技能
                if (!ordinalSkill.equals(WukongSkills.WUKONG_DODGE) && executer.hasStamina(this.getConsumption())) {
                    executer.getSkill(SkillSlots.DODGE).setSkill(WukongSkills.WUKONG_DODGE);
                    // 发送同步请求到服务器
                    EpicFightNetworkManager.sendToServer(new CPChangeSkill(dodgeId, -1, WukongSkills.WUKONG_DODGE.toString(), false));
                    executer.getSkill(SkillSlots.DODGE).sendExecuteRequest((LocalPlayerPatch) executer, ClientEngine.getInstance().controllEngine);
                    executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                        wkPlayer.setLastDodgeSkill(ordinalSkill.toString());
                        PacketRelay.syncPlayer(((LocalPlayer) executer.getOriginal()));
                    });
                    event.setCanceled(true); // 取消默认闪避执行
                }
            }
        }));
    }

    /**
     * 当技能被移除时，移除事件监听器和恢复状态。
     */
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        // 移除所有注册的事件监听器
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);

        // 恢复闪避技能
        if (!container.getExecuter().isLogicalClient()) {
            PacketRelay.syncPlayer(((ServerPlayer) container.getExecuter().getOriginal()));
        }
        container.getExecuter().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            if (wkPlayer.getLastDodgeSkill().isEmpty()) {
                container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(null);
            } else {
                container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(SkillManager.getSkill(wkPlayer.getLastDodgeSkill()));
            }
        });
    }

/**
 *  判断目标实体是否可以被格挡
 */
    public static boolean canBeBlocked(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (Config.entitiesCanBeBlocked.isEmpty()) {
            // 加载配置中定义的可被格挡的实体
            Config.entitiesCanBeBlocked = Config.ENTITIES_CAN_BE_BLOCKED_BY_STAFF_FLOWER.get().stream()
                    .map(entityName -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName)))
                    .collect(Collectors.toSet());
        }
        return Config.entitiesCanBeBlocked.contains(entity.getType());
    }
    /**
     *  判断是否为正面攻击，并且是否可以被格挡 isBlocked:格挡判断
     */
    private boolean isBlocked(DamageSource damageSource, ServerPlayer player) {
        Vec3 sourceLocation = damageSource.getSourcePosition();
        if (sourceLocation != null) {
            Vec3 viewVector = player.getViewVector(1.0F); // 获取玩家的视角
            Vec3 toSourceLocation = sourceLocation.subtract((player).position()).normalize();
            if (toSourceLocation.dot(viewVector) > 0.0) { // 如果攻击来自正面
                if (damageSource instanceof EpicFightDamageSource epicFightDamageSource) {
                    return !epicFightDamageSource.hasTag(SourceTags.GUARD_PUNCTURE); // 判断是否有穿透格挡标志
                }
            }
        }
        return false; // 不是正面攻击或不支持格挡
    }

    // 显示格挡效果（音效和粒子效果）
    public static void showBlockedEffect(ServerPlayerPatch playerPatch, Entity directEntity) {
        playerPatch.playSound(EpicFightSounds.CLASH, -0.05F, 0.1F); // 播放格挡音效
        ServerPlayer serverPlayer = playerPatch.getOriginal();
        EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serverPlayer.getLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, directEntity);
    }

    /**
     * 每帧更新技能状态，控制棍花技能的执行。
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        // 判断当前武器是否有效、是否在战斗模式且是否在地面上
        if (!WukongWeaponCategories.isWeaponValid(container.getExecuter()) || !container.getExecuter().isBattleMode() || !container.getExecuter().getOriginal().isOnGround()) {
            return;
        }

        if(container.getExecuter().isLogicalClient()){
            // 客户端同步技能按键状态
            if(container.getDataManager().getDataValue(STAFF_SPIN_KEY_PRESSED) != WukongKeyMappings.STAFF_FLOWER.isDown()){
                container.getDataManager().setDataSync(STAFF_SPIN_KEY_PRESSED, WukongKeyMappings.STAFF_FLOWER.isDown(), ((LocalPlayer) container.getExecuter().getOriginal()));
            }
            if(container.getDataManager().getDataValue(W_PRESSED) != WukongKeyMappings.W.isDown()) {
                container.getDataManager().setDataSync(W_PRESSED, WukongKeyMappings.W.isDown(), ((LocalPlayer) container.getExecuter().getOriginal()));
            }
        } else {
            // 服务端执行棍花技能动画并消耗体力
            if(container.getDataManager().getDataValue(STAFF_SPIN_KEY_PRESSED) && container.getExecuter().hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue()) && !container.getExecuter().getEntityState().inaction()){
                boolean twoHand = container.getDataManager().getDataValue(W_PRESSED);
                // 根据是否按下 W 键决定使用单手还是双手棍花动画
                container.getExecuter().playAnimationSynchronized(twoHand ? WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP : WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP, 0.15F);
                container.getExecuter().consumeStamina(container.getExecuter().getOriginal().isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
            }
        }
    }
}

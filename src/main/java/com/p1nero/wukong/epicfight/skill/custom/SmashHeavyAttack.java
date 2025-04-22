package com.p1nero.wukong.epicfight.skill.custom;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.animation.custom.WukongDodgeAnimation;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.CloudStepSkill;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.TTTBSkill;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 劈棍重击技能类，代表一种特殊的棍法技能
 * 该技能包含重击、蓄力、衍生攻击等多种特性，并且具有各种动画效果。
 */
public class SmashHeavyAttack extends HeavyAttack {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static final int MAX_DERIVE_TIMER = Config.DERIVE_CHECK_TIME.get().intValue();  // 衍生攻击的最大有效时间
    public static final SkillDataManager.SkillDataKey<Boolean> IS_IN_SPECIAL_ATTACK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);  // 是否正在进行特殊攻击
    public static final SkillDataManager.SkillDataKey<Boolean> IS_SPECIAL_ATTACK_SUCCESS = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);  // 是否成功识破并触发特殊攻击
    public static SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);  // 衍生攻击的计时器
    public static SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);  // 是否可以进行第一段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);  // 是否可以进行第二段衍生
    protected final StaticAnimation[] animations;  // 存储不同等级的重击动画
    protected StaticAnimation deriveAnimation1;  // 第一段衍生攻击的动画
    protected StaticAnimation deriveAnimation2;  // 第二段衍生攻击的动画
    @NotNull
    protected StaticAnimation jumpAttackHeavy;  // 跳跃重击的动画
    @NotNull
    protected StaticAnimation charging;  // 蓄力动画
    @NotNull
    protected StaticAnimation chargePre;  // 蓄力前动画

    /**
     * 获取所有的重击攻击动画，包括普通的重击和衍生攻击
     *
     * @return 所有的重击动画列表
     */
    @Override
    public List<StaticAnimation> getHeavyAttacks() {
        List<StaticAnimation> staticAnimations = new java.util.ArrayList<>(List.of(animations));
        staticAnimations.add(deriveAnimation2);  // 添加第二段衍生攻击动画
        return staticAnimations;
    }

    /**
     * 创建并返回一个构建者实例，用于构建充能攻击。
     *
     * @return 构建器实例
     */
    public static Builder createChargedAttack() {
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    /**
     * 构造函数，用于初始化重击技能。
     *
     * @param builder 构建器实例，用于配置重击技能的动画等属性
     */
    public SmashHeavyAttack(Builder builder) {
        super(builder);
        charging = builder.chargingAnimation.get();
        chargePre = builder.pre.get();

        this.animations = new StaticAnimation[builder.animationProviders.length];
        for (int i = 0; i < builder.animationProviders.length; i++) {
            this.animations[i] = builder.animationProviders[i].get();
        }

        deriveAnimation1 = builder.derive1.get();
        deriveAnimation2 = builder.derive2.get();
        jumpAttackHeavy = builder.jumpAttackHeavy.get();
    }

    /**
     * 注册技能所需的内容，并初始化技能的相关数据。
     *
     * @param event 初始化事件
     */
    public static void register(final FMLCommonSetupEvent event) {
        HeavyAttack.register(event);
        event.enqueueWork(() -> {
            SmashHeavyAttack.CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            SmashHeavyAttack.DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
        });
    }

    /**
     * 在服务器端执行重击技能。
     * 包括判断是否能够使用跳跃重击、是否可以进行衍生攻击等。
     *
     * @param executer 执行技能的玩家
     * @param args 技能执行时的参数
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        dataManager.setDataSync(STARS_CONSUMED, container.getStack(), player);  // 更新星数

        // 如果可以进行跳跃重击，执行跳跃重击动画
        if (dataManager.getDataValue(CAN_JUMP_HEAVY)) {
            dataManager.setData(PROTECT_NEXT_FALL, true);  // 防止跳跃攻击时的跌落动作
            dataManager.setDataSync(CAN_JUMP_HEAVY, false, player);
            if (container.getStack() > 0) {
                executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);  // 播放对应的声音
            }
            executer.playAnimationSynchronized(jumpAttackHeavy, 0.15F);  // 播放跳跃重击动画
            resetConsumption(container, executer, false);  // 重置技能消耗
        } else if (player.isOnGround()) {
            // 处理长按期间的衍生攻击判断
            if (dataManager.getDataValue(DERIVE_TIMER) > 0) {
                if (dataManager.getDataValue(CAN_FIRST_DERIVE)) {
                    dataManager.setDataSync(CAN_FIRST_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);  // 防止跌落
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);  // 播放声音
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation1, 0.2F);  // 播放第一段衍生动画
                } else if (dataManager.getDataValue(CAN_SECOND_DERIVE)) {
                    dataManager.setDataSync(CAN_SECOND_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);  // 防止跌落
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);  // 播放声音
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation2, 0.2F);  // 播放第二段衍生动画
                }
            } else {
                // 启动重击，进行蓄力操作
                if (!dataManager.getDataValue(IS_CHARGING)) {
                    executer.playAnimationSynchronized(chargePre, 0.2F);  // 播放蓄力前动画
                }
            }
        }
        super.executeOnServer(executer, args);
    }

    /**
     * 初始化技能容器，设置技能所需的数据管理器和事件监听器。
     *
     * @param container 技能容器
     */
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, IS_IN_SPECIAL_ATTACK, false);
        SkillDataRegister.register(manager, IS_SPECIAL_ATTACK_SUCCESS, false);
        SkillDataRegister.register(manager, CAN_FIRST_DERIVE, false);
        SkillDataRegister.register(manager, CAN_SECOND_DERIVE, false);
        SkillDataRegister.register(manager, DERIVE_TIMER, 0);

        // 长按期间禁止跳跃
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.HEAVY.isDownWithoutConflictCheck()) {
                Input input = event.getMovementInput();
                input.jumping = false;  // 禁止跳跃
            }
        }));
        super.onInitiate(container);
    }

    /**
     * 在技能被移除时，移除所有相关的事件监听器。
     *
     * @param container 技能容器
     */
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID);
    }

    /**
     * 更新技能容器，处理技能状态和计时器的更新。
     *
     * @param container 技能容器
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if (!container.getExecuter().isLogicalClient()) {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            // 更新衍生计时器
            dataManager.setDataSync(DERIVE_TIMER, Math.max(dataManager.getDataValue(DERIVE_TIMER) - 1, 0), serverPlayer);
            dataManager.setDataSync(RED_TIMER, Math.max(dataManager.getDataValue(RED_TIMER) - 1, 0), serverPlayer);
            if (dataManager.getDataValue(DERIVE_TIMER) <= 0) {
                dataManager.setDataSync(CAN_FIRST_DERIVE, false, serverPlayer);
                dataManager.setDataSync(CAN_SECOND_DERIVE, false, serverPlayer);
            }

            // 处理蓄力状态
            if (dataManager.getDataValue(IS_CHARGING)) {
                if (!dataManager.getDataValue(KEY_PRESSING) || !serverPlayerPatch.hasStamina(Config.CHARGING_STAMINA_CONSUME.get().floatValue() + 0.1F)) {
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                    dataManager.setData(PROTECT_NEXT_FALL, true);  // 防止重击时的跌落
                    serverPlayerPatch.playAnimationSynchronized(animations[container.getStack()], 0.0F);  // 播放当前的重击动画
                    dataManager.setDataSync(STARS_CONSUMED, container.getStack(), serverPlayer);  // 设置星数
                    resetConsumption(container, serverPlayerPatch, true);
                }
            }
        }
    }

    /**
     * 获取道具上显示的工具提示内容。
     *
     * @param itemstack 物品堆
     * @param cap 物品能力
     * @param playerCap 玩家能力
     * @return 显示的工具提示
     */
    @Override
    public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        List<Component> list = Lists.newArrayList();
        list.add(new TranslatableComponent(this.getTranslationKey()).withStyle(ChatFormatting.GOLD).append(new TextComponent(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
        list.add(new TranslatableComponent("skill.wukong.smash_style.tooltip"));  // 添加技能描述
        return list;
    }

    /**
     * 构建器类，用于配置并创建一个新的重击技能实例。
     */
    public static class Builder extends Skill.Builder<SmashHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;  // 动画提供器数组
        protected StaticAnimationProvider derive1;  // 第一段衍生动画
        protected StaticAnimationProvider derive2;  // 第二段衍生动画
        protected StaticAnimationProvider jumpAttackHeavy;  // 跳跃重击动画
        StaticAnimationProvider chargingAnimation;  // 蓄力动画
        StaticAnimationProvider pre;  // 蓄力前动画

        public Builder() {
        }

        /**
         * 设置技能类别
         *
         * @param category 技能类别
         * @return 构建器实例
         */
        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        /**
         * 设置技能激活方式
         *
         * @param activateType 激活类型
         * @return 构建器实例
         */
        public Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        /**
         * 设置技能资源
         *
         * @param resource 技能资源
         * @return 构建器实例
         */
        public Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        /**
         * 设置创意模式标签
         *
         * @param tab 标签
         * @return 构建器实例
         */
        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        /**
         * 设置蓄力动画
         *
         * @param chargingAnimation 蓄力动画
         * @return 构建器实例
         */
        public Builder setChargingAnimation(StaticAnimationProvider chargingAnimation) {
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        /**
         * 设置蓄力前动画
         *
         * @param pre 蓄力前动画
         * @return 构建器实例
         */
        public Builder setChargePreAnimation(StaticAnimationProvider pre) {
            this.pre = pre;
            return this;
        }

        /**
         * 设置重击动画（0~4星重击）
         *
         * @param animationProviders 动画提供器数组
         * @return 构建器实例
         */
        public Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }

        /**
         * 设置可长按的衍生动画
         *
         * @param derive1 第一段衍生动画
         * @param derive2 第二段衍生动画
         * @return 构建器实例
         */
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            return this;
        }

        /**
         * 设置跳跃重击动画
         *
         * @param jumpAttackHeavy 跳跃重击动画
         * @return 构建器实例
         */
        public Builder setJumpAttackHeavy(StaticAnimationProvider jumpAttackHeavy) {
            this.jumpAttackHeavy = jumpAttackHeavy;
            return this;
        }
    }

}

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
 * 劈棍重击
 */
public class SmashHeavyAttack extends HeavyAttack {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static final int MAX_DERIVE_TIMER = Config.DERIVE_CHECK_TIME.get().intValue();//在此期间内再按才被视为衍生
    public static final SkillDataManager.SkillDataKey<Boolean> IS_IN_SPECIAL_ATTACK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否正在切手技
    public static final SkillDataManager.SkillDataKey<Boolean> IS_SPECIAL_ATTACK_SUCCESS = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否识破成功
    public static SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//衍生合法时间计时器
    public static SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第一段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第二段衍生
    protected final StaticAnimation[] animations;//0~4共有五种重击
    protected StaticAnimation deriveAnimation1;
    protected StaticAnimation deriveAnimation2;
    @NotNull
    protected StaticAnimation jumpAttackHeavy;
    @NotNull
    protected StaticAnimation charging;
    @NotNull
    protected StaticAnimation chargePre;

    @Override
    public List<StaticAnimation> getHeavyAttacks() {
        List<StaticAnimation> staticAnimations = new java.util.ArrayList<>(List.of(animations));
        staticAnimations.add(deriveAnimation2);
        return staticAnimations;
    }

    public static Builder createChargedAttack() {
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

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
     * 保险，yesman官方提供的解法
     */
    public static void register(final FMLCommonSetupEvent event) {
        HeavyAttack.register(event);
        event.enqueueWork(() -> {
            SmashHeavyAttack.CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            SmashHeavyAttack.DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
        });
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link SmashHeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        dataManager.setDataSync(STARS_CONSUMED, container.getStack(), player);//0星也是星！
        if (dataManager.getDataValue(CAN_JUMP_HEAVY)) {
            dataManager.setData(PROTECT_NEXT_FALL, true);//放里面，防止瞎按技能键就防坠机的bug
            //跳跃攻击，也消耗所有棍势
            dataManager.setDataSync(CAN_JUMP_HEAVY, false, player);
            if (container.getStack() > 0) {//0星是null会中断
                executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
            }
            executer.playAnimationSynchronized(jumpAttackHeavy, 0.15F);
            resetConsumption(container, executer, false);
        } else if (player.isOnGround()) {


            //铜头铁壁成功后的判断，可以马上放蓄力，算彩蛋但是清了棍势
            SkillContainer tongTouTieBi = executer.getSkill(WukongSkills.TONG_TOU_TIE_BI);
            if (tongTouTieBi != null) {
                SkillDataManager tDataManager = tongTouTieBi.getDataManager();
                if (tDataManager.hasData(TTTBSkill.TTTB_TIMER) && tDataManager.getDataValue(TTTBSkill.TTTB_TIMER) > 0 && container.getStack() > 0) {
                    executer.playAnimationSynchronized(animations[container.getStack()], 0.0F);
                    resetConsumption(container, executer, true);
                    super.executeOnServer(executer, args);
                    return;
                }
            }

            if (dataManager.getDataValue(DERIVE_TIMER) > 0) {
                if (dataManager.getDataValue(CAN_FIRST_DERIVE)) {
                    dataManager.setDataSync(CAN_FIRST_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
                } else if (dataManager.getDataValue(CAN_SECOND_DERIVE)) {
                    dataManager.setDataSync(CAN_SECOND_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
                }
            } else {
                //重击，消耗所有星，开始蓄力，松手在客户端判断
                if (!dataManager.getDataValue(IS_CHARGING)) {
                    executer.playAnimationSynchronized(chargePre, 0.2F);
                }
            }

        }

        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, IS_IN_SPECIAL_ATTACK, false);
        SkillDataRegister.register(manager, IS_SPECIAL_ATTACK_SUCCESS, false);
        SkillDataRegister.register(manager, CAN_FIRST_DERIVE, false);
        SkillDataRegister.register(manager, CAN_SECOND_DERIVE, false);
        SkillDataRegister.register(manager, DERIVE_TIMER, 0);

        //长按期间禁止跳跃
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.HEAVY.isDownWithoutConflictCheck()) {
                Input input = event.getMovementInput();
                input.jumping = false;
            }
        }));

        //成功识破加棍势，并重置普攻计数器，下次从三段普攻开始
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {

            if (container.getDataManager().getDataValue(IS_IN_SPECIAL_ATTACK)) {
                //需加判断，否则此期间会猛涨
                if (!container.getDataManager().getDataValue(IS_SPECIAL_ATTACK_SUCCESS)) {
                    container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), container.getResource() + Config.CHARGING_SPEED.get().floatValue() * 20);//获得大量棍势
                    container.getDataManager().setDataSync(IS_SPECIAL_ATTACK_SUCCESS, true, event.getPlayerPatch().getOriginal());
                }
            }

            if (container.getDataManager().getDataValue(IS_SPECIAL_ATTACK_SUCCESS)) {
                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.ACTION_ANIMATION_RESET, event.getPlayerPatch(), event.getPlayerPatch().getSkill(SkillSlots.BASIC_ATTACK), deriveAnimation1, 2);
                event.setAmount(0);
                event.setResult(AttackResult.ResultType.MISSED);
                event.setCanceled(true);
            }

        }));

        //普攻后立即右键可以衍生
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
                    if (!WukongWeaponCategories.isWeaponValid(event.getPlayerPatch())) {
                        return;
                    }

                    List<StaticAnimation> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
                    //autoAnimations 的倒一倒二是冲刺和跳跃攻击，倒三是第五段普攻
                    boolean isLightAttack = autoAnimations.contains(event.getAnimation()) && !event.getAnimation().equals(autoAnimations.get(autoAnimations.size() - 1)) && !event.getAnimation().equals(autoAnimations.get(autoAnimations.size() - 2));
                    boolean isLastLightAttack = autoAnimations.get(autoAnimations.size() - 3).equals(event.getAnimation());

                    //蓄力的时候做动作是非法的，应该清空棍势，悟空Dodge额外判断
                    if (container.getDataManager().getDataValue(IS_CHARGING) && !event.getAnimation().equals(chargePre) && !(event.getAnimation() instanceof WukongDodgeAnimation)) {
                        this.setConsumptionSynchronize(event.getPlayerPatch(), 1);
                        this.setStackSynchronize(event.getPlayerPatch(), 0);
                        container.getDataManager().setDataSync(IS_CHARGING, false, player);
                    }

                    //释放普攻后重置可衍生时间
                    if (isLastLightAttack) {
                        container.getDataManager().setDataSync(CAN_FIRST_DERIVE, false, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, 0, player);
                    } else if (isLightAttack || event.getAnimation().equals(WukongAnimations.STAFF_AUTO1_DASH)) {
                        container.getDataManager().setDataSync(CAN_FIRST_DERIVE, true, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_DERIVE_TIMER, player);
                    }

                }));

        //刷新四蓄计时器，识破打中则可接二段
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    if (event.getDamageSource().getAnimation().equals(deriveAnimation1)) {
                        container.getDataManager().setDataSync(CAN_SECOND_DERIVE, true, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_DERIVE_TIMER, player);
                    }
                }));

        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event -> {
                    //成功识破则无视防御并造成强硬直
                    if (container.getDataManager().getDataValue(IS_SPECIAL_ATTACK_SUCCESS)) {
                        event.getDamageSource().addTag(SourceTags.GUARD_PUNCTURE);
                        event.getDamageSource().setStunType(StunType.HOLD);
                    }

                    //聚形散气加伤（没有暴击率...）
                    if (manager.hasData(CloudStepSkill.CHARGING_TIMER)) {
                        int chargingTime = manager.getDataValue(CloudStepSkill.CHARGING_TIMER);
                        if (List.of(animations).contains(event.getDamageSource().getAnimation())) {
                            double damageBoost = 1 + (0.2 * (CloudStepSkill.MAX_TIME - chargingTime) / CloudStepSkill.MAX_TIME);
                            event.setAttackDamage((float) (damageBoost * event.getAttackDamage()));
                        }
                    }

                    //根据星数改跳跃重击和破、斩棍式伤害
                    int starCnt = container.getDataManager().getDataValue(STARS_CONSUMED);
                    if (event.getDamageSource().getAnimation().equals(jumpAttackHeavy)) {
                        float mul = switch (starCnt) {
                            case 1 -> 3;
                            case 2 -> 4.5F;
                            case 3 -> 6.2F;
                            case 4 -> 8.75F;
                            default -> 1.45F;
                        };
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    } else if (event.getDamageSource().getAnimation().equals(deriveAnimation1)) {
                        float mul = starCnt == 0 ? 1.0F : 1.96F;
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    } else if (event.getDamageSource().getAnimation().equals(deriveAnimation2)) {
                        float mul = switch (starCnt) {
                            case 1 -> 4.7F;
                            case 2 -> 4.9F;
                            case 3, 4 -> 5.1F;
                            default -> 4.48F;
                        };
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    }
                }));

        super.onInitiate(container);
    }

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

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if (!container.getExecuter().isLogicalClient()){
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            //更新计时器
            dataManager.setDataSync(DERIVE_TIMER, Math.max(dataManager.getDataValue(DERIVE_TIMER) - 1, 0), serverPlayer);//切手技有效时间计算
            dataManager.setDataSync(RED_TIMER, Math.max(dataManager.getDataValue(RED_TIMER) - 1, 0), serverPlayer);//使用技能星数显示
            if (dataManager.getDataValue(DERIVE_TIMER) <= 0) {
                dataManager.setDataSync(CAN_FIRST_DERIVE, false, serverPlayer);
                dataManager.setDataSync(CAN_SECOND_DERIVE, false, serverPlayer);
            }

            if (dataManager.getDataValue(IS_CHARGING)) {
                //松手或没耐力则清空棍势打重击
                if (!dataManager.getDataValue(KEY_PRESSING) || !serverPlayerPatch.hasStamina(Config.CHARGING_STAMINA_CONSUME.get().floatValue() + 0.1F)) {
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                    dataManager.setData(PROTECT_NEXT_FALL, true);//MAN
                    serverPlayerPatch.playAnimationSynchronized(animations[container.getStack()], 0.0F);//有几星就几星重击
                    dataManager.setDataSync(STARS_CONSUMED, container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    resetConsumption(container, serverPlayerPatch, true);
                }
            }

        }

    }

    @Override
    public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        List<Component> list = Lists.newArrayList();
        list.add(new TranslatableComponent(this.getTranslationKey()).withStyle(ChatFormatting.GOLD).append(new TextComponent(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
        list.add(new TranslatableComponent("skill.wukong.smash_style.tooltip"));
        return list;
    }

    public static class Builder extends Skill.Builder<SmashHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        protected StaticAnimationProvider jumpAttackHeavy;
        StaticAnimationProvider chargingAnimation;
        StaticAnimationProvider pre;

        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public Builder setChargingAnimation(StaticAnimationProvider chargingAnimation) {
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        public Builder setChargePreAnimation(StaticAnimationProvider pre) {
            this.pre = pre;
            return this;
        }

        /**
         * 0~4星重击
         */
        public Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }

        /**
         * 如果是可长按的衍生则derive1就是pre动画，具体逻辑在动画那里判断
         */
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            return this;
        }

        public Builder setJumpAttackHeavy(StaticAnimationProvider jumpAttackHeavy) {
            this.jumpAttackHeavy = jumpAttackHeavy;
            return this;
        }
    }

}

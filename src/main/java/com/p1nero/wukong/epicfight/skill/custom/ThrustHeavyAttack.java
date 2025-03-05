package com.p1nero.wukong.epicfight.skill.custom;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import io.netty.buffer.Unpooled;
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
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
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
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 劈棍重击
 */
public class ThrustHeavyAttack extends HeavyAttack {
    public static final int MAX_DODGE_SUCCESS_TICKS = 300;//15s
    public static final int MAX_TRANSPARENT_TIMER = 30;
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static SkillDataManager.SkillDataKey<Boolean> IS_ATTACK_KEY_DOWN = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否处于长按衍生
    public static final SkillDataManager.SkillDataKey<Boolean> IS_REPEATING_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否处于搅棍
    public static final SkillDataManager.SkillDataKey<Integer> REPEATING_DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//搅棍合法时间计时器
    public static final SkillDataManager.SkillDataKey<Integer> TRANSPARENT_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//虚化计时器
    public static final SkillDataManager.SkillDataKey<Integer> FENGCHUANHUA_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//凤穿花合法时间计时器
    public static final SkillDataManager.SkillDataKey<Integer> DODGE_SUCCESS_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//退寸成功后不耗耐力的持续时间
    public static SkillDataManager.SkillDataKey<Boolean> DODGE_SUCCESS = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否退寸成功，用于进尺无敌帧判断
    public static SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//衍生合法时间计时器
    public static SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第一段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第二段衍生
    protected final StaticAnimation[] animations;//0~4共有五种重击
    protected StaticAnimation deriveAnimation1;
    protected StaticAnimation deriveAnimation2;
    protected StaticAnimation deriveLoopPre;
    protected StaticAnimation deriveLoopEnd;
    @NotNull
    protected StaticAnimation jumpAttackHeavy;
    @NotNull
    protected StaticAnimation charging;
    @NotNull
    protected StaticAnimation chargePre;

    public static Builder createChargedAttack() {
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    public ThrustHeavyAttack(Builder builder) {
        super(builder);
        charging = builder.chargingAnimation.get();
        chargePre = builder.pre.get();

        this.animations = new StaticAnimation[builder.animationProviders.length];
        for (int i = 0; i < builder.animationProviders.length; i++) {
            this.animations[i] = builder.animationProviders[i].get();
        }

        deriveAnimation1 = builder.derive1.get();
        deriveAnimation2 = builder.derive2.get();
        deriveLoopPre = builder.derivePre.get();
        deriveLoopEnd = builder.deriveEnd.get();
        jumpAttackHeavy = builder.jumpAttackHeavy.get();
    }

    /**
     * 保险，yesman官方提供的解法
     */
    public static void register(final FMLCommonSetupEvent event) {
        HeavyAttack.register(event);
        event.enqueueWork(() -> {
            ThrustHeavyAttack.CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            ThrustHeavyAttack.DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
            ThrustHeavyAttack.IS_ATTACK_KEY_DOWN = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否处于长按衍生
        });
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link ThrustHeavyAttack#updateContainer(SkillContainer)}
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
        } else {

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

            //凤穿花，具体看闪避动画相关判断
            if (dataManager.getDataValue(FENGCHUANHUA_TIMER) > 10 && container.isFull()) {
                executer.playAnimationSynchronized(animations[container.getStack()], 0.0F);
                this.setStackSynchronize(executer, 0);
                this.setConsumptionSynchronize(executer, 1);
            } else if (dataManager.getDataValue(DERIVE_TIMER) > 0) {//有无星都能退
                if (dataManager.getDataValue(CAN_FIRST_DERIVE)) {
                    dataManager.setDataSync(CAN_FIRST_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
                } else if (dataManager.getDataValue(CAN_SECOND_DERIVE)) {
                    dataManager.setDataSync(CAN_SECOND_DERIVE, false, player);
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    if (container.getStack() > 0) {
                        executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                        this.setStackSynchronize(executer, container.getStack() - 1);
                        this.setConsumptionSynchronize(executer, 1);
                    }
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
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, IS_ATTACK_KEY_DOWN, false);
        SkillDataRegister.register(manager, IS_REPEATING_DERIVE, false);
        SkillDataRegister.register(manager, REPEATING_DERIVE_TIMER, 0);
        SkillDataRegister.register(manager, DODGE_SUCCESS_TIMER, 0);
        SkillDataRegister.register(manager, DODGE_SUCCESS, false);
        SkillDataRegister.register(manager, TRANSPARENT_TIMER, 0);
        SkillDataRegister.register(manager, FENGCHUANHUA_TIMER, 0);
        SkillDataRegister.register(manager, CHARGED4_TIMER, 0);
        SkillDataRegister.register(manager, CAN_FIRST_DERIVE, false);
        SkillDataRegister.register(manager, CAN_SECOND_DERIVE, false);
        SkillDataRegister.register(manager, DERIVE_TIMER, 0);

        //长按期间禁止移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.HEAVY.isDown()) {
                Input input = event.getMovementInput();
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
                clientPlayer.setSprinting(false);
                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));

        //退寸成功
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation().equals(deriveAnimation1)) {
                this.onDodgeSuccess(container, event.getPlayerPatch());
            }
        }));

        //成功识破加棍势，并重置普攻计数器，下次从三段普攻开始
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {

            AnimationPlayer animationPlayer = event.getPlayerPatch().getAnimator().getPlayerFor(null);
            DynamicAnimation currentAnim = animationPlayer.getAnimation();

            //退寸成功后进尺无敌
            //寸退成功在闪避成功事件里判断
            if (currentAnim.equals(deriveAnimation2) && container.getDataManager().getDataValue(DODGE_SUCCESS)) {
                event.setResult(AttackResult.ResultType.MISSED);
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }

            //隐身无敌
            //寸退成功在闪避成功事件里判断
            if (container.getDataManager().getDataValue(TRANSPARENT_TIMER) > 0) {
                event.setResult(AttackResult.ResultType.MISSED);
                event.setAmount(0);
                event.setCanceled(true);
                return;
            }

            //退寸成功则可虚化
            if (currentAnim.equals(deriveAnimation1) && animationPlayer.getElapsedTime() <= 0.8F) {
                this.onDodgeSuccess(container, event.getPlayerPatch());
                event.setResult(AttackResult.ResultType.MISSED);
                event.setAmount(0);
                event.setCanceled(true);
            }

            //寸退和进尺过程中的霸体
            if (currentAnim.equals(deriveAnimation1) || currentAnim.equals(deriveAnimation2)) {
                if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                    epicFightDamageSource.setStunType(StunType.NONE);
                }
                LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS, event.getAmount() * 0.7F, attackerPatch);
                event.setResult(AttackResult.ResultType.BLOCKED);
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

                    //退完可接进尺
                    if (event.getAnimation().equals(deriveAnimation1)) {
                        container.getDataManager().setDataSync(CAN_SECOND_DERIVE, true, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_DERIVE_TIMER, player);
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

        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event -> {

                    //聚形散气加伤（没有暴击率...）
                    if (manager.hasData(CloudStepSkill.CHARGING_TIMER)) {
                        int chargingTime = manager.getDataValue(CloudStepSkill.CHARGING_TIMER);
                        if (List.of(animations).contains(event.getDamageSource().getAnimation())) {
                            double damageBoost = 1 + (0.2 * (CloudStepSkill.MAX_TIME - chargingTime) / CloudStepSkill.MAX_TIME);
                            event.setAttackDamage((float) (damageBoost * event.getAttackDamage()));
                        }
                    }

                    //根据星数改跳跃重击和进尺伤害
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
                    } else if (event.getDamageSource().getAnimation().equals(deriveAnimation2)) {

                    }
                })
        );

        super.onInitiate(container);
    }

    /**
     * 退寸成功
     */
    public void onDodgeSuccess(SkillContainer container, ServerPlayerPatch serverPlayerPatch) {
        container.getDataManager().setDataSync(TRANSPARENT_TIMER, MAX_TRANSPARENT_TIMER, serverPlayerPatch.getOriginal());
        container.getDataManager().setDataSync(DODGE_SUCCESS_TIMER, MAX_DODGE_SUCCESS_TICKS, serverPlayerPatch.getOriginal());//一段时间内不耗耐力
        container.getDataManager().setDataSync(DODGE_SUCCESS, true, serverPlayerPatch.getOriginal());//下次进尺可以无伤
        serverPlayerPatch.playSound(WuKongSounds.PERFECT_DODGE.get(), 0.5F, 0, 0);//TODO 替换
        PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageParticle(serverPlayerPatch.getOriginal().getId()));
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
        listener.removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
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
            dataManager.setDataSync(REPEATING_DERIVE_TIMER, Math.max(dataManager.getDataValue(REPEATING_DERIVE_TIMER) - 1, 0), serverPlayer);//搅棍有效时间计算
            dataManager.setDataSync(FENGCHUANHUA_TIMER, Math.max(dataManager.getDataValue(FENGCHUANHUA_TIMER) - 1, 0), serverPlayer);//凤穿花有效时间计算
            dataManager.setDataSync(TRANSPARENT_TIMER, Math.max(dataManager.getDataValue(TRANSPARENT_TIMER) - 1, 0), serverPlayer);//透明有效时间计算
            dataManager.setDataSync(DODGE_SUCCESS_TIMER, Math.max(dataManager.getDataValue(DODGE_SUCCESS_TIMER) - 1, 0), serverPlayer);//不耗耐力有效时间计算
            dataManager.setDataSync(RED_TIMER, Math.max(dataManager.getDataValue(RED_TIMER) - 1, 0), serverPlayer);//使用技能星数显示
            if (dataManager.getDataValue(DERIVE_TIMER) <= 0) {
                dataManager.setDataSync(CAN_FIRST_DERIVE, false, serverPlayer);
                dataManager.setDataSync(CAN_SECOND_DERIVE, false, serverPlayer);
            }

            //蓄力中
            if (dataManager.getDataValue(IS_CHARGING)) {

                //松手或没耐力则清空棍势打重击
                if (!dataManager.getDataValue(KEY_PRESSING) || !serverPlayerPatch.hasStamina(Config.CHARGING_STAMINA_CONSUME.get().floatValue() + 0.1F)) {
                    serverPlayerPatch.playAnimationSynchronized(animations[container.getStack()], 0.0F);//有几星就几星重击
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                    dataManager.setDataSync(STARS_CONSUMED, container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    resetConsumption(container, serverPlayerPatch, true);
                }
            }

            //开始搅
            if (dataManager.getDataValue(REPEATING_DERIVE_TIMER) > 0 && !dataManager.getDataValue(IS_REPEATING_DERIVE)) {
                if (dataManager.getDataValue(IS_ATTACK_KEY_DOWN)) {
                    serverPlayerPatch.playAnimationSynchronized(deriveLoopPre, 0.15F);
                    dataManager.setDataSync(IS_REPEATING_DERIVE, true, serverPlayer);
                    dataManager.setDataSync(REPEATING_DERIVE_TIMER, 0, serverPlayer);
                }
            }
            //结束搅
            if (dataManager.getDataValue(IS_REPEATING_DERIVE)) {
                //扣耐力
                if (!serverPlayer.isCreative()) {
                    serverPlayerPatch.consumeStamina(Config.CHARGING_STAMINA_CONSUME.get().floatValue());
                    if (!serverPlayerPatch.hasStamina(0.1F)) {
                        serverPlayerPatch.playAnimationSynchronized(deriveLoopEnd, 0.15F);
                        dataManager.setDataSync(IS_REPEATING_DERIVE, false, serverPlayer);
                    }
                }
                //重置可退寸时间
                dataManager.setDataSync(ThrustHeavyAttack.CAN_FIRST_DERIVE, true, serverPlayerPatch.getOriginal());
                dataManager.setDataSync(ThrustHeavyAttack.DERIVE_TIMER, ThrustHeavyAttack.MAX_DERIVE_TIMER, serverPlayerPatch.getOriginal());
                //松手了则播end
                if (!dataManager.getDataValue(IS_ATTACK_KEY_DOWN)) {
                    serverPlayerPatch.playAnimationSynchronized(deriveLoopEnd, 0.15F);
                    dataManager.setDataSync(IS_REPEATING_DERIVE, false, serverPlayer);
                }
            }

        }

    }

    @Override
    public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerCap) {
        List<Component> list = Lists.newArrayList();
        list.add(new TranslatableComponent(this.getTranslationKey()).withStyle(ChatFormatting.GOLD).append(new TextComponent(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
        list.add(new TranslatableComponent("skill.wukong.thrust_style.tooltip"));
        return list;
    }

    @Override
    public List<StaticAnimation> getHeavyAttacks() {
        List<StaticAnimation> staticAnimations = new java.util.ArrayList<>(List.of(animations));
        staticAnimations.add(deriveAnimation2);
        return staticAnimations;
    }

    public static class Builder extends Skill.Builder<ThrustHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        protected StaticAnimationProvider derivePre;
        protected StaticAnimationProvider deriveEnd;
        protected StaticAnimationProvider jumpAttackHeavy;
        protected StaticAnimationProvider chargingAnimation;
        protected StaticAnimationProvider pre;

        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Resource resource) {
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
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2, StaticAnimationProvider derivePre, StaticAnimationProvider deriveEnd) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            this.derivePre = derivePre;
            this.deriveEnd = deriveEnd;
            return this;
        }

        public Builder setJumpAttackHeavy(StaticAnimationProvider jumpAttackHeavy) {
            this.jumpAttackHeavy = jumpAttackHeavy;
            return this;
        }
    }

}
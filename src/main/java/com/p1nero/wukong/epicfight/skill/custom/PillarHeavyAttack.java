package com.p1nero.wukong.epicfight.skill.custom;

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
import com.p1nero.wukong.epicfight.animation.custom.WukongDodgeAnimation;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.CloudStepSkill;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.TTTBSkill;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.ClientEngine;
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
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 立棍重击
 */
public class PillarHeavyAttack extends WeaponInnateSkill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-22ed-a05b-0242ac114514");
    public static final int MAX_DERIVE_TIMER = Config.DERIVE_CHECK_TIME.get().intValue();//在此期间内再按才被视为衍生
    public static SkillDataManager.SkillDataKey<Boolean> KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//技能键是否按下
    public static final SkillDataManager.SkillDataKey<Boolean> IS_REPEATING_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否处于长按一段衍生
    private static final SkillDataManager.SkillDataKey<Integer> CHARGED4_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//四段棍势持续时间
    public static final int MAX_CHARGED4_TICKS = 300;//15s
    private static SkillDataManager.SkillDataKey<Integer> RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//亮灯时间
    public static final SkillDataManager.SkillDataKey<Integer> LAST_STACK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//上一次的层数，用于判断是否加层
    public static final SkillDataManager.SkillDataKey<Integer> STARS_CONSUMED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//本次攻击是否消耗星（是否强化）
    public static final SkillDataManager.SkillDataKey<Boolean> IS_IN_SPECIAL_ATTACK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否正在切手技
    public static final SkillDataManager.SkillDataKey<Boolean> IS_SPECIAL_ATTACK_SUCCESS = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否识破成功
    public static SkillDataManager.SkillDataKey<Boolean> IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否正在蓄力
    public static SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//衍生合法时间计时器
    public static SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第一段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第二段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_JUMP_HEAVY = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用跳跃重击
    public static final SkillDataManager.SkillDataKey<Boolean> PLAY_SOUND = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否播放棍势消耗音效
    public static final SkillDataManager.SkillDataKey<Float> DAMAGE_REDUCE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);//是否播放棍势消耗音效
    public static final SkillDataManager.SkillDataKey<Boolean> PROTECT_NEXT_FALL = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//防止坠机
    protected final StaticAnimation[] pres;//升起
    protected final StaticAnimation[] posts;//打出
    protected StaticAnimation deriveAnimationPre;
    protected StaticAnimation deriveAnimationLoop;
    protected StaticAnimation deriveAnimation1Post;
    protected StaticAnimation deriveAnimation2;
    @NotNull
    protected StaticAnimation jumpAttackHeavy;

    public static Builder createChargedAttack() {
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    public PillarHeavyAttack(PillarHeavyAttack.Builder builder) {
        super(builder);
        this.pres = new StaticAnimation[builder.pres.length];
        for (int i = 0; i < builder.pres.length; i++) {
            WukongMoveset.LOGGER.info("loading heavy attack pre animations: {}", builder.pres[i].get());
            this.pres[i] = builder.pres[i].get();
        }

        this.posts = new StaticAnimation[builder.posts.length];
        for (int i = 0; i < builder.posts.length; i++) {
            WukongMoveset.LOGGER.info("loading heavy attack animations: {}", builder.posts[i].get());
            this.posts[i] = builder.posts[i].get();
        }
        deriveAnimationPre = builder.derive1Pre.get();
        deriveAnimationLoop = builder.derive1Loop.get();
        deriveAnimation1Post = builder.derive1Post.get();
        deriveAnimation2 = builder.derive2.get();
        jumpAttackHeavy = builder.jumpAttackHeavy.get();
    }

    /**
     * 保险，yesman官方提供的解法
     */
    public static void register(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PillarHeavyAttack.IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            PillarHeavyAttack.CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            PillarHeavyAttack.KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            PillarHeavyAttack.DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
            PillarHeavyAttack.RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
        });
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link PillarHeavyAttack#updateContainer(SkillContainer)}
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
            if(tongTouTieBi != null){
                SkillDataManager tDataManager = tongTouTieBi.getDataManager();
                if(tDataManager.hasData(TTTBSkill.TTTB_TIMER) && tDataManager.getDataValue(TTTBSkill.TTTB_TIMER) > 0 && container.getStack() > 0){
//                    executer.playAnimationSynchronized(animations[container.getStack()], 0.0F);
                    resetConsumption(container, executer, true);
                    super.executeOnServer(executer, args);
                    return;
                }
            }

            //如果用了星则要强化衍生
            boolean stackConsumed = container.getStack() > 0;
            if (dataManager.getDataValue(DERIVE_TIMER) > 0 && stackConsumed) {
                if (dataManager.getDataValue(CAN_FIRST_DERIVE)) {
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimationPre, 0.2F);
                } else if (dataManager.getDataValue(CAN_SECOND_DERIVE)) {
                    dataManager.setData(PROTECT_NEXT_FALL, true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
                }
            } else {
                //重击，消耗所有星，开始蓄力，松手在客户端判断
                if (!dataManager.getDataValue(IS_CHARGING)) {
                    executer.playAnimationSynchronized(pres[container.getStack()], 0.2F);
                }
            }

        }

        super.executeOnServer(executer, args);
    }

    /**
     * 清空耐力并播红光和音效
     *
     * @param playSound 如果是通过蓄力而释放的就不播音效
     */
    private void resetConsumption(SkillContainer container, ServerPlayerPatch executer, boolean playSound) {
        if (playSound && container.getStack() > 0) {
            int cnt = container.getStack();
            new Thread(() -> {
                for (int i = 0; i < cnt; i++) {
                    executer.playSound(WuKongSounds.stackSounds.get(i).get(), 1, 1);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        WukongMoveset.LOGGER.error("interrupted when play stack sounds!", e);
                    }
                }
            }).start();
        } else {
            container.getDataManager().setDataSync(PLAY_SOUND, true, executer.getOriginal());
        }
        container.getDataManager().setDataSync(RED_TIMER, MAX_DERIVE_TIMER, executer.getOriginal());//通知客户端该亮红灯了
        this.setStackSynchronize(executer, 0);
        this.setConsumptionSynchronize(executer, 1);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, IS_REPEATING_DERIVE, false);
        SkillDataRegister.register(manager, KEY_PRESSING, false);
        SkillDataRegister.register(manager, CHARGED4_TIMER, 0);
        SkillDataRegister.register(manager, RED_TIMER, 0);
        SkillDataRegister.register(manager, LAST_STACK, 0);
        SkillDataRegister.register(manager, STARS_CONSUMED, 0);
        SkillDataRegister.register(manager, DAMAGE_REDUCE, 0.0F);
        SkillDataRegister.register(manager, IS_CHARGING, false);
        SkillDataRegister.register(manager, IS_IN_SPECIAL_ATTACK, false);
        SkillDataRegister.register(manager, IS_SPECIAL_ATTACK_SUCCESS, false);
        SkillDataRegister.register(manager, CAN_FIRST_DERIVE, false);
        SkillDataRegister.register(manager, CAN_SECOND_DERIVE, false);
        SkillDataRegister.register(manager, CAN_JUMP_HEAVY, false);
        SkillDataRegister.register(manager, PLAY_SOUND, true);
        SkillDataRegister.register(manager, PROTECT_NEXT_FALL, false);
        SkillDataRegister.register(manager, DERIVE_TIMER, 0);

        //长按期间禁止移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.HEAVY.isDownWithoutConflictCheck()) {
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

        //钢管舞期间减伤
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            event.getPlayerPatch().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                if (wkPlayer.getDamageReduce() > 0) {
                    if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                        epicFightDamageSource.setStunType(StunType.NONE);
                    }
                    LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                    this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS, event.getAmount() * (1 - wkPlayer.getDamageReduce()), attackerPatch);
                    event.setResult(AttackResult.ResultType.BLOCKED);
                    event.setCanceled(true);
                }
            });

            //防止坠机
            if (event.getDamageSource().isFall() && container.getDataManager().getDataValue(PROTECT_NEXT_FALL)) {
                event.setAmount(0);
                event.setCanceled(true);
                container.getDataManager().setData(PROTECT_NEXT_FALL, false);
            }
        }));

//        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID, (event) -> {
//            container.getDataManager().setData(PROTECT_NEXT_FALL, false);
//        });

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
                    if (container.getDataManager().getDataValue(IS_CHARGING) && !List.of(pres).contains(event.getAnimation()) && !(event.getAnimation() instanceof WukongDodgeAnimation)) {
                        this.setConsumptionSynchronize(event.getPlayerPatch(), 1);
                        this.setStackSynchronize(event.getPlayerPatch(), 0);
                        container.getDataManager().setDataSync(IS_CHARGING, false, player);
                    }

                    //释放普攻后重置可衍生时间
                    if (isLightAttack && !isLastLightAttack) {
                        container.getDataManager().setDataSync(CAN_FIRST_DERIVE, true, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_DERIVE_TIMER, player);
                    }

                }));

        //刷新四蓄计时器
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    if (container.isFull()) {
                        container.getDataManager().setDataSync(CHARGED4_TIMER, MAX_CHARGED4_TICKS, player);
                    }
                })
        );

        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event -> {

                    //聚形散气加伤（没有暴击率...）TODO
//                    if(manager.hasData(CloudStepSkill.CHARGING_TIMER)){
//                        int chargingTime = manager.getDataValue(CloudStepSkill.CHARGING_TIMER);
//                        if(List.of(animations).contains(event.getDamageSource().getAnimation())){
//                            double damageBoost = 1 + (0.2 * (CloudStepSkill.MAX_TIME - chargingTime) / CloudStepSkill.MAX_TIME);
//                            event.setAttackDamage((float) (damageBoost * event.getAttackDamage()));
//                        }

                    //根据星数改跳跃重击和切手2伤害
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
                        float mul = switch (starCnt) {
                            case 1 -> 4.7F;
                            case 2 -> 4.9F;
                            case 3, 4 -> 5.1F;
                            default -> 4.48F;
                        };
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    }
                    //对倒地的敌人不施加硬直
                    event.getTarget().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
                        if (entityPatch instanceof LivingEntityPatch<?> livingEntityPatch) {
                            if (livingEntityPatch.getEntityState().knockDown()) {
                                event.getDamageSource().setStunType(StunType.NONE);
                            }
                        }
                    });
                }));

        super.onInitiate(container);
    }

    public void processDamage(PlayerPatch<?> entitypatch, DamageSource damageSource, AttackResult.ResultType resultType, float amount, @Nullable LivingEntityPatch<?> attackerPatch) {
        AttackResult result = (entitypatch != null && !damageSource.isBypassInvul()) ? new AttackResult(resultType, amount) : AttackResult.success(amount);
        if (attackerPatch != null) {
            attackerPatch.setLastAttackResult(result);
        }
        DamageSource deflictedDamage = new DamageSource(damageSource.msgId).bypassInvul();
        if (entitypatch != null) {
            entitypatch.getOriginal().hurt(deflictedDamage, result.damage);
        }
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
        if (!dataManager.hasData(KEY_PRESSING)) {
            dataManager.registerData(KEY_PRESSING);
        }
        if (container.getExecuter().isLogicalClient()) {
            //KEY_PRESSING用于服务端判断是否继续播动画
            boolean isKeyDown = WukongKeyMappings.HEAVY.isDown();
            dataManager.setDataSync(KEY_PRESSING, isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            //层数变化检测以播音效
            if (container.getStack() > dataManager.getDataValue(LAST_STACK)) {
                serverPlayerPatch.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                dataManager.setDataSync(PLAY_SOUND, false, serverPlayer);
            }
            dataManager.setData(LAST_STACK, container.getStack());

            //跳重击的判断
            if (!serverPlayer.isOnGround() && !serverPlayer.isInWater() && serverPlayer.getDeltaMovement().y > 0.05D) {
                dataManager.setDataSync(CAN_JUMP_HEAVY, true, serverPlayer);
            } else if (dataManager.getDataValue(CAN_JUMP_HEAVY)) {
                dataManager.setDataSync(CAN_JUMP_HEAVY, false, serverPlayer);
            }

            //更新计时器
            dataManager.setDataSync(DERIVE_TIMER, Math.max(dataManager.getDataValue(DERIVE_TIMER) - 1, 0), serverPlayer);//切手技有效时间计算
            dataManager.setDataSync(RED_TIMER, Math.max(dataManager.getDataValue(RED_TIMER) - 1, 0), serverPlayer);//使用技能星数显示
            if (dataManager.getDataValue(DERIVE_TIMER) <= 0) {
                dataManager.setDataSync(CAN_FIRST_DERIVE, false, serverPlayer);
                dataManager.setDataSync(CAN_SECOND_DERIVE, false, serverPlayer);
            }

            if (dataManager.getDataValue(IS_CHARGING)) {
                //防止切物品产生的bug
                if (!WukongWeaponCategories.isWeaponValid(serverPlayerPatch)) {
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                    this.setConsumptionSynchronize(serverPlayerPatch, 1);
                    this.setStackSynchronize(serverPlayerPatch, 0);
                    return;
                }
                //蓄力的加条
                if (container.getStack() < 3) {
                    int stackOld = container.getStack();
                    this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                    int stackNew = container.getStack();
                    if (stackNew > stackOld) {
                        serverPlayerPatch.playAnimationSynchronized(pres[stackNew], 0.25F);//升高
                    }
                }
                //松手则清空棍势打重击
                if (!dataManager.getDataValue(KEY_PRESSING)) {
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                    dataManager.setData(PROTECT_NEXT_FALL, true);//MAN
                    serverPlayerPatch.playAnimationSynchronized(posts[container.getStack()], 0.0F);//有几星就几星重击
                    dataManager.setDataSync(STARS_CONSUMED, container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    resetConsumption(container, serverPlayerPatch, true);
                }
            }

            //破条则加stack清空蓄力条
            if (container.getStack() < 3 && Math.ceil(container.getResource(1.0F) * 20) > 10) {
                this.setConsumptionSynchronize(serverPlayerPatch, 1);
                this.setStackSynchronize(serverPlayerPatch, container.getStack() + 1);
            }

            //四蓄的掉棍势时间判断
            int current = dataManager.getDataValue(CHARGED4_TIMER);
            if (current > 0) {
                dataManager.setDataSync(CHARGED4_TIMER, current - 1, serverPlayer);
            }
            float consumption = Config.CHARGING_SPEED.get().floatValue() / 5;
            if (current == 1 && container.isFull()) {
                this.setStackSynchronize(serverPlayerPatch, 3);
                this.setConsumptionSynchronize(serverPlayerPatch, container.getMaxResource() - consumption);
            }
            if (current == 0 && container.getStack() >= 3 && container.getResource() > consumption + 0.1) {
                this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() - consumption);
            }

        }

    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return WukongWeaponCategories.isWeaponValid(container.getExecuter());
    }

    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        int stack = container.getStack();
        int style = container.getExecuter().getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(container.getExecuter()).universalOrdinal() - WukongStyles.SMASH.universalOrdinal();
        float cooldownRatio = !container.isFull() && !container.isActivated() ? container.getResource(1.0F) : 1.0F;
        int progress = ((int) Math.ceil(cooldownRatio * 20));
        ConfigurationIngame config = EpicFightMod.CLIENT_INGAME_CONFIG;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        poseStack.pushPose();
        poseStack.translate(0.0, gui.getSlidingProgression(), 0.0);
        ResourceLocation progressTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/progress/" + progress + ".png");
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/style/" + style + (stack == 4 ? "_1" : "_0") + ".png");
        ResourceLocation stackTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/" + stack + ".png");
        ResourceLocation goldenLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/gold.png");
        RenderSystem.setShaderTexture(0, progressTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        RenderSystem.setShaderTexture(0, styleTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        RenderSystem.setShaderTexture(0, stackTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);

        if (container.getDataManager().getDataValue(RED_TIMER) > 0) {
            int star = Math.min(container.getDataManager().getDataValue(STARS_CONSUMED), 3);
            if (star > 0) {
                ResourceLocation lightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/" + star + ".png");//及时获取stack，新鲜的，热乎的
                RenderSystem.setShaderTexture(0, lightTexture);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);

            }
        }

        if (container.isFull()) {
            RenderSystem.setShaderTexture(0, goldenLightTexture);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        }


    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }

    public static class Builder extends Skill.Builder<PillarHeavyAttack> {
        protected StaticAnimationProvider jumpAttackHeavy;
        protected StaticAnimationProvider[] pres;
        protected StaticAnimationProvider[] loops;
        protected StaticAnimationProvider[] posts;
        protected StaticAnimationProvider derive1Pre;
        protected StaticAnimationProvider derive1Loop;
        protected StaticAnimationProvider derive1Post;
        protected StaticAnimationProvider derive2;

        public Builder() {
        }

        public PillarHeavyAttack.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public PillarHeavyAttack.Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public PillarHeavyAttack.Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public PillarHeavyAttack.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        /**
         * 升起
         */
        public PillarHeavyAttack.Builder setPreAnimations(StaticAnimationProvider... pre) {
            this.pres = pre;
            return this;
        }

        /**
         * 0~4星重击
         */
        public PillarHeavyAttack.Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.posts = animationProviders;
            return this;
        }

        /**
         * 如果是可长按的衍生则derive1就是pre动画，具体逻辑在动画那里判断
         */
        public PillarHeavyAttack.Builder setDeriveAnimations(StaticAnimationProvider derive1Pre, StaticAnimationProvider derive1Loop, StaticAnimationProvider derive1, StaticAnimationProvider derive2) {
            this.derive1Pre = derive1Pre;
            this.derive1Loop = derive1Loop;
            this.derive1Post = derive1;
            this.derive2 = derive2;
            return this;
        }

        public PillarHeavyAttack.Builder setJumpAttackHeavy(StaticAnimationProvider jumpAttackHeavy) {
            this.jumpAttackHeavy = jumpAttackHeavy;
            return this;
        }
    }

}

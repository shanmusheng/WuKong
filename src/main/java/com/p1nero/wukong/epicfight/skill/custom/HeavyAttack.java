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
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.CloudStepSkill;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2i;
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
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

public abstract class HeavyAttack extends WeaponInnateSkill {
    private static final UUID EVENT_UUID = UUID.fromString("d2c110cc-f00f-11ed-a05b-0242ac114514");
    public static final int MAX_DERIVE_TIMER = Config.DERIVE_CHECK_TIME.get().intValue();//在此期间内再按才被视为衍生
    public static final int MAX_CHARGED4_TICKS = 300;//15s
    protected static SkillDataManager.SkillDataKey<Integer> RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//亮灯时间
    public static final SkillDataManager.SkillDataKey<Integer> STARS_CONSUMED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//本次攻击是否消耗星（是否强化）
    public static SkillDataManager.SkillDataKey<Boolean> KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//技能键是否按下
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_JUMP_HEAVY = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用跳跃重击
    public static final SkillDataManager.SkillDataKey<Integer> LAST_STACK = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//上一次的层数，用于判断是否加层
    public static final SkillDataManager.SkillDataKey<Boolean> PLAY_SOUND = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否播放棍势消耗音效
    public static SkillDataManager.SkillDataKey<Boolean> IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否正在蓄力
    protected static final SkillDataManager.SkillDataKey<Integer> CHARGED4_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//四段棍势持续时间
    public static final SkillDataManager.SkillDataKey<Boolean> PROTECT_NEXT_FALL = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//防止坠机
    public static final SkillDataManager.SkillDataKey<Float> DAMAGE_REDUCE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);//伤害减免

    public HeavyAttack(Builder<? extends Skill> builder) {
        super(builder);
    }

    public static void register(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
            KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
            IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
        });
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, RED_TIMER, 0);
        SkillDataRegister.register(manager, KEY_PRESSING, false);
        SkillDataRegister.register(manager, IS_CHARGING, false);
        SkillDataRegister.register(manager, STARS_CONSUMED, 0);
        SkillDataRegister.register(manager, CAN_JUMP_HEAVY, false);
        SkillDataRegister.register(manager, LAST_STACK, 0);
        SkillDataRegister.register(manager, PLAY_SOUND, true);
        SkillDataRegister.register(manager, CHARGED4_TIMER, 0);
        SkillDataRegister.register(manager, PROTECT_NEXT_FALL, false);
        SkillDataRegister.register(manager, DAMAGE_REDUCE, 0.0F);

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            //防止坠机
            if (event.getDamageSource().isFall() && container.getDataManager().getDataValue(PROTECT_NEXT_FALL)) {
                event.setAmount(0);
                event.setResult(AttackResult.ResultType.MISSED);
                event.setCanceled(true);
                container.getDataManager().setData(PROTECT_NEXT_FALL, false);
            }

            //减伤判断
            event.getPlayerPatch().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                if (wkPlayer.getDamageReduce() > 0 && !event.isCanceled()) {
                    if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource) {
                        epicFightDamageSource.setStunType(StunType.NONE);
                    }
                    LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                    this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS, event.getAmount() * (1 - wkPlayer.getDamageReduce()), attackerPatch);
                    event.setResult(AttackResult.ResultType.BLOCKED);
                    event.setCanceled(true);
                }
            });
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
                //对倒地的敌人不施加硬直
                event.getTarget().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
                    if (entityPatch instanceof LivingEntityPatch<?> livingEntityPatch) {
                        if (livingEntityPatch.getEntityState().knockDown()) {
                            event.getDamageSource().setStunType(StunType.NONE);
                        }
                    }
                });
            })
        );

    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
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
            if(WukongKeyMappings.HEAVY.isDown() != dataManager.getDataValue(KEY_PRESSING)){
                dataManager.setDataSync(KEY_PRESSING, WukongKeyMappings.HEAVY.isDown(), ((LocalPlayer) container.getExecuter().getOriginal()));
            }
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
                    this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
                //扣耐力
                if (!serverPlayer.isCreative()) {
                    serverPlayerPatch.consumeStamina(Config.CHARGING_STAMINA_CONSUME.get().floatValue());
                }
            }

            //破条则加stack清空蓄力条
            if (container.getStack() < 1 && container.getResource() > container.getMaxResource() * 0.3) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 2 && container.getResource() > container.getMaxResource() * 0.5) {
                breakProgress(serverPlayerPatch, container);
            } else if (container.getStack() < 3 && container.getResource() > container.getMaxResource() * 0.7) {
                breakProgress(serverPlayerPatch, container);
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

    public abstract List<StaticAnimation> getHeavyAttacks();


    /**
     * 清空耐力并播红光和音效
     *
     * @param playSound 如果是通过蓄力而释放的就不播音效
     */
    protected void resetConsumption(SkillContainer container, ServerPlayerPatch executer, boolean playSound) {
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

    public void breakProgress(ServerPlayerPatch serverPlayerPatch, SkillContainer container) {
        this.setConsumptionSynchronize(serverPlayerPatch, 0.1F);
        this.setStackSynchronize(serverPlayerPatch, container.getStack() + 1);
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
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
        int progress = ((int) Math.ceil(cooldownRatio * 40));
        ConfigurationIngame config = EpicFightMod.CLIENT_INGAME_CONFIG;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        poseStack.pushPose();
        poseStack.translate(0.0, gui.getSlidingProgression(), 0.0);
        ResourceLocation progressTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/progress/" + progress + ".png");
        ResourceLocation stanceTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stance/" + style + (stack == 4 ? "_1" : "_0") + ".png");
        ResourceLocation stackBgTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/ui" + stack + ".png");
        ResourceLocation stackTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/stack" + stack + ".png");
        ResourceLocation goldenLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/gold.png");
        ResourceLocation whiteLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/white.png");
        ResourceLocation redLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/red.png");
        drawTexture(progressTexture, poseStack, pos.x - 12, pos.y - 12);
        drawTexture(stanceTexture, poseStack, pos.x - 12, pos.y - 12);
        drawTexture(stackBgTexture, poseStack, pos.x - 12, pos.y - 12);
        Vec2i light1 = new Vec2i(pos.x - 14, pos.y + 3);
        Vec2i light2 = new Vec2i(pos.x - 5, pos.y + 1);
        Vec2i light3 = new Vec2i(pos.x + 4, pos.y - 5);
        List<Vec2i> lightList = List.of(light1, light2, light3);

        if (container.isFull()) {
            for (Vec2i lightPos : lightList) {
                drawTexture(goldenLightTexture, poseStack, lightPos.x, lightPos.y);
            }
        }

        if (container.getDataManager().getDataValue(RED_TIMER) > 0) {
            int star = Math.min(container.getDataManager().getDataValue(STARS_CONSUMED), 3);
            if (star > 0) {
                for (int i = 0; i < star; i++) {
                    Vec2i lightPos = lightList.get(i);
                    drawTexture(redLightTexture, poseStack, lightPos.x, lightPos.y);
                }
            }
        }

        if (stack > 0) {
            for (int i = 0; i < Math.min(stack, 3); i++) {
                Vec2i lightPos = lightList.get(i);
                drawTexture(whiteLightTexture, poseStack, lightPos.x, lightPos.y);
            }
            drawTexture(stackTexture, poseStack, pos.x - 12, pos.y - 12);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public void drawTexture(ResourceLocation texture, PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, x, y, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
    }

}

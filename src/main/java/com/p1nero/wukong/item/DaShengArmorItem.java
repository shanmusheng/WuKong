package com.p1nero.wukong.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class DaShengArmorItem extends GeoArmorItem implements IAnimatable {
    protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("idle");

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public DaShengArmorItem(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    private <T extends DaShengArmorItem> PlayState idleAnimController(final AnimationEvent<T> event) {
        event.getController().setAnimation(IDLE_ANIM);

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "Idle Controller", 0, this::idleAnimController));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull Level level, @NotNull Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, level, entity, p_41407_, p_41408_);
        if (entity instanceof Player player) {
            if (isFullArmor(player)) {
                if (!level.isClientSide) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1, 1));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1, 1));
                    if (player instanceof ServerPlayer serverPlayer && serverPlayer.getAbilities().flying) {
                        serverPlayer.getLevel().sendParticles(ParticleTypes.CLOUD, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 5, 0, 0, 0, 0.01);
                        serverPlayer.getLevel().sendParticles(ParticleTypes.CLOUD, serverPlayer.getX() + 0.4, serverPlayer.getY(), serverPlayer.getZ(), 5, 0, 0, 0, 0.01);
                        serverPlayer.getLevel().sendParticles(ParticleTypes.CLOUD, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ() + 0.4, 5, 0, 0, 0, 0.01);
                        serverPlayer.getLevel().sendParticles(ParticleTypes.CLOUD, serverPlayer.getX() - 0.4, serverPlayer.getY(), serverPlayer.getZ(), 5, 0, 0, 0, 0.01);
                        serverPlayer.getLevel().sendParticles(ParticleTypes.CLOUD, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ() - 0.4, 5, 0, 0, 0, 0.01);
                    }
                }
                if (!player.isCreative()) {
                    player.getAbilities().mayfly = true;
                }
            } else if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @Nullable Level p_41422_, @NotNull List<Component> list, @NotNull TooltipFlag p_41424_) {
        list.add(new TextComponent("好！好！好！").withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));
        list.add(new TextComponent("全套穿着获得筋斗云（创造飞行）"));
        list.add(new TextComponent("【凝星制作组】赞助").withStyle(ChatFormatting.GREEN));
        super.appendHoverText(p_41421_, p_41422_, list, p_41424_);
    }

    public static boolean isFullArmor(LivingEntity livingEntity) {
        return livingEntity.getMainHandItem().is(WukongItems.JIN_GU_BANG.get()) &&
                livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(WukongItems.DASHENG_H.get()) &&
                livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(WukongItems.DASHENG_C.get()) &&
                livingEntity.getItemBySlot(EquipmentSlot.LEGS).is(WukongItems.DASHENG_L.get()) &&
                livingEntity.getItemBySlot(EquipmentSlot.FEET).is(WukongItems.DASHENG_F.get());
    }

    public static boolean isSuit(ItemStack stack) {
        return stack.is(WukongItems.JIN_GU_BANG.get()) ||
                stack.is(WukongItems.DASHENG_H.get()) ||
                stack.is(WukongItems.DASHENG_C.get()) ||
                stack.is(WukongItems.DASHENG_L.get()) ||
                stack.is(WukongItems.DASHENG_F.get());
    }

}

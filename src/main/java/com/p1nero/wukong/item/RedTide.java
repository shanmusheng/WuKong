package com.p1nero.wukong.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.item.WeaponItem;

public class RedTide extends WeaponItem {
    public RedTide(Tier tier, int damageIn, float speedIn, Properties builder) {
        super(tier, damageIn, speedIn, builder);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack itemStack, @NotNull LivingEntity p_43279_, @NotNull LivingEntity p_43280_) {
        return super.hurtEnemy(itemStack, p_43279_, p_43280_);
    }
}

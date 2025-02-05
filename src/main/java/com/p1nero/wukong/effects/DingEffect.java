package com.p1nero.wukong.effects;

import com.p1nero.wukong.client.WuKongSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DingEffect extends MobEffect {
    public DingEffect() {
        super(MobEffectCategory.BENEFICIAL, -13261);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "effect.wukong.ding";
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity entity, @NotNull AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        Level level = entity.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GLOW, entity.getX(), entity.getY() + 1, entity.getZ(), 60, 0, 0, 0, 12);
            serverLevel.sendParticles(ParticleTypes.WAX_OFF, entity.getX(), entity.getY() + 1, entity.getZ(), 60, 0, 0, 0, 12);
        }
        entity.aiStep();
        entity.tick();

        float pitch = (entity.getRandom().nextFloat() * 2.0F - 1.0F) * (0);
        if (!level.isClientSide()) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), WuKongSounds.BREAK.get(), (entity).getSoundSource(), 1f, 1.0F + pitch);
        } else {
            (entity).level.playLocalSound(entity.getX(), entity.getY(), (entity).getZ(), WuKongSounds.BREAK.get(), (entity).getSoundSource(), 1f, 1.0F + pitch, false);
        }
    }
}

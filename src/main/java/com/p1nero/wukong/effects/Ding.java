package com.p1nero.wukong.effects;

import com.p1nero.wukong.client.events.DingEndEvent;
import com.p1nero.wukong.client.events.DingStartEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

public class Ding extends MobEffect {
    public Ding() {
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
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
         DingEndEvent.execute(entity.level, entity);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        LevelAccessor levelAccessor = entity.getLevel();
        DingStartEvent.execute(entity,levelAccessor);
        super.applyEffectTick(entity, amplifier);
    }


}

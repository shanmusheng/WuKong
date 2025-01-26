package com.p1nero.wukong.client.particle;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WuKongParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WukongMoveset.MOD_ID);
    public static final RegistryObject<SimpleParticleType> ENTITY_AFTER_IMAGE_WITH_TEXTURE = PARTICLES.register("after_image_with_texture", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DING = PARTICLES.register("ding", () -> new SimpleParticleType(true));
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void RP(ParticleFactoryRegisterEvent event) {
        ParticleEngine PE = Minecraft.getInstance().particleEngine;
        PE.register(DING.get(), DingParticle.DangerParticleProvider::new);
    }
}

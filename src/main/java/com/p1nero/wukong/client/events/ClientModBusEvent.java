package com.p1nero.wukong.client.events;


import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.particle.EntityAfterImageWithTextureParticle;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.entity.WukongEntities;
import com.p1nero.wukong.item.WukongItems;
import com.p1nero.wukong.item.client.RenderRedTide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= WukongMoveset.MOD_ID, value=Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(ParticleFactoryRegisterEvent event) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(WuKongParticles.ENTITY_AFTER_IMAGE_WITH_TEXTURE.get(), new EntityAfterImageWithTextureParticle.Provider());
    }
    @SubscribeEvent
    public static void onRenderItem(PatchedRenderersEvent.Add event) {
        event.addItemRenderer(WukongItems.RED_TIDE.get(), new RenderRedTide());
    }

    @SubscribeEvent
    public static void bindEntityRenderer(FMLClientSetupEvent event){
        EntityRenderers.register(WukongEntities.CLOUD_STEP_LEFT_ENTITY.get(), NoopRenderer::new);
    }

}

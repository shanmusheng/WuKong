package com.p1nero.wukong.client.events;


import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.particle.EntityAfterImageWithTextureParticle;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.entity.WukongEntities;
import com.p1nero.wukong.entity.client.FakeWukongRenderer;
import com.p1nero.wukong.item.DaShengArmorItem;
import com.p1nero.wukong.item.WukongItems;
import com.p1nero.wukong.item.client.DashengArmorRenderer;
import com.p1nero.wukong.item.client.RenderRedTide;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;

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
        EntityRenderers.register(WukongEntities.FAKE_WUKONG_ENTITY.get(), FakeWukongRenderer::new);
        GeoArmorRenderer.registerArmorRenderer(DaShengArmorItem.class, DashengArmorRenderer::new);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onRenderPatched(PatchedRenderersEvent.Add event) {
        event.addPatchedEntityRenderer(WukongEntities.FAKE_WUKONG_ENTITY.get(), () -> new PHumanoidRenderer<>(Meshes.ALEX));
    }

}

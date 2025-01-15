package com.p1nero.wukong.listener;

import com.p1nero.wukong.WukongMoveset;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.client.forgeevent.RenderEpicFightPlayerEvent;

@Mod.EventBusSubscriber(modid= WukongMoveset.MOD_ID, value= Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEntityListener {
    @SubscribeEvent
    public static void renderEntity(RenderEpicFightPlayerEvent event){

    }
}

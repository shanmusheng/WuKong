package com.p1nero.wukong.events;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event){
        if(event.getEntityLiving() instanceof Player player){

        }
    }

}

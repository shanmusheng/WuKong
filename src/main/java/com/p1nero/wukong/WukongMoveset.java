package com.p1nero.wukong;

import com.mojang.logging.LogUtils;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.effects.WuKongEffects;
import com.p1nero.wukong.entity.WukongEntities;
import com.p1nero.wukong.epicfight.WukongDamageSourceTags;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.CoolDownSkill;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.CloudStepSkill;
import com.p1nero.wukong.epicfight.skill.custom.magicarts.ShenWaiShenFaSkill;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.item.WukongItems;
import com.p1nero.wukong.network.PacketHandler;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib3.GeckoLib;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.damagesource.SourceTag;

@Mod("wukong")
public class WukongMoveset{
    public static final String MOD_ID = "wukong";
    public static final String ITEM_HAS_EFFECT_TIMER_KEY = "wukong_has_effect_timer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WukongMoveset(){
        SourceTag.ENUM_MANAGER.loadPreemptive(WukongDamageSourceTags.class);
        SkillCategory.ENUM_MANAGER.loadPreemptive(WukongSkillCategories.class);
        SkillSlot.ENUM_MANAGER.loadPreemptive(WukongSkillSlots.class);
        WeaponCategory.ENUM_MANAGER.loadPreemptive(WukongWeaponCategories.class);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WukongItems.ITEMS.register(bus);
        WuKongParticles.PARTICLES.register(bus);
        WuKongSounds.SOUND_EVENTS.register(bus);
        WukongEntities.ENTITIES.register(bus);
        WuKongEffects.REGISTRY.register(bus);
        bus.addListener(SmashHeavyAttack::register);
        bus.addListener(ThrustHeavyAttack::register);
        bus.addListener(CoolDownSkill::register);
        bus.addListener(ShenWaiShenFaSkill::register);
        bus.addListener(CloudStepSkill::register);
        bus.addListener(StaffPassive::register);
        PacketHandler.register();
        WukongSkills.registerSkills();

        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(WukongSkills::BuildSkills);
        fg_bus.addListener(WukongAnimations::onPlayerTick);
        fg_bus.addListener(WukongMoveset::onPlayerLoggedIn);
        GeckoLib.initialize();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * 给予指南
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
    }

}

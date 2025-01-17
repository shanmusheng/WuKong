package com.p1nero.wukong.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.server.UpdateWeaponInnatePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Collection;
import java.util.Set;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongKeyMappings {
    public static final MyKeyMapping W = new MyKeyMapping("key.wukong.w", GLFW.GLFW_KEY_W, "key.wukong.category");
    public static final MyKeyMapping JIAO_ZHEN = new MyKeyMapping("key.wukong.jiao_zhen", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.wukong.category");
    public static final MyKeyMapping JXSQ = new MyKeyMapping("key.wukong.jxsq", InputConstants.Type.MOUSE, GLFW.GLFW_KEY_2, "key.wukong.category");
    public static final MyKeyMapping HEAVY = new MyKeyMapping("key.wukong.heavy",  InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.wukong.category");
    public static final MyKeyMapping SMASH_STYLE = new MyKeyMapping("key.wukong.smash_stance", GLFW.GLFW_KEY_Z, "key.wukong.category");
    public static final MyKeyMapping PILLAR_STYLE = new MyKeyMapping("key.wukong.pillar_stance", GLFW.GLFW_KEY_X, "key.wukong.category");
    public static final MyKeyMapping THRUST_STYLE = new MyKeyMapping("key.wukong.thrust_stance", GLFW.GLFW_KEY_C, "key.wukong.category");
    public static final KeyMapping STAFF_FLOWER = new CombatKeyMapping("key.wukong.staff_spin", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.wukong.category");

    @SubscribeEvent
    public static void registerKeys(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(JIAO_ZHEN);
        ClientRegistry.registerKeyBinding(HEAVY);
        ClientRegistry.registerKeyBinding(SMASH_STYLE);
        ClientRegistry.registerKeyBinding(PILLAR_STYLE);
        ClientRegistry.registerKeyBinding(THRUST_STYLE);
        ClientRegistry.registerKeyBinding(STAFF_FLOWER);
    }

}

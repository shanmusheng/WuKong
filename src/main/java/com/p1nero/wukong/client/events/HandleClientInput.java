package com.p1nero.wukong.client.events;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.mixin.ControlEngineAccessor;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.server.UpdateWeaponInnatePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, value = {Dist.CLIENT})
public class HandleClientInput {

    /**
     * 按键切换棍势，确保学过才可以用按键切换。
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }
        LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(localPlayer, LocalPlayerPatch.class);
        if(patch == null){
            return;
        }
        //放动画的时候不能切棍式
        if(patch.getEntityState().inaction() || patch.getEntityState().attacking()){
            return;
        }
        localPlayer.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
            Collection<Skill> styles = capabilitySkill.getLearnedSkills(WukongSkillCategories.STAFF_STYLE);
            Skill skill;
            if (WukongKeyMappings.SMASH_STYLE.isRelease() && styles.contains(WukongSkills.SMASH_STYLE)) {
                skill = WukongSkills.SMASH_STYLE;
            } else if (WukongKeyMappings.THRUST_STYLE.isRelease() && styles.contains(WukongSkills.THRUST_STYLE)) {
                skill = WukongSkills.THRUST_STYLE;
            } else if (WukongKeyMappings.PILLAR_STYLE.isRelease() && styles.contains(WukongSkills.PILLAR_STYLE)) {
                skill = WukongSkills.PILLAR_STYLE;
            } else {
                return;
            }
            SkillContainer skillContainer = patch.getSkill(WukongSkillSlots.STAFF_STYLE);
            skillContainer.setSkill(skill);
            capabilitySkill.addLearnedSkill(skill);
            localPlayer.displayClientMessage(new TranslatableComponent("tips.wukong.style_change").append(skill.getDisplayName()), true);
            EpicFightNetworkManager.sendToServer(new CPChangeSkill(WukongSkillSlots.STAFF_STYLE.universalOrdinal(), -1, skill.toString(), false));
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new UpdateWeaponInnatePacket());
        });
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseInputEvent event) {

        if(Minecraft.getInstance().player != null){

            //重击按下
            if(event.getButton() == WukongKeyMappings.HEAVY.getKey().getValue()){
                heavyAttackKeyPressed(event.getAction());
            }

            //奇术
            if(event.getButton() == WukongKeyMappings.QI_SHU.getKey().getValue()){
                qiShuKeyPressed(event.getAction());
            }

            //身法
            if(event.getButton() == WukongKeyMappings.SHEN_FA.getKey().getValue()){
                shenFaKeyPressed(event.getAction());
            }

            //搅阵判断
            if (WukongKeyMappings.JIAO_ZHEN.getKey().equals(WukongKeyMappings.JIAO_ZHEN.getDefaultKey())) {
                if (event.getButton() == 0) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    LocalPlayerPatch patch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
                    if (player.isAlive() && patch != null && patch.getSkill(SkillSlots.WEAPON_INNATE) != null) {
                        SkillDataManager manager = patch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        if (manager.hasData(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN)) {
                            if (event.getAction() == 1) {
                                manager.setDataSync(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN, true, player);
                            } else if (event.getAction() == 0) {
                                manager.setDataSync(ThrustHeavyAttack.IS_ATTACK_KEY_DOWN, false, player);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event){

        if(Minecraft.getInstance().player != null){
            if(event.getKey() == WukongKeyMappings.HEAVY.getKey().getValue()){
                heavyAttackKeyPressed(event.getAction());
            }

            //奇术
            if(event.getKey() == WukongKeyMappings.QI_SHU.getKey().getValue()){
                qiShuKeyPressed(event.getAction());
            }

            //身法
            if(event.getKey() == WukongKeyMappings.SHEN_FA.getKey().getValue()){
                shenFaKeyPressed(event.getAction());
            }

        }

    }

    public static void heavyAttackKeyPressed(int action){
        if(action == 1){
            sendSkillPacket(SkillSlots.WEAPON_INNATE, WukongKeyMappings.HEAVY);
        }
    }

    public static void qiShuKeyPressed(int action) {
        if (action == 1) {
            sendSkillPacket(WukongSkillSlots.QI_SHU, WukongKeyMappings.QI_SHU);
        }
    }

    public static void shenFaKeyPressed(int action){
        if(action == 1){
            sendSkillPacket(WukongSkillSlots.SHEN_FA, WukongKeyMappings.SHEN_FA);
        }
    }

    public static void sendSkillPacket(SkillSlot slot, KeyMapping key){
        LocalPlayer player = Minecraft.getInstance().player;
        if(player != null && EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)){
            LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
            if(localPlayerPatch != null && localPlayerPatch.getSkill(slot) != null && localPlayerPatch.getSkill(slot).sendExecuteRequest(localPlayerPatch, ClientEngine.getInstance().controllEngine).shouldReserverKey()){
                ControlEngineAccessor controlEngine = (ControlEngineAccessor) ClientEngine.getInstance().controllEngine;
                controlEngine.setReserveCounter(8);
                controlEngine.setReservedOrChargingSkillSlot(slot);
                controlEngine.setReservedKey(key);
            }
        }
    }

}
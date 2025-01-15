package com.p1nero.wukong.network.packet.server;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 同步数据
 */
public record UpdateWeaponInnatePacket() implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
    }
    public static UpdateWeaponInnatePacket decode(FriendlyByteBuf buf){
        return new UpdateWeaponInnatePacket();
    }
    @Override
    public void execute(Player player) {
        if(player != null){
            ItemStack toChange = player.getMainHandItem();
            EpicFightCapabilities.getItemStackCapability(player.getMainHandItem()).changeWeaponInnateSkill(EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class), toChange);
        }
    }
}
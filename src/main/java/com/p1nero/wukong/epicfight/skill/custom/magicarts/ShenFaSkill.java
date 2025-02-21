package com.p1nero.wukong.epicfight.skill.custom.magicarts;

import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.capability.WKPlayer;
import com.p1nero.wukong.epicfight.skill.custom.CoolDownSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 共用冷却
 */
public abstract class ShenFaSkill extends CoolDownSkill {
    public ShenFaSkill(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        SkillDataManager dataManager = executer.getSkill(this).getDataManager();
        dataManager.setDataSync(COOLDOWN_TIMER, getMaxCooldown(), executer.getOriginal());
        WKPlayer wkPlayer = executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).orElse(new WKPlayer());
        wkPlayer.setCooldownShenFa(getMaxCooldown());
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(!container.getExecuter().isLogicalClient()){
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();
            SkillDataManager manager = container.getDataManager();
            WKPlayer wkPlayer = serverPlayer.getCapability(WKCapabilityProvider.WK_PLAYER).orElse(new WKPlayer());
            wkPlayer.setCooldownShenFa(Math.max(wkPlayer.getCooldownShenFa() - 1, 0));
            manager.setDataSync(COOLDOWN_TIMER, wkPlayer.getCooldownShenFa(), serverPlayer);
        }
    }

}

package com.p1nero.wukong.epicfight.weapon;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.StaffStance;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;
///WeaponCapabilityPresets （武器能力预设）
@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongWeaponCapabilityPresets {

    // STAFF: Staff 武器的能力构建器
    public static final Function<Item, CapabilityItem.Builder> STAFF = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    // 选择武器样式（劈棍、戳棍、立棍等），根据玩家的当前技能选择
                    .styleProvider((livingEntityPatch) -> {
                        if (livingEntityPatch instanceof PlayerPatch<?> playerPatch) {
                            // 获取玩家的Staff技能槽
                            SkillContainer container = playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE);
                            if (container.getSkill() instanceof StaffStance style) {
                                return style.getStyle(container); // 返回选定的武器样式
                            }
                        }
                        return WukongStyles.SMASH; // 默认样式为劈棍
                    })
                    .collider(WukongColliders.WK_STAFF)  // 设置碰撞体
                    .hitSound(EpicFightSounds.BLUNT_HIT) // 设置命中声音
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get()) // 设置命中粒子效果
                    .canBePlacedOffhand(false)  // 设置不能作为副手武器
                    .comboCancel((style) -> false)  // 设置是否取消连击
                    .passiveSkill(WukongSkills.STAFF_SPIN)  // 设置被动技能为 StaffSpin
                    // 劈棍（Smash Style）连击动画
                    .newStyleCombo(WukongStyles.SMASH,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
//                            WukongAnimations.STAFF_AUTO4,
//                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1_DASH,
                            WukongAnimations.JUMP_ATTACK_LIGHT)
                    .innateSkill(WukongStyles.SMASH, (itemstack) -> WukongSkills.SMASH_HEAVY_ATTACK)  // 设置劈棍的固有技能
                    // 设置不同动作下的动画
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.IDLE, WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.WALK, WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.CHASE, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.RUN, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.JUMP, WukongAnimations.JUMP)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.FALL, WukongAnimations.FALL)
                    // 戳棍（Thrust Style）连击动画
                    .newStyleCombo(WukongStyles.THRUST,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1_DASH,
                            WukongAnimations.JUMP_ATTACK_LIGHT)
                    .innateSkill(WukongStyles.THRUST, (itemstack) -> WukongSkills.THRUST_HEAVY_ATTACK)  // 设置戳棍的固有技能
                    // 设置戳棍动作下的动画
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.IDLE, WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.WALK, WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.CHASE, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.RUN, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.JUMP, WukongAnimations.JUMP)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.FALL, WukongAnimations.FALL)
                    // 立棍（Pillar Style）连击动画
                    .newStyleCombo(WukongStyles.PILLAR,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1_DASH,
                            WukongAnimations.JUMP_ATTACK_LIGHT)
                    // 设置立棍动作下的动画
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.IDLE, WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.WALK, WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.CHASE, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.RUN, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.JUMP, WukongAnimations.JUMP)
                    .livingMotionModifier(WukongStyles.PILLAR, LivingMotions.FALL, WukongAnimations.FALL);

    // SMASH_ONLY: 只包含劈棍样式的能力构建器
    public static final Function<Item, CapabilityItem.Builder> SMASH_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.SMASH)  // 设置默认样式为劈棍
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)
                    // 劈棍连击动画
                    .newStyleCombo(WukongStyles.SMASH,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1_DASH,
                            WukongAnimations.JUMP_ATTACK_LIGHT)
                    .innateSkill(WukongStyles.SMASH, (itemstack) -> WukongSkills.SMASH_HEAVY_ATTACK)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.IDLE, WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.WALK, WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.CHASE, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.RUN, WukongAnimations.DASH)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.JUMP, WukongAnimations.JUMP)
                    .livingMotionModifier(WukongStyles.SMASH, LivingMotions.FALL, WukongAnimations.FALL);

    // THRUST_ONLY: 只包含戳棍样式的能力构建器
    public static final Function<Item, CapabilityItem.Builder> THRUST_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.THRUST)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)
                    // 戳棍连击动画
                    .newStyleCombo(WukongStyles.THRUST,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO1) // 空中连击
                    .innateSkill(WukongStyles.THRUST, (itemstack) -> WukongSkills.THRUST_HEAVY_ATTACK)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.IDLE, WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.WALK, WukongAnimations.WALK)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.CHASE, WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.THRUST, LivingMotions.RUN, WukongAnimations.RUN);

    // 注册武器能力到事件中
    @SubscribeEvent
    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        // 将武器能力预设（Staff, Smash, Thrust）注册到事件类型条目中
        event.getTypeEntry().put("wk_staff", STAFF);
        event.getTypeEntry().put("smash_only", SMASH_ONLY);
        event.getTypeEntry().put("thrust_only", THRUST_ONLY);
    }

}

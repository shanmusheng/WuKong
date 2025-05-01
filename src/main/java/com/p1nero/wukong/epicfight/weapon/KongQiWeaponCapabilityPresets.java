//package com.p1nero.wukong.epicfight.weapon;
//
//import com.p1nero.wukong.WukongMoveset;
//import com.p1nero.wukong.epicfight.KongQiStyles;
//import com.p1nero.wukong.epicfight.animation.KongqiAnimations;
//import net.minecraft.world.item.Item;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import yesman.epicfight.api.animation.LivingMotions;
//import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
//import yesman.epicfight.gameasset.Animations;
//import yesman.epicfight.gameasset.ColliderPreset;
//import yesman.epicfight.gameasset.EpicFightSounds;
//import yesman.epicfight.world.capabilities.EpicFightCapabilities;
//import yesman.epicfight.world.capabilities.item.CapabilityItem;
//import yesman.epicfight.world.capabilities.item.WeaponCapability;
//
//import java.util.function.Function;
/////WeaponCapabilityPresets （武器能力预设）
//
//@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class KongQiWeaponCapabilityPresets {
//    public static final Function<Item, CapabilityItem.Builder> SWORDING = (item) ->
//            (CapabilityItem.Builder) WeaponCapability.builder().category(KongQiWeaponCategories.KQ_SWORD)
//                    .category(CapabilityItem.WeaponCategories.SWORD)
//                    .styleProvider((playerpatch) -> KongQiStyles.SWORDING)
//                    .collider(ColliderPreset.LONGSWORD)
//                    .hitSound(EpicFightSounds.BLADE_HIT)
//                    .newStyleCombo(KongQiStyles.SWORDING, KongqiAnimations.KONGQI_AUTO1, KongqiAnimations.KONGQI_AUTO2, KongqiAnimations.KONGQI_AUTO3, KongqiAnimations.KONGQI_AUTO1_DASH)
////                    .newStyleCombo(CapabilityItem.Styles.TWO_HAND, Animations.SWORD_DUAL_AUTO1, Animations.SWORD_DUAL_AUTO2, Animations.SWORD_DUAL_AUTO3, Animations.SWORD_DUAL_DASH, Animations.SWORD_DUAL_AIR_SLASH)
////                    .newStyleCombo(CapabilityItem.Styles.MOUNT, Animations.SWORD_MOUNT_ATTACK)
////                    .specialAttack(CapabilityItem.Styles.ONE_HAND, KongQiStyles.SWORD)
////                    .specialAttack(CapabilityItem.Styles.TWO_HAND, Skills.DANCING_EDGE)
//                    .livingMotionModifier(CapabilityItem.Styles.ONE_HAND, LivingMotions.BLOCK, Animations.SWORD_GUARD)
//                    .livingMotionModifier(CapabilityItem.Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
//                    .weaponCombinationPredicator((entitypatch) -> EpicFightCapabilities.getItemStackCapability(entitypatch.getOriginal().getOffhandItem()).getWeaponCategory() == CapabilityItem.WeaponCategories.SWORD);
//
//    // 注册武器能力到事件中
//    //监听这个事件将武器能力预设（Staff, Smash, Thrust）注册到事件类型条目中
//    //第一步拥有一个武器类型
//    //第二部要使用 需要应用到数据包
//    @SubscribeEvent
//    public static void register(WeaponCapabilityPresetRegistryEvent event) {
//        // 将武器能力预设（Staff, Smash, Thrust）注册到事件类型条目中
//        event.getTypeEntry().put("kq_sword", SWORDING);
//    }
//}

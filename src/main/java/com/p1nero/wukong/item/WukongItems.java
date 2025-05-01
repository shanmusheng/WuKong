package com.p1nero.wukong.item;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.item.client.KangJinStaff;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
//注册物品
public class WukongItems {

    public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab("wukong.items") {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(WukongItems.JIN_GU_BANG.get());
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WukongMoveset.MOD_ID);
    public static final RegistryObject<Item> RED_TIDE = ITEMS.register("red_tide", () -> new RedTide(Tiers.NETHERITE, 7, -3, (new Item.Properties()).defaultDurability(1427).rarity(Rarity.UNCOMMON).tab(CREATIVE_MODE_TAB)));
    public static final RegistryObject<Item> JIN_GU_BANG = ITEMS.register("jingubang", () -> new JinGuBang(Tiers.NETHERITE, 11, -3, (new Item.Properties()).defaultDurability(2777).rarity(StaffRarity.SHEN_ZHEN).tab(CREATIVE_MODE_TAB)));
    public static final RegistryObject<Item> DASHENG_H = ITEMS.register("dasheng_h", () -> new DaShengArmorItem(WukongArmorMaterials.DA_SHENG, EquipmentSlot.HEAD, (new Item.Properties()).defaultDurability(2777).rarity(StaffRarity.SHEN_ZHEN)));
    public static final RegistryObject<Item> DASHENG_C = ITEMS.register("dasheng_c", () -> new DaShengArmorItem(WukongArmorMaterials.DA_SHENG, EquipmentSlot.CHEST, (new Item.Properties()).defaultDurability(2777).rarity(StaffRarity.SHEN_ZHEN)));
    public static final RegistryObject<Item> DASHENG_L = ITEMS.register("dasheng_l", () -> new DaShengArmorItem(WukongArmorMaterials.DA_SHENG, EquipmentSlot.LEGS, (new Item.Properties()).defaultDurability(2777).rarity(StaffRarity.SHEN_ZHEN)));
    public static final RegistryObject<Item> DASHENG_F = ITEMS.register("dasheng_f", () -> new DaShengArmorItem(WukongArmorMaterials.DA_SHENG, EquipmentSlot.FEET, (new Item.Properties()).defaultDurability(2777).rarity(StaffRarity.SHEN_ZHEN)));

    public static final RegistryObject<Item> LOST_SWORD = ITEMS.register("lost_swrod", () -> new LostSword(Tiers.NETHERITE, 11, -3, (new Item.Properties()).defaultDurability(2777).tab(CREATIVE_MODE_TAB)));

}

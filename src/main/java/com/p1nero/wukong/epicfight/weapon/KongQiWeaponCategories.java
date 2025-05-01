//package com.p1nero.wukong.epicfight.weapon;
//
//import net.minecraft.world.InteractionHand;
//import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
//import yesman.epicfight.world.capabilities.item.WeaponCategory;
//
//public enum KongQiWeaponCategories implements WeaponCategory {
//    KQ_SWORD;
//    private KongQiWeaponCategories(){
//        this.id = WeaponCategory.ENUM_MANAGER.assign(this);
//    }
//    final int id;
//    @Override
//    public int universalOrdinal() {
//        return this.id;
//    }
//
//    /**
//     * 判断武器是否是悟空棍子类型
//     */
//    public static boolean isWeaponValid(LivingEntityPatch<?> playerPatch){
//        return playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory().equals(KongQiWeaponCategories.KQ_SWORD);
//    }
//}

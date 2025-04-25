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
//这个是主类
@Mod("wukong")  // 模组标识符，表示这是一个名为"Wukong"的模组
public class WukongMoveset {

    public static final String MOD_ID = "wukong";  // 模组的唯一标识符
    public static final String ITEM_HAS_EFFECT_TIMER_KEY = "wukong_has_effect_timer";  // 用于存储计时器的key
    public static final Logger LOGGER = LogUtils.getLogger();  // 日志记录器，方便调试和错误输出

    // 构造函数，模组初始化时会执行
    public WukongMoveset() {
        // 加载伤害源标签（WukongDamageSourceTags）
        SourceTag.ENUM_MANAGER.loadPreemptive(WukongDamageSourceTags.class);
        // 加载技能类别标签（WukongSkillCategories）
        SkillCategory.ENUM_MANAGER.loadPreemptive(WukongSkillCategories.class);
        // 加载技能槽标签（WukongSkillSlots）
        SkillSlot.ENUM_MANAGER.loadPreemptive(WukongSkillSlots.class);
        // 加载武器类别标签（WukongWeaponCategories）
        WeaponCategory.ENUM_MANAGER.loadPreemptive(WukongWeaponCategories.class);

        // 获取模组的事件总线
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册物品、粒子、声音、实体、效果等
        WukongItems.ITEMS.register(bus);
        WuKongParticles.PARTICLES.register(bus);
        WuKongSounds.SOUND_EVENTS.register(bus);
        WukongEntities.ENTITIES.register(bus);
        WuKongEffects.REGISTRY.register(bus);

        // 注册各种技能的监听器和初始化方法
        bus.addListener(SmashHeavyAttack::register);
        bus.addListener(ThrustHeavyAttack::register);
        bus.addListener(CoolDownSkill::register);
        bus.addListener(ShenWaiShenFaSkill::register);
        bus.addListener(CloudStepSkill::register);
        bus.addListener(StaffPassive::register);

        // 注册网络数据包处理器
        PacketHandler.register();

        // 注册技能
        WukongSkills.registerSkills();

        // 注册Forge的事件总线，用于监听玩家技能构建等事件
        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(WukongSkills::BuildSkills);  // 构建技能
        fg_bus.addListener(WukongAnimations::onPlayerTick);  // 每次玩家更新时调用动画更新
        fg_bus.addListener(WukongMoveset::onPlayerLoggedIn);  // 玩家登录时的处理
        GeckoLib.initialize();  // 初始化GeckoLib库，用于处理动画等
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);  // 注册模组配置
    }

    /**
     * 玩家登录时的处理方法
     * 目前并没有具体的实现内容
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 可以在这里执行玩家登录时的相关初始化工作
    }
}

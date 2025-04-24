package com.p1nero.wukong;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

//@Mod("kongqi")  // 模组标识符，表示这是一个名为"Wukong"的模组
public class KongQiMoveset {

    public static final String MOD_ID = "kongqi";  // 模组的唯一标识符
    public static final String ITEM_HAS_EFFECT_TIMER_KEY = "kongqi_has_effect_timer";  // 用于存储计时器的key
    public static final Logger LOGGER = LogUtils.getLogger();  // 日志记录器，方便调试和错误输出
}

package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.animation.custom.WukongDodgeAnimation;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import io.netty.buffer.Unpooled;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 完美闪避回棍势，蓄力的时候完美闪避保留棍势
 */
public class WukongDodgeSkill extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d011cc-f30f-11ed-a05b-0242ac114515"); // 定义事件的UUID，用于监听事件
    private static final SkillDataManager.SkillDataKey<Integer> COUNT = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER); // 定义闪避计数器
    private static final SkillDataManager.SkillDataKey<Integer> DIRECTION = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER); // 定义闪避方向
    private static final SkillDataManager.SkillDataKey<Integer> RESET_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER); // 定义重置计时器
    public static final SkillDataManager.SkillDataKey<Boolean> DODGE_PLAYED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN); // 定义是否播放过完美闪避动画
    public static final int RESET_TICKS = 100; // 定义闪避重置的时长（100 tick）
    protected final StaticAnimationProvider[][] animations; // 定义动画数组，存储不同的闪避动画

    /**
     * 构造方法，初始化技能
     */
    public static WukongDodgeSkill.Builder createDodgeBuilder() {
        return (new WukongDodgeSkill.Builder())
                .setCategory(SkillCategories.DODGE) // 设置技能类别为闪避
                .setActivateType(ActivateType.ONE_SHOT) // 设置技能为一次性激活
                .setResource(Resource.STAMINA); // 设置技能资源消耗为体力
    }

    public WukongDodgeSkill(WukongDodgeSkill.Builder builder) {
        super(builder); // 调用父类构造方法
        animations = builder.animations; // 初始化动画数据
    }

    /**
     * 在技能初始化时，注册闪避成功事件的监听器
     */
    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COUNT); // 注册闪避计数器
        container.getDataManager().registerData(DIRECTION); // 注册方向数据
        container.getDataManager().registerData(RESET_TIMER); // 注册重置计时器
        container.getDataManager().registerData(DODGE_PLAYED); // 注册是否播放完美闪避动画的数据

        // 添加闪避成功事件的监听器
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            Player player = event.getPlayerPatch().getOriginal(); // 获取玩家对象

            // 设置完美闪避标志
            player.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                wkPlayer.setPerfectDodge(true);
            });

            // 如果闪避动画是完美闪避动画
            if (event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation() instanceof WukongDodgeAnimation) {
                // 如果没有播放过完美闪避动画
                if (!container.getDataManager().getDataValue(DODGE_PLAYED)) {
                    event.getPlayerPatch().playSound(WuKongSounds.PERFECT_DODGE.get(), 1, 1); // 播放完美闪避音效

                    // 如果是服务器端，发送粒子效果包
                    if (player.level instanceof ServerLevel) {
                        PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageParticle(player.getId())); // 发送完美闪避的粒子效果
                    }

                    // 获取棍势技能的容器并同步消耗
                    SkillContainer weaponInnateContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                    weaponInnateContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), weaponInnateContainer.getResource() + Config.CHARGING_SPEED.get().floatValue() * 30);

                    // 设置完美闪避已经播放标志
                    container.getDataManager().setData(DODGE_PLAYED, true);

                    // 播放完美闪避的动画
                    event.getPlayerPatch().playAnimationSynchronized(this.animations[3][container.getDataManager().getDataValue(DIRECTION)].get(), 0.0F); // 只播放一次
                }
            }
        }));
    }

    /**
     * 移除技能时取消事件监听器
     */
    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID); // 移除闪避成功事件监听器
    }

    /**
     * 客户端方法，收集玩家输入数据，并打包成 FriendlyByteBuf 以便传输
     */
    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        Input input = executer.getOriginal().input; // 获取玩家的输入
        input.tick(false); // 更新输入状态
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;

        // 创建 FriendlyByteBuf 存储输入数据
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(forward);
        buf.writeInt(backward);
        buf.writeInt(left);
        buf.writeInt(right);

        return buf; // 返回打包好的数据
    }

    /**
     * 客户端方法，将玩家输入数据转换为技能执行包
     */
    @OnlyIn(Dist.CLIENT)
    public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
        // 读取玩家输入数据
        int forward = args.readInt();
        int backward = args.readInt();
        int left = args.readInt();
        int right = args.readInt();

        // 计算纵向和横向的总和
        int vertic = forward + backward;
        int horizon = left + right;

        // 根据输入方向计算旋转角度
        int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);

        // 创建执行技能的包并填充数据
        CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
        packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1); // 设置纵向方向
        packet.getBuffer().writeFloat((float) degree); // 设置旋转角度

        return packet; // 返回封包
    }

    /**
     * 客户端方法，显示技能工具提示（显示技能的消耗）
     */
    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        // 将技能消耗添加到工具提示中
        list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
        return list; // 返回工具提示列表
    }

    /**
     * 服务器端方法，执行技能，播放闪避动画并同步玩家状态
     */
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args); // 调用父类执行方法

        // 读取参数并播放动画
        int i = args.readInt();
        float yaw = args.readFloat();
        SkillDataManager dataManager = executer.getSkill(SkillSlots.DODGE).getDataManager();
        dataManager.setData(DODGE_PLAYED, false); // 重置播放标志
        int count = dataManager.getDataValue(COUNT); // 获取闪避计数器

        // 播放闪避动画（根据计数器轮播）
        executer.playAnimationSynchronized(this.animations[count][i].get(), 0.0F);
        executer.playSound(EpicFightSounds.ROLL, 1.0F, 1.0F); // 播放闪避音效

        // 更新方向数据，并同步到服务器
        dataManager.setDataSync(DIRECTION, i, executer.getOriginal());

        // 如果不是第一次闪避，重置计时器
        if (count != 0) {
            dataManager.setDataSync(RESET_TIMER, RESET_TICKS, executer.getOriginal());
        }

        // 更新闪避计数器
        dataManager.setDataSync(COUNT, ++count % 3, executer.getOriginal());

        // 改变玩家模型的旋转
        executer.changeModelYRot(yaw);
    }

    /**
     * 更新技能容器（处理闪避计数器的重置）
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();

        // 如果计时器大于0，减少计时器
        if (manager.hasData(RESET_TIMER) && manager.getDataValue(RESET_TIMER) > 0) {
            manager.setData(RESET_TIMER, manager.getDataValue(RESET_TIMER) - 1);

            // 如果计时器到达1并且闪避计数器有数据，重置闪避计数器
            if (manager.getDataValue(RESET_TIMER) == 1 && manager.hasData(COUNT)) {
                manager.setData(COUNT, 0);
            }
        }
    }

    /**
     * 检查玩家状态是否允许执行闪避技能
     */
    public boolean isExecutableState(PlayerPatch<?> executer) {
        EntityState playerState = executer.getEntityState();
        return !executer.isUnstable() && playerState.canUseSkill() &&
                !executer.getOriginal().isInWater() &&
                !executer.getOriginal().onClimbable() &&
                executer.getOriginal().getVehicle() == null;
    }

    // 构建器类，用于构建 WukongDodgeSkill 实例
    public static class Builder extends Skill.Builder<WukongDodgeSkill> {
        protected StaticAnimationProvider[][] animations = new StaticAnimationProvider[4][4]; // 动画二维数组

        // 设置技能的类别
        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        // 设置技能的激活类型
        public Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }
        // 设置技能的资源类型
        public Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        // 设置技能的创意标签（物品栏类别）
        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        // 设置动画1
        public WukongDodgeSkill.Builder setAnimations1(StaticAnimationProvider... animations) {
            this.animations[0] = animations;
            return this;
        }

        // 设置动画2
        public WukongDodgeSkill.Builder setAnimations2(StaticAnimationProvider... animations) {
            this.animations[1] = animations;
            return this;
        }

        // 设置动画3
        public WukongDodgeSkill.Builder setAnimations3(StaticAnimationProvider... animations) {
            this.animations[2] = animations;
            return this;
        }

        // 设置完美闪避动画
        public WukongDodgeSkill.Builder setPerfectAnimations(StaticAnimationProvider... animations) {
            this.animations[3] = animations;
            return this;
        }
    }
}

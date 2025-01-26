package com.p1nero.wukong.item;

import com.p1nero.wukong.client.AnimationJudge;
import com.p1nero.wukong.item.client.JinGuBangRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.item.WeaponItem;

import java.util.List;
import java.util.function.Consumer;

public class JinGuBang extends WeaponItem implements IAnimatable {
    public final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final Minecraft mc = Minecraft.getInstance();
    LocalPlayerPatch lpp = EpicFightCapabilities.getEntityPatch(mc.player, LocalPlayerPatch.class);
    public JinGuBang(Tier tier, int damageIn, float speedIn, Properties builder) {
        super(tier, damageIn, speedIn, builder);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level p_41422_, @NotNull List<Component> list, @NotNull TooltipFlag p_41424_) {
        super.appendHoverText(itemStack, p_41422_, list, p_41424_);
        list.add(Component.nullToEmpty("那块铁，挽着些儿就死，磕着些儿就亡，挨挨儿皮破，擦擦儿筋伤！").copy().withStyle(ChatFormatting.GOLD));
        list.add(Component.nullToEmpty("【凝星制作组赞助】"));
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            private JinGuBangRenderer renderer = null;
            @Override public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                if (this.renderer == null)
                    this.renderer = new JinGuBangRenderer();

                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "stackController", 0, animationEvent -> {
            if (lpp != null && lpp.getAnimator().getPlayerFor(null).getAnimation() instanceof StaticAnimation staticAnimation && AnimationJudge.changemode(staticAnimation,lpp)) {
                animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("change", ILoopType.EDefaultLoopTypes.LOOP));
            }
            else {
                animationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

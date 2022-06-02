/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.ItemRenderer
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.opengl.GL11
 */
package cascade.mixin.mixins;

import cascade.event.events.RenderItemInFirstPersonEvent;
import cascade.features.modules.core.ClickGui;
import cascade.features.modules.visual.HandChams;
import cascade.features.modules.visual.NoRender;
import cascade.util.render.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ItemRenderer.class})
public abstract class MixinItemRenderer {
    @Shadow
    @Final
    public Minecraft mc;
    private boolean injection = true;

    @Inject(method={"renderFireInFirstPerson"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderFireInFirstPersonHook(CallbackInfo info) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noOverlay.getValue().booleanValue()) {
            info.cancel();
        }
    }

    @Inject(method={"renderSuffocationOverlay"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderSuffocationOverlay(CallbackInfo ci) {
        if (NoRender.getInstance().isEnabled() && NoRender.getInstance().noOverlay.getValue().booleanValue()) {
            ci.cancel();
        }
    }

    @Shadow
    public abstract void func_187457_a(AbstractClientPlayer var1, float var2, float var3, EnumHand var4, float var5, ItemStack var6, float var7);

    @Shadow
    protected abstract void func_187456_a(float var1, float var2, EnumHandSide var3);

    @Inject(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (this.injection) {
            info.cancel();
            float xOffset = 0.0f;
            float yOffset = 0.0f;
            this.injection = false;
            if (HandChams.getINSTANCE().isEnabled() && hand == EnumHand.MAIN_HAND && stack.func_190926_b()) {
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    this.func_187457_a(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
                }
                GlStateManager.func_179094_E();
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glPushAttrib((int)1048575);
                } else {
                    GlStateManager.func_179123_a();
                }
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glPolygonMode((int)1032, (int)6913);
                }
                GL11.glDisable((int)3553);
                GL11.glDisable((int)2896);
                if (HandChams.getINSTANCE().mode.getValue().equals((Object)HandChams.RenderMode.Wireframe)) {
                    GL11.glEnable((int)2848);
                    GL11.glEnable((int)3042);
                }
                GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getRed() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getGreen() / 255.0f), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : (float)HandChams.getINSTANCE().c.getValue().getBlue() / 255.0f), (float)((float)HandChams.getINSTANCE().c.getValue().getAlpha() / 255.0f));
                this.func_187457_a(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
                GlStateManager.func_179099_b();
                GlStateManager.func_179121_F();
            }
            if (!stack.field_190928_g || HandChams.getINSTANCE().isDisabled()) {
                this.func_187457_a(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
            } else if (!stack.field_190928_g || HandChams.getINSTANCE().isDisabled()) {
                this.func_187457_a(player, p_187457_2_, p_187457_3_, hand, p_187457_5_, stack, p_187457_7_);
            }
            this.injection = true;
        }
    }

    @Redirect(method={"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    public void captainHook(ItemRenderer itemRenderer, EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        RenderItemInFirstPersonEvent pre = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, 0);
        MinecraftForge.EVENT_BUS.post((Event)pre);
        if (!pre.isCanceled()) {
            itemRenderer.func_187462_a(entitylivingbaseIn, pre.getStack(), pre.getTransformType(), leftHanded);
        }
        RenderItemInFirstPersonEvent post = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, 1);
        MinecraftForge.EVENT_BUS.post((Event)post);
    }
}


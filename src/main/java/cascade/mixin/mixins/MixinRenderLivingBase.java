/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.client.event.RenderLivingEvent$Post
 *  net.minecraftforge.client.event.RenderLivingEvent$Pre
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  org.lwjgl.opengl.GL11
 */
package cascade.mixin.mixins;

import cascade.Cascade;
import cascade.event.events.ModelRenderEvent;
import cascade.features.modules.core.ClickGui;
import cascade.features.modules.visual.Chams;
import cascade.util.Util;
import cascade.util.render.ColorUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase>
extends Render<T> {
    private static final ResourceLocation glint;
    private static final ResourceLocation RES_ITEM_GLINT;
    @Shadow
    protected ModelBase mainModel;
    @Shadow
    protected boolean field_188323_j;
    float red;
    float green;
    float blue;

    public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
        super(renderManagerIn);
    }

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
        this.red = 0.0f;
        this.green = 0.0f;
        this.blue = 0.0f;
    }

    @Redirect(method={"renderModel"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderModelHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        boolean cancel = false;
        if (Chams.getINSTANCE().texture.getValue().booleanValue()) {
            GL11.glDisable((int)2929);
            GL11.glDepthMask((boolean)false);
            Color visibleColor2 = ColorUtil.getColor(entityIn, Chams.getINSTANCE().textureColor.getValue().getRed(), Chams.getINSTANCE().textureColor.getValue().getGreen(), Chams.getINSTANCE().textureColor.getValue().getBlue(), Chams.getINSTANCE().textureColor.getValue().getAlpha(), true);
            GL11.glColor4f((float)((float)visibleColor2.getRed() / 255.0f), (float)((float)visibleColor2.getGreen() / 255.0f), (float)((float)visibleColor2.getBlue() / 255.0f), (float)((float)Chams.getINSTANCE().textureColor.getValue().getAlpha() / 255.0f));
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GL11.glEnable((int)2929);
            GL11.glDepthMask((boolean)true);
        } else if (!cancel) {
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Overwrite
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Pre(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z))) {
            boolean shouldSit;
            GlStateManager.func_179094_E();
            GlStateManager.func_179129_p();
            this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
            this.mainModel.isRiding = shouldSit = entity.func_184218_aH() && entity.func_184187_bx() != null && entity.func_184187_bx().shouldRiderSit();
            this.mainModel.isChild = entity.isChild();
            try {
                float f = this.interpolateRotation(((EntityLivingBase)entity).prevRenderYawOffset, ((EntityLivingBase)entity).renderYawOffset, partialTicks);
                float f2 = this.interpolateRotation(((EntityLivingBase)entity).prevRotationYawHead, ((EntityLivingBase)entity).rotationYawHead, partialTicks);
                float f3 = f2 - f;
                if (shouldSit && entity.func_184187_bx() instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity.func_184187_bx();
                    f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                    f3 = f2 - f;
                    float f4 = MathHelper.wrapAngleTo180_float((float)f3);
                    if (f4 < -85.0f) {
                        f4 = -85.0f;
                    }
                    if (f4 >= 85.0f) {
                        f4 = 85.0f;
                    }
                    f = f2 - f4;
                    if (f4 * f4 > 2500.0f) {
                        f += f4 * 0.2f;
                    }
                    f3 = f2 - f;
                }
                float f5 = ((EntityLivingBase)entity).prevRotationPitch + (((EntityLivingBase)entity).rotationPitch - ((EntityLivingBase)entity).prevRotationPitch) * partialTicks;
                this.renderLivingAt(entity, x, y, z);
                float f6 = this.handleRotationFloat(entity, partialTicks);
                this.rotateCorpse(entity, f6, f, partialTicks);
                float f7 = this.func_188322_c(entity, partialTicks);
                float f8 = 0.0f;
                float f9 = 0.0f;
                if (!entity.func_184218_aH()) {
                    f8 = ((EntityLivingBase)entity).field_184618_aE + (((EntityLivingBase)entity).limbSwingAmount - ((EntityLivingBase)entity).field_184618_aE) * partialTicks;
                    f9 = ((EntityLivingBase)entity).field_184619_aG - ((EntityLivingBase)entity).limbSwingAmount * (1.0f - partialTicks);
                    if (entity.isChild()) {
                        f9 *= 3.0f;
                    }
                    if (f8 > 1.0f) {
                        f8 = 1.0f;
                    }
                    f3 = f2 - f;
                }
                GlStateManager.func_179141_d();
                this.mainModel.setLivingAnimations(entity, f9, f8, partialTicks);
                this.mainModel.setRotationAngles(f9, f8, f6, f3, f5, f7, entity);
                if (this.field_188301_f) {
                    boolean flag1 = this.func_177088_c(entity);
                    GlStateManager.func_179142_g();
                    GlStateManager.func_187431_e((int)this.func_188298_c((Entity)entity));
                    if (!this.field_188323_j) {
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                    }
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).func_175149_v()) {
                        this.func_177093_a(entity, f9, f8, partialTicks, f6, f3, f5, f7);
                    }
                    GlStateManager.func_187417_n();
                    GlStateManager.func_179119_h();
                    if (flag1) {
                        this.func_180565_e();
                    }
                } else {
                    if (Chams.getINSTANCE().isEnabled() && entity instanceof EntityPlayer && Chams.getINSTANCE().solid.getValue().booleanValue()) {
                        this.red = (float)Chams.getINSTANCE().solidC.getValue().getRed() / 255.0f;
                        this.green = (float)Chams.getINSTANCE().solidC.getValue().getGreen() / 255.0f;
                        this.blue = (float)Chams.getINSTANCE().solidC.getValue().getBlue() / 255.0f;
                        GlStateManager.func_179094_E();
                        if (Chams.getINSTANCE().glint.getValue().booleanValue()) {
                            GL11.glPushAttrib((int)1048575);
                            GL11.glEnable((int)3042);
                            GL11.glDepthMask((boolean)false);
                            GL11.glEnable((int)3553);
                            GL11.glDisable((int)2929);
                            Util.mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                            GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
                            GL11.glEnable((int)3553);
                            GL11.glBlendFunc((int)768, (int)771);
                            GL11.glBlendFunc((int)770, (int)32772);
                        }
                        GlStateManager.func_179140_f();
                        GL11.glPushAttrib((int)1048575);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        GL11.glDisable((int)2929);
                        GL11.glDepthMask((boolean)false);
                        if (Cascade.friendManager.isFriend(entity.getCommandSenderName()) || entity == Minecraft.getMinecraft().thePlayer) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Chams.getINSTANCE().solidC.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Chams.getINSTANCE().solidC.getValue().getAlpha() / 255.0f));
                        }
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glDisable((int)2896);
                        GL11.glEnable((int)2929);
                        GL11.glDepthMask((boolean)true);
                        if (Cascade.friendManager.isFriend(entity.getCommandSenderName()) || entity == Minecraft.getMinecraft().thePlayer) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Chams.getINSTANCE().solidC.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Chams.getINSTANCE().solidC.getValue().getAlpha() / 255.0f));
                        }
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glEnable((int)2896);
                        GlStateManager.func_179099_b();
                        GlStateManager.func_179145_e();
                        GlStateManager.func_179121_F();
                    }
                    boolean flag1 = this.func_177090_c(entity, partialTicks);
                    if (!(entity instanceof EntityPlayer) || Chams.getINSTANCE().isEnabled() && Chams.getINSTANCE().wireframe.getValue().booleanValue() || Chams.getINSTANCE().isDisabled()) {
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                    }
                    if (flag1) {
                        this.func_177091_f();
                    }
                    GlStateManager.func_179132_a((boolean)true);
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).func_175149_v()) {
                        this.func_177093_a(entity, f9, f8, partialTicks, f6, f3, f5, f7);
                    }
                    if (Chams.getINSTANCE().isEnabled() && entity instanceof EntityPlayer && Chams.getINSTANCE().wireframe.getValue().booleanValue()) {
                        this.red = (float)Chams.getINSTANCE().wireC.getValue().getRed() / 255.0f;
                        this.green = (float)Chams.getINSTANCE().wireC.getValue().getGreen() / 255.0f;
                        this.blue = (float)Chams.getINSTANCE().wireC.getValue().getBlue() / 255.0f;
                        GlStateManager.func_179094_E();
                        if (Chams.getINSTANCE().glint.getValue().booleanValue()) {
                            GL11.glPushAttrib((int)1048575);
                            GL11.glEnable((int)3042);
                            GL11.glDepthMask((boolean)false);
                            GL11.glEnable((int)3553);
                            GL11.glDisable((int)2929);
                            Util.mc.getTextureManager().bindTexture(RES_ITEM_GLINT);
                            GL11.glTexCoord3d((double)1.0, (double)1.0, (double)1.0);
                            GL11.glEnable((int)3553);
                            GL11.glBlendFunc((int)768, (int)771);
                            GL11.glBlendFunc((int)770, (int)32772);
                        }
                        GL11.glPushAttrib((int)1048575);
                        GL11.glPolygonMode((int)1032, (int)6913);
                        GL11.glDisable((int)3553);
                        GL11.glDisable((int)2896);
                        GL11.glDisable((int)2929);
                        GL11.glEnable((int)2848);
                        GL11.glEnable((int)3042);
                        GL11.glBlendFunc((int)770, (int)771);
                        if (Cascade.friendManager.isFriend(entity.getCommandSenderName()) || entity == Minecraft.getMinecraft().thePlayer) {
                            GL11.glColor4f((float)0.0f, (float)191.0f, (float)255.0f, (float)((float)Chams.getINSTANCE().wireC.getValue().getAlpha() / 255.0f));
                        } else {
                            GL11.glColor4f((float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRed() / 255.0f : this.red), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getGreen() / 255.0f : this.green), (float)(ClickGui.getInstance().rainbow.getValue() != false ? (float)ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getBlue() / 255.0f : this.blue), (float)((float)Chams.getINSTANCE().wireC.getValue().getAlpha() / 255.0f));
                        }
                        GL11.glLineWidth((float)Chams.getINSTANCE().lineWidth.getValue().floatValue());
                        this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                        GL11.glEnable((int)2896);
                        GlStateManager.func_179099_b();
                        GlStateManager.func_179121_F();
                    }
                }
                GlStateManager.func_179101_C();
            }
            catch (Exception exception) {
                // empty catch block
            }
            GlStateManager.func_179138_g((int)OpenGlHelper.lightmapTexUnit);
            GlStateManager.func_179098_w();
            GlStateManager.func_179138_g((int)OpenGlHelper.defaultTexUnit);
            GlStateManager.func_179089_o();
            GlStateManager.func_179121_F();
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            MinecraftForge.EVENT_BUS.post((Event)new RenderLivingEvent.Post(entity, (RenderLivingBase)RenderLivingBase.class.cast((Object)this), partialTicks, x, y, z));
        }
    }

    @Redirect(method={"renderModel"}, at=@At(value="INVOKE", target="net/minecraft/client/model/ModelBase.render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    private void renderHook(ModelBase model, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        RenderLivingBase renderLiving = (RenderLivingBase)RenderLivingBase.class.cast((Object)this);
        EntityLivingBase entity = (EntityLivingBase)entityIn;
        ModelRenderEvent.Pre event = new ModelRenderEvent.Pre(renderLiving, entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (!event.isCanceled()) {
            model.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        MinecraftForge.EVENT_BUS.post((Event)new ModelRenderEvent.Post(renderLiving, entity, model, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale));
    }

    @Shadow
    protected abstract boolean func_193115_c(EntityLivingBase var1);

    @Shadow
    protected abstract float getSwingProgress(T var1, float var2);

    @Shadow
    protected abstract float interpolateRotation(float var1, float var2, float var3);

    @Shadow
    protected abstract float handleRotationFloat(T var1, float var2);

    @Shadow
    protected abstract void rotateCorpse(T var1, float var2, float var3, float var4);

    @Shadow
    public abstract float func_188322_c(T var1, float var2);

    @Shadow
    protected abstract void func_180565_e();

    @Shadow
    protected abstract boolean func_177088_c(T var1);

    @Shadow
    protected abstract void renderLivingAt(T var1, double var2, double var4, double var6);

    @Shadow
    protected abstract void func_177091_f();

    @Shadow
    protected abstract void renderModel(T var1, float var2, float var3, float var4, float var5, float var6, float var7);

    @Shadow
    protected abstract void func_177093_a(T var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

    @Shadow
    protected abstract boolean func_177090_c(T var1, float var2);

    static {
        RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
        glint = new ResourceLocation("textures/shinechams.png");
    }
}


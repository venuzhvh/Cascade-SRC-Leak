/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderGlobal
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.Display
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.glu.GLU
 *  org.lwjgl.util.glu.Sphere
 */
package cascade.util.render;

import cascade.Cascade;
import cascade.util.Util;
import cascade.util.entity.EntityUtil;
import cascade.util.render.ColorUtil;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Objects;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

public class RenderUtil
implements Util {
    private static final Frustum frustrum = new Frustum();
    private static final FloatBuffer screenCoords = BufferUtils.createFloatBuffer((int)3);
    private static final IntBuffer viewport = BufferUtils.createIntBuffer((int)16);
    private static final FloatBuffer modelView = BufferUtils.createFloatBuffer((int)16);
    private static final FloatBuffer projection = BufferUtils.createFloatBuffer((int)16);
    public static RenderItem itemRender = mc.func_175599_af();
    public static ICamera camera = new Frustum();
    private static boolean depth = GL11.glIsEnabled((int)2896);
    private static boolean texture = GL11.glIsEnabled((int)3042);
    private static boolean clean = GL11.glIsEnabled((int)3553);
    private static boolean bind = GL11.glIsEnabled((int)2929);
    private static boolean override = GL11.glIsEnabled((int)2848);
    public static BufferBuilder bufferbuilder;
    public static Tessellator tessellator;
    public static Tessellator tessellator2;
    public static BufferBuilder builder;
    public static int deltaTime;

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 1.0f, 1.0f);
    }

    public static void drawGradientRainbowOutLine(double left, double top, double right, double bottom, float width) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)width);
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        int rainbow = RenderUtil.rainbow(1).getRGB();
        int rainbow2 = RenderUtil.rainbow(1000).getRGB();
        float a4 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
        float r4 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
        float g4 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
        float b4 = (float)(rainbow & 0xFF) / 255.0f;
        float a1 = (float)(rainbow2 >> 24 & 0xFF) / 255.0f;
        float r1 = (float)(rainbow2 >> 16 & 0xFF) / 255.0f;
        float g1 = (float)(rainbow2 >> 8 & 0xFF) / 255.0f;
        float b1 = (float)(rainbow2 & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        bufferbuilder.func_181662_b(right, bottom, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
        bufferbuilder.func_181662_b(right, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        bufferbuilder.func_181662_b(left, top, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
        bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static Vec3d updateToCamera(Vec3d vec) {
        return new Vec3d(vec.xCoord - RenderUtil.mc.func_175598_ae().viewerPosX, vec.yCoord - RenderUtil.mc.func_175598_ae().viewerPosY, vec.zCoord - RenderUtil.mc.func_175598_ae().viewerPosZ);
    }

    public static void addBuilderVertex(BufferBuilder bufferBuilder, double x, double y, double z, Color color) {
        bufferBuilder.func_181662_b(x, y, z).func_181666_a((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f).func_181675_d();
    }

    public static void prepare() {
        GlStateManager.func_179094_E();
        GlStateManager.func_179097_i();
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a((boolean)false);
        GlStateManager.func_179118_c();
        GlStateManager.func_179129_p();
        GlStateManager.func_179147_l();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glBlendFunc((int)770, (int)771);
    }

    public static void release() {
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179145_e();
        GlStateManager.func_179126_j();
        GlStateManager.func_179141_d();
        GlStateManager.func_179121_F();
        GL11.glEnable((int)3553);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public static void drawOutlineRect(double left, double top, double right, double bottom, Color color, float lineWidth) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color.getRGB() >> 24 & 0xFF) / 255.0f;
        float f = (float)(color.getRGB() >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color.getRGB() >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color.getRGB() & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GL11.glPolygonMode((int)1032, (int)6913);
        GL11.glLineWidth((float)lineWidth);
        GlStateManager.func_179090_x();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179131_c((float)f, (float)f1, (float)f2, (float)f3);
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(left, bottom, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(right, bottom, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(right, top, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(left, top, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public static void drawRectangleCorrectly(int x, int y, int w, int h, int color) {
        GL11.glLineWidth((float)1.0f);
        Gui.drawRect((int)x, (int)y, (int)(x + w), (int)(y + h), (int)color);
    }

    public static AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - RenderUtil.mc.func_175598_ae().viewerPosX, bb.minY - RenderUtil.mc.func_175598_ae().viewerPosY, bb.minZ - RenderUtil.mc.func_175598_ae().viewerPosZ, bb.maxX - RenderUtil.mc.func_175598_ae().viewerPosX, bb.maxY - RenderUtil.mc.func_175598_ae().viewerPosY, bb.maxZ - RenderUtil.mc.func_175598_ae().viewerPosZ);
    }

    public static boolean isInViewFrustrum(Entity entity) {
        return RenderUtil.isInViewFrustrum(entity.func_174813_aQ()) || entity.ignoreFrustumCheck;
    }

    public static boolean isInViewFrustrum(AxisAlignedBB bb) {
        Entity current = Minecraft.getMinecraft().func_175606_aa();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static Vec3d to2D(double x, double y, double z) {
        GL11.glGetFloat((int)2982, (FloatBuffer)modelView);
        GL11.glGetFloat((int)2983, (FloatBuffer)projection);
        GL11.glGetInteger((int)2978, (IntBuffer)viewport);
        boolean result = GLU.gluProject((float)((float)x), (float)((float)y), (float)((float)z), (FloatBuffer)modelView, (FloatBuffer)projection, (IntBuffer)viewport, (FloatBuffer)screenCoords);
        if (result) {
            return new Vec3d((double)screenCoords.get(0), (double)((float)Display.getHeight() - screenCoords.get(1)), (double)screenCoords.get(2));
        }
        return null;
    }

    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, boolean outline, float outlineWidth, int color) {
        boolean blend = GL11.glIsEnabled((int)3042);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        RenderUtil.hexColor(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
        GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
        GL11.glVertex2d((double)x, (double)y);
        GL11.glEnd();
        if (outline) {
            GL11.glLineWidth((float)outlineWidth);
            GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)alpha);
            GL11.glBegin((int)2);
            GL11.glVertex2d((double)x, (double)y);
            GL11.glVertex2d((double)(x - size / widthDiv), (double)(y + size));
            GL11.glVertex2d((double)x, (double)(y + size / heightDiv));
            GL11.glVertex2d((double)(x + size / widthDiv), (double)(y + size));
            GL11.glVertex2d((double)x, (double)y);
            GL11.glEnd();
        }
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        if (!blend) {
            GL11.glDisable((int)3042);
        }
        GL11.glDisable((int)2848);
    }

    public static void hexColor(int hexColor) {
        float red = (float)(hexColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(hexColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hexColor & 0xFF) / 255.0f;
        float alpha = (float)(hexColor >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder BufferBuilder2 = tessellator.func_178180_c();
        BufferBuilder2.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        BufferBuilder2.func_181662_b((double)(x + 0), (double)(y + height), (double)zLevel).func_187315_a((double)((float)(textureX + 0) * 0.00390625f), (double)((float)(textureY + height) * 0.00390625f)).func_181675_d();
        BufferBuilder2.func_181662_b((double)(x + width), (double)(y + height), (double)zLevel).func_187315_a((double)((float)(textureX + width) * 0.00390625f), (double)((float)(textureY + height) * 0.00390625f)).func_181675_d();
        BufferBuilder2.func_181662_b((double)(x + width), (double)(y + 0), (double)zLevel).func_187315_a((double)((float)(textureX + width) * 0.00390625f), (double)((float)(textureY + 0) * 0.00390625f)).func_181675_d();
        BufferBuilder2.func_181662_b((double)(x + 0), (double)(y + 0), (double)zLevel).func_187315_a((double)((float)(textureX + 0) * 0.00390625f), (double)((float)(textureY + 0) * 0.00390625f)).func_181675_d();
        tessellator.draw();
    }

    public static void blockESP(BlockPos b, Color c, double length, double length2) {
        RenderUtil.blockEsp(b, c, length, length2);
    }

    public static void drawBoxESP(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, secondC ? secondColor : color, lineWidth, air);
        }
    }

    public static void glScissor(float x, float y, float x1, float y1, ScaledResolution sr) {
        GL11.glScissor((int)((int)(x * (float)sr.getScaleFactor())), (int)((int)((float)RenderUtil.mc.displayHeight - y1 * (float)sr.getScaleFactor())), (int)((int)((x1 - x) * (float)sr.getScaleFactor())), (int)((int)((y1 - y) * (float)sr.getScaleFactor())));
    }

    public static void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.func_179094_E();
        GlStateManager.func_179090_x();
        GlStateManager.func_179147_l();
        GlStateManager.func_179118_c();
        GlStateManager.func_179120_a((int)770, (int)771, (int)1, (int)0);
        GlStateManager.func_179103_j((int)7425);
        GL11.glLineWidth((float)thickness);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)x, (double)y, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double)x1, (double)y1, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GlStateManager.func_179103_j((int)7424);
        GL11.glDisable((int)2848);
        GlStateManager.func_179084_k();
        GlStateManager.func_179141_d();
        GlStateManager.func_179098_w();
        GlStateManager.func_179121_F();
    }

    public static void drawBox(BlockPos pos, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - RenderUtil.mc.func_175598_ae().viewerPosX, (double)pos.func_177956_o() - RenderUtil.mc.func_175598_ae().viewerPosY, (double)pos.func_177952_p() - RenderUtil.mc.func_175598_ae().viewerPosZ, (double)(pos.func_177958_n() + 1) - RenderUtil.mc.func_175598_ae().viewerPosX, (double)(pos.func_177956_o() + 1) - RenderUtil.mc.func_175598_ae().viewerPosY, (double)(pos.func_177952_p() + 1) - RenderUtil.mc.func_175598_ae().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.func_175606_aa()).posX, RenderUtil.mc.func_175606_aa().posY, RenderUtil.mc.func_175606_aa().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.minY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.minZ + RenderUtil.mc.func_175598_ae().viewerPosZ, bb.maxX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.maxY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.maxZ + RenderUtil.mc.func_175598_ae().viewerPosZ))) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            RenderGlobal.func_189696_b((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
        }
    }

    public static void drawBlockOutline(BlockPos pos, Color color, float linewidth, boolean air) {
        IBlockState iblockstate = RenderUtil.mc.theWorld.func_180495_p(pos);
        if ((air || iblockstate.func_185904_a() != Material.air) && RenderUtil.mc.theWorld.func_175723_af().func_177746_a(pos)) {
            assert (RenderUtil.mc.field_175622_Z != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.field_175622_Z, mc.func_184121_ak());
            RenderUtil.drawBlockOutline(iblockstate.func_185918_c((World)RenderUtil.mc.theWorld, pos).func_186662_g((double)0.002f).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), color, linewidth);
        }
    }

    public static void drawBlockOutline(AxisAlignedBB bb, Color color, float linewidth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void drawBoxESP(BlockPos pos, Color color, float lineWidth, boolean outline, boolean box, int boxAlpha) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - RenderUtil.mc.func_175598_ae().viewerPosX, (double)pos.func_177956_o() - RenderUtil.mc.func_175598_ae().viewerPosY, (double)pos.func_177952_p() - RenderUtil.mc.func_175598_ae().viewerPosZ, (double)(pos.func_177958_n() + 1) - RenderUtil.mc.func_175598_ae().viewerPosX, (double)(pos.func_177956_o() + 1) - RenderUtil.mc.func_175598_ae().viewerPosY, (double)(pos.func_177952_p() + 1) - RenderUtil.mc.func_175598_ae().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.func_175606_aa()).posX, RenderUtil.mc.func_175606_aa().posY, RenderUtil.mc.func_175606_aa().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.minY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.minZ + RenderUtil.mc.func_175598_ae().viewerPosZ, bb.maxX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.maxY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.maxZ + RenderUtil.mc.func_175598_ae().viewerPosZ))) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            GL11.glLineWidth((float)lineWidth);
            double dist = RenderUtil.mc.thePlayer.getDistance((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() + 0.5f), (double)((float)pos.func_177952_p() + 0.5f)) * 0.75;
            if (box) {
                RenderGlobal.func_189696_b((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)boxAlpha / 255.0f));
            }
            if (outline) {
                RenderGlobal.func_189694_a((double)bb.minX, (double)bb.minY, (double)bb.minZ, (double)bb.maxX, (double)bb.maxY, (double)bb.maxZ, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            }
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
        }
    }

    public static void drawText(BlockPos pos, String text) {
        GlStateManager.func_179094_E();
        RenderUtil.glBillboardDistanceScaled((float)pos.func_177958_n() + 0.5f, (float)pos.func_177956_o() + 0.5f, (float)pos.func_177952_p() + 0.5f, (EntityPlayer)RenderUtil.mc.thePlayer, 1.0f);
        GlStateManager.func_179097_i();
        GlStateManager.func_179137_b((double)(-((double)Cascade.textManager.getStringWidth(text) / 2.0)), (double)0.0, (double)0.0);
        Cascade.textManager.drawStringWithShadow(text, 0.0f, 0.0f, -5592406);
        GlStateManager.func_179121_F();
    }

    public static void drawOutlinedBlockESP(BlockPos pos, Color color, float linewidth) {
        IBlockState iblockstate = RenderUtil.mc.theWorld.func_180495_p(pos);
        Vec3d interp = EntityUtil.interpolateEntity((Entity)RenderUtil.mc.thePlayer, mc.func_184121_ak());
        RenderUtil.drawBoundingBox(iblockstate.func_185918_c((World)RenderUtil.mc.theWorld, pos).func_186662_g((double)0.002f).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), linewidth, ColorUtil.toRGBA(color));
    }

    public static void blockEsp(BlockPos blockPos, Color c, double length, double length2) {
        double x = (double)blockPos.func_177958_n() - RenderUtil.mc.field_175616_W.renderPosX;
        double y = (double)blockPos.func_177956_o() - RenderUtil.mc.field_175616_W.renderPosY;
        double z = (double)blockPos.func_177952_p() - RenderUtil.mc.field_175616_W.renderPosZ;
        GL11.glPushMatrix();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)2.0f);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4d((double)((float)c.getRed() / 255.0f), (double)((float)c.getGreen() / 255.0f), (double)((float)c.getBlue() / 255.0f), (double)0.25);
        RenderUtil.drawColorBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length), 0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glColor4d((double)0.0, (double)0.0, (double)0.0, (double)0.5);
        RenderUtil.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + length2, y + 1.0, z + length));
        GL11.glLineWidth((float)2.0f);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public static void drawScissorRect(float x, float y, float w, float h) {
        RenderUtil.prepareScissor(x, y, w, h);
        GL11.glEnable((int)3089);
        RenderUtil.drawRect(x, y, w, h, new Color(0, 0, 0, 0).getRGB());
        GL11.glDisable((int)3089);
    }

    public static void prepareScissor(float x, float y, float width, float height) {
        GL11.glPushAttrib((int)524288);
        GL11.glScissor((int)((int)(x * (float)new ScaledResolution(mc).getScaleFactor())), (int)((int)((float)new ScaledResolution(mc).getScaledHeight() - height) * new ScaledResolution(mc).getScaleFactor()), (int)((int)(width - x) * new ScaledResolution(mc).getScaleFactor()), (int)((int)(height - y) * new ScaledResolution(mc).getScaleFactor()));
        GL11.glEnable((int)3089);
    }

    public static void restoreScissor() {
        GL11.glDisable((int)3089);
        GL11.glPopAttrib();
    }

    public static void drawRect(float x, float y, float w, float h, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_179120_a((int)770, (int)771, (int)1, (int)0);
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b((double)x, (double)h, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double)w, (double)h, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double)w, (double)y, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b((double)x, (double)y, 0.0).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    public static void drawColorBox(AxisAlignedBB axisalignedbb, float red, float green, float blue, float alpha) {
        Tessellator ts = Tessellator.func_178181_a();
        BufferBuilder vb = ts.func_178180_c();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
        vb.func_181668_a(7, DefaultVertexFormats.field_181707_g);
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        vb.func_181662_b(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        ts.draw();
    }

    public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder vertexbuffer = tessellator.func_178180_c();
        vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181675_d();
        tessellator.draw();
        vertexbuffer.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181675_d();
        tessellator.draw();
        vertexbuffer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.minY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).func_181675_d();
        vertexbuffer.func_181662_b(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).func_181675_d();
        tessellator.draw();
    }

    public static void glrendermethod() {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glLineWidth((float)2.0f);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2884);
        GL11.glDisable((int)2929);
        double viewerPosX = RenderUtil.mc.func_175598_ae().viewerPosX;
        double viewerPosY = RenderUtil.mc.func_175598_ae().viewerPosY;
        double viewerPosZ = RenderUtil.mc.func_175598_ae().viewerPosZ;
        GL11.glPushMatrix();
        GL11.glTranslated((double)(-viewerPosX), (double)(-viewerPosY), (double)(-viewerPosZ));
    }

    public static void glStart(float n, float n2, float n3, float n4) {
        RenderUtil.glrendermethod();
        GL11.glColor4f((float)n, (float)n2, (float)n3, (float)n4);
    }

    public static void glEnd() {
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }

    public static AxisAlignedBB getBoundingBox(BlockPos blockPos) {
        return RenderUtil.mc.theWorld.func_180495_p(blockPos).func_185900_c((IBlockAccess)RenderUtil.mc.theWorld, blockPos).func_186670_a(blockPos);
    }

    public static void drawOutlinedBox(AxisAlignedBB axisAlignedBB) {
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.minY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.maxX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.maxZ);
        GL11.glVertex3d((double)axisAlignedBB.minX, (double)axisAlignedBB.maxY, (double)axisAlignedBB.minZ);
        GL11.glEnd();
    }

    public static void drawFilledBoxESPN(BlockPos pos, Color color) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - RenderUtil.mc.func_175598_ae().viewerPosX, (double)pos.func_177956_o() - RenderUtil.mc.func_175598_ae().viewerPosY, (double)pos.func_177952_p() - RenderUtil.mc.func_175598_ae().viewerPosZ, (double)(pos.func_177958_n() + 1) - RenderUtil.mc.func_175598_ae().viewerPosX, (double)(pos.func_177956_o() + 1) - RenderUtil.mc.func_175598_ae().viewerPosY, (double)(pos.func_177952_p() + 1) - RenderUtil.mc.func_175598_ae().viewerPosZ);
        int rgba = ColorUtil.toRGBA(color);
        RenderUtil.drawFilledBox(bb, rgba);
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)width);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;
        GlStateManager.func_179137_b((double)((double)x - RenderUtil.mc.func_175598_ae().renderPosX), (double)((double)y - RenderUtil.mc.func_175598_ae().renderPosY), (double)((double)z - RenderUtil.mc.func_175598_ae().renderPosZ));
        GlStateManager.func_187432_a((float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)(-RenderUtil.mc.thePlayer.rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179114_b((float)RenderUtil.mc.thePlayer.rotationPitch, (float)(RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.func_179152_a((float)(-scale), (float)(-scale), (float)scale);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        RenderUtil.glBillboard(x, y, z);
        int distance = (int)player.getDistance((double)x, (double)y, (double)z);
        float scaleDistance = (float)distance / 2.0f / (2.0f + (2.0f - scale));
        if (scaleDistance < 1.0f) {
            scaleDistance = 1.0f;
        }
        GlStateManager.func_179152_a((float)scaleDistance, (float)scaleDistance, (float)scaleDistance);
    }

    public static void drawColoredBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)width);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, 0.0f).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, 0.0f).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, 0.0f).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, 0.0f).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, 0.0f).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void drawSphere(double x, double y, double z, float size, int slices, int stacks) {
        Sphere s = new Sphere();
        GL11.glPushMatrix();
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)1.2f);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        s.setDrawStyle(100013);
        GL11.glTranslated((double)(x - RenderUtil.mc.field_175616_W.renderPosX), (double)(y - RenderUtil.mc.field_175616_W.renderPosY), (double)(z - RenderUtil.mc.field_175616_W.renderPosZ));
        s.draw(size, slices, stacks);
        GL11.glLineWidth((float)2.0f);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void GLPre(float lineWidth) {
        depth = GL11.glIsEnabled((int)2896);
        texture = GL11.glIsEnabled((int)3042);
        clean = GL11.glIsEnabled((int)3553);
        bind = GL11.glIsEnabled((int)2929);
        override = GL11.glIsEnabled((int)2848);
        RenderUtil.GLPre(depth, texture, clean, bind, override, lineWidth);
    }

    public static void GlPost() {
        RenderUtil.GLPost(depth, texture, clean, bind, override);
    }

    private static void GLPre(boolean depth, boolean texture, boolean clean, boolean bind, boolean override, float lineWidth) {
        if (depth) {
            GL11.glDisable((int)2896);
        }
        if (!texture) {
            GL11.glEnable((int)3042);
        }
        GL11.glLineWidth((float)lineWidth);
        if (clean) {
            GL11.glDisable((int)3553);
        }
        if (bind) {
            GL11.glDisable((int)2929);
        }
        if (!override) {
            GL11.glEnable((int)2848);
        }
        GlStateManager.func_187401_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.func_179132_a((boolean)false);
    }

    public static float[][] getBipedRotations(ModelBiped biped) {
        float[][] rotations = new float[5][];
        float[] headRotation = new float[]{biped.bipedHead.rotateAngleX, biped.bipedHead.rotateAngleY, biped.bipedHead.rotateAngleZ};
        rotations[0] = headRotation;
        float[] rightArmRotation = new float[]{biped.field_178723_h.rotateAngleX, biped.field_178723_h.rotateAngleY, biped.field_178723_h.rotateAngleZ};
        rotations[1] = rightArmRotation;
        float[] leftArmRotation = new float[]{biped.field_178724_i.rotateAngleX, biped.field_178724_i.rotateAngleY, biped.field_178724_i.rotateAngleZ};
        rotations[2] = leftArmRotation;
        float[] rightLegRotation = new float[]{biped.field_178721_j.rotateAngleX, biped.field_178721_j.rotateAngleY, biped.field_178721_j.rotateAngleZ};
        rotations[3] = rightLegRotation;
        float[] leftLegRotation = new float[]{biped.field_178722_k.rotateAngleX, biped.field_178722_k.rotateAngleY, biped.field_178722_k.rotateAngleZ};
        rotations[4] = leftLegRotation;
        return rotations;
    }

    private static void GLPost(boolean depth, boolean texture, boolean clean, boolean bind, boolean override) {
        GlStateManager.func_179132_a((boolean)true);
        if (!override) {
            GL11.glDisable((int)2848);
        }
        if (bind) {
            GL11.glEnable((int)2929);
        }
        if (clean) {
            GL11.glEnable((int)3553);
        }
        if (!texture) {
            GL11.glDisable((int)3042);
        }
        if (depth) {
            GL11.glEnable((int)2896);
        }
    }

    public static void drawArc(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
        GL11.glBegin((int)4);
        int i = (int)((float)num_segments / (360.0f / start_angle)) + 1;
        while ((float)i <= (float)num_segments / (360.0f / end_angle)) {
            double previousangle = Math.PI * 2 * (double)(i - 1) / (double)num_segments;
            double angle = Math.PI * 2 * (double)i / (double)num_segments;
            GL11.glVertex2d((double)cx, (double)cy);
            GL11.glVertex2d((double)((double)cx + Math.cos(angle) * (double)r), (double)((double)cy + Math.sin(angle) * (double)r));
            GL11.glVertex2d((double)((double)cx + Math.cos(previousangle) * (double)r), (double)((double)cy + Math.sin(previousangle) * (double)r));
            ++i;
        }
        RenderUtil.glEnd();
    }

    public static void drawArcOutline(float cx, float cy, float r, float start_angle, float end_angle, int num_segments) {
        GL11.glBegin((int)2);
        int i = (int)((float)num_segments / (360.0f / start_angle)) + 1;
        while ((float)i <= (float)num_segments / (360.0f / end_angle)) {
            double angle = Math.PI * 2 * (double)i / (double)num_segments;
            GL11.glVertex2d((double)((double)cx + Math.cos(angle) * (double)r), (double)((double)cy + Math.sin(angle) * (double)r));
            ++i;
        }
        RenderUtil.glEnd();
    }

    public static void drawCircleOutline(float x, float y, float radius) {
        RenderUtil.drawCircleOutline(x, y, radius, 0, 360, 40);
    }

    public static void drawCircleOutline(float x, float y, float radius, int start, int end, int segments) {
        RenderUtil.drawArcOutline(x, y, radius, start, end, segments);
    }

    public static void drawCircle(float x, float y, float radius) {
        RenderUtil.drawCircle(x, y, radius, 0, 360, 64);
    }

    public static void drawCircle(float x, float y, float radius, int start, int end, int segments) {
        RenderUtil.drawArc(x, y, radius, start, end, segments);
    }

    public static void drawOutlinedRoundedRectangle(int x, int y, int width, int height, float radius, float dR, float dG, float dB, float dA, float outlineWidth) {
        RenderUtil.drawRoundedRectangle(x, y, width, height, radius);
        GL11.glColor4f((float)dR, (float)dG, (float)dB, (float)dA);
        RenderUtil.drawRoundedRectangle((float)x + outlineWidth, (float)y + outlineWidth, (float)width - outlineWidth * 2.0f, (float)height - outlineWidth * 2.0f, radius);
    }

    public static void drawRectangle(float x, float y, float width, float height) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glBegin((int)2);
        GL11.glVertex2d((double)width, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)height);
        GL11.glVertex2d((double)width, (double)height);
        RenderUtil.glEnd();
    }

    public static void drawRectangleXY(float x, float y, float width, float height) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glBegin((int)2);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        RenderUtil.glEnd();
    }

    public static void drawFilledRectangle(float x, float y, float width, float height) {
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        RenderUtil.glEnd();
    }

    public static void drawRoundedRectangle(float x, float y, float width, float height, float radius) {
        GL11.glEnable((int)3042);
        RenderUtil.drawArc(x + width - radius, y + height - radius, radius, 0.0f, 90.0f, 16);
        RenderUtil.drawArc(x + radius, y + height - radius, radius, 90.0f, 180.0f, 16);
        RenderUtil.drawArc(x + radius, y + radius, radius, 180.0f, 270.0f, 16);
        RenderUtil.drawArc(x + width - radius, y + radius, radius, 270.0f, 360.0f, 16);
        GL11.glBegin((int)4);
        GL11.glVertex2d((double)(x + width - radius), (double)y);
        GL11.glVertex2d((double)(x + radius), (double)y);
        GL11.glVertex2d((double)(x + width - radius), (double)(y + radius));
        GL11.glVertex2d((double)(x + width - radius), (double)(y + radius));
        GL11.glVertex2d((double)(x + radius), (double)y);
        GL11.glVertex2d((double)(x + radius), (double)(y + radius));
        GL11.glVertex2d((double)(x + width), (double)(y + radius));
        GL11.glVertex2d((double)x, (double)(y + radius));
        GL11.glVertex2d((double)x, (double)(y + height - radius));
        GL11.glVertex2d((double)(x + width), (double)(y + radius));
        GL11.glVertex2d((double)x, (double)(y + height - radius));
        GL11.glVertex2d((double)(x + width), (double)(y + height - radius));
        GL11.glVertex2d((double)(x + width - radius), (double)(y + height - radius));
        GL11.glVertex2d((double)(x + radius), (double)(y + height - radius));
        GL11.glVertex2d((double)(x + width - radius), (double)(y + height));
        GL11.glVertex2d((double)(x + width - radius), (double)(y + height));
        GL11.glVertex2d((double)(x + radius), (double)(y + height - radius));
        GL11.glVertex2d((double)(x + radius), (double)(y + height));
        RenderUtil.glEnd();
    }

    public static void renderOne(float lineWidth) {
        RenderUtil.checkSetupFBO();
        GL11.glPushAttrib((int)1048575);
        GL11.glDisable((int)3008);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2960);
        GL11.glClear((int)1024);
        GL11.glClearStencil((int)15);
        GL11.glStencilFunc((int)512, (int)1, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc((int)512, (int)0, (int)15);
        GL11.glStencilOp((int)7681, (int)7681, (int)7681);
        GL11.glPolygonMode((int)1032, (int)6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc((int)514, (int)1, (int)15);
        GL11.glStencilOp((int)7680, (int)7680, (int)7680);
        GL11.glPolygonMode((int)1032, (int)6913);
    }

    public static void renderFour(Color color) {
        RenderUtil.setColor(color);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glEnable((int)10754);
        GL11.glPolygonOffset((float)1.0f, (float)-2000000.0f);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
    }

    public static void renderFive() {
        GL11.glPolygonOffset((float)1.0f, (float)2000000.0f);
        GL11.glDisable((int)10754);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)2960);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)3008);
        GL11.glPopAttrib();
    }

    public static void setColor(Color color) {
        GL11.glColor4d((double)((double)color.getRed() / 255.0), (double)((double)color.getGreen() / 255.0), (double)((double)color.getBlue() / 255.0), (double)((double)color.getAlpha() / 255.0));
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = RenderUtil.mc.framebufferMc;
        if (fbo != null && fbo.depthBuffer > -1) {
            RenderUtil.setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT((int)fbo.depthBuffer);
        int stencilDepthBufferID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT((int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glRenderbufferStorageEXT((int)36161, (int)34041, (int)RenderUtil.mc.displayWidth, (int)RenderUtil.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36128, (int)36161, (int)stencilDepthBufferID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT((int)36160, (int)36096, (int)36161, (int)stencilDepthBufferID);
    }

    public static void drawBoxESP2(BlockPos pos, Color color, boolean secondC, Color secondColor, float lineWidth, boolean outline, boolean box, int boxAlpha, boolean air, double growAmt) {
        if (box) {
            RenderUtil.drawBox2(pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), boxAlpha), growAmt);
        }
        if (outline) {
            RenderUtil.drawBlockOutline2(pos, secondC ? secondColor : color, lineWidth, air, growAmt);
        }
    }

    public static void drawBox2(BlockPos pos, Color color, double growAmt) {
        AxisAlignedBB bb = new AxisAlignedBB((double)pos.func_177958_n() - RenderUtil.mc.func_175598_ae().viewerPosX, (double)pos.func_177956_o() - RenderUtil.mc.func_175598_ae().viewerPosY, (double)pos.func_177952_p() - RenderUtil.mc.func_175598_ae().viewerPosZ, (double)(pos.func_177958_n() + 1) - RenderUtil.mc.func_175598_ae().viewerPosX, (double)(pos.func_177956_o() + 1) - RenderUtil.mc.func_175598_ae().viewerPosY, (double)(pos.func_177952_p() + 1) - RenderUtil.mc.func_175598_ae().viewerPosZ).func_186662_g(-growAmt);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.func_175606_aa()).posX, RenderUtil.mc.func_175606_aa().posY, RenderUtil.mc.func_175606_aa().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.minY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.minZ + RenderUtil.mc.func_175598_ae().viewerPosZ, bb.maxX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.maxY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.maxZ + RenderUtil.mc.func_175598_ae().viewerPosZ))) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            RenderGlobal.func_189696_b((AxisAlignedBB)bb, (float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
        }
    }

    public static void drawBlockOutline2(BlockPos pos, Color color, float linewidth, boolean air, double growAmt) {
        IBlockState iblockstate = RenderUtil.mc.theWorld.func_180495_p(pos);
        if ((air || iblockstate.func_185904_a() != Material.air) && RenderUtil.mc.theWorld.func_175723_af().func_177746_a(pos)) {
            assert (RenderUtil.mc.field_175622_Z != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.field_175622_Z, mc.func_184121_ak());
            RenderUtil.drawBlockOutline(iblockstate.func_185918_c((World)RenderUtil.mc.theWorld, pos).func_186662_g(-growAmt).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), color, linewidth);
        }
    }

    public static void drawCrossESP(BlockPos pos, Color color, float lineWidth, boolean air) {
        RenderUtil.drawBlockCrossedESP(pos, new Color(color.getRed(), color.getGreen(), color.getBlue()), lineWidth, air);
    }

    public static void drawBlockCrossedESP(BlockPos pos, Color color, float linewidth, boolean air) {
        IBlockState iblockstate = RenderUtil.mc.theWorld.func_180495_p(pos);
        if ((air || iblockstate.func_185904_a() != Material.air) && RenderUtil.mc.theWorld.func_175723_af().func_177746_a(pos)) {
            assert (RenderUtil.mc.field_175622_Z != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.field_175622_Z, mc.func_184121_ak());
            RenderUtil.drawBlockCrossed(iblockstate.func_185918_c((World)RenderUtil.mc.theWorld, pos).func_186662_g((double)0.002f).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), color, linewidth);
        }
    }

    public static void drawBlockCrossed(AxisAlignedBB bb, Color color, float linewidth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void drawFlucESP(BlockPos pos, Color color, float lineWidth, boolean air) {
        RenderUtil.drawBlockFlucESP(pos, new Color(color.getRed(), color.getGreen(), color.getBlue()), lineWidth, air);
    }

    public static void drawBlockFlucESP(BlockPos pos, Color color, float linewidth, boolean air) {
        IBlockState iblockstate = RenderUtil.mc.theWorld.func_180495_p(pos);
        if ((air || iblockstate.func_185904_a() != Material.air) && RenderUtil.mc.theWorld.func_175723_af().func_177746_a(pos)) {
            assert (RenderUtil.mc.field_175622_Z != null);
            Vec3d interp = EntityUtil.interpolateEntity(RenderUtil.mc.field_175622_Z, mc.func_184121_ak());
            RenderUtil.drawBlockFluctuate(iblockstate.func_185918_c((World)RenderUtil.mc.theWorld, pos).func_186662_g((double)0.002f).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), color, linewidth);
        }
    }

    public static void drawBlockFluctuate(AxisAlignedBB bb, Color color, float linewidth) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)linewidth);
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void drawBBBox(AxisAlignedBB BB, Color Color2, int alpha) {
        AxisAlignedBB bb = new AxisAlignedBB(BB.minX - RenderUtil.mc.func_175598_ae().viewerPosX, BB.minY - RenderUtil.mc.func_175598_ae().viewerPosY, BB.minZ - RenderUtil.mc.func_175598_ae().viewerPosZ, BB.maxX - RenderUtil.mc.func_175598_ae().viewerPosX, BB.maxY - RenderUtil.mc.func_175598_ae().viewerPosY, BB.maxZ - RenderUtil.mc.func_175598_ae().viewerPosZ);
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.func_175606_aa()).posX, RenderUtil.mc.func_175606_aa().posY, RenderUtil.mc.func_175606_aa().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(bb.minX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.minY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.minZ + RenderUtil.mc.func_175598_ae().viewerPosZ, bb.maxX + RenderUtil.mc.func_175598_ae().viewerPosX, bb.maxY + RenderUtil.mc.func_175598_ae().viewerPosY, bb.maxZ + RenderUtil.mc.func_175598_ae().viewerPosZ))) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            RenderGlobal.func_189696_b((AxisAlignedBB)bb, (float)((float)Color2.getRed() / 255.0f), (float)((float)Color2.getGreen() / 255.0f), (float)((float)Color2.getBlue() / 255.0f), (float)((float)alpha / 255.0f));
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
        }
    }

    public static void drawBlockOutlineBB(AxisAlignedBB bb, Color color, float linewidth) {
        Vec3d interp = RenderUtil.interpolateEntity((Entity)RenderUtil.mc.thePlayer, mc.func_184121_ak());
        RenderUtil.drawBlockOutline(bb.func_186662_g((double)0.002f).offset(-interp.xCoord, -interp.yCoord, -interp.zCoord), color, linewidth);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    public static void drawRectCol(float x, float y, float width, float height, Color color) {
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        GL11.glVertex2f((float)(x + width), (float)y);
        GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawBorder(float x, float y, float width, float height, Color color) {
        RenderUtil.drawRectCol(x - 1.0f, y - 1.0f, 1.0f, height + 2.0f, color);
        RenderUtil.drawRectCol(x + width, y - 1.0f, 1.0f, height + 2.0f, color);
        RenderUtil.drawRectCol(x, y - 1.0f, width, 1.0f, color);
        RenderUtil.drawRectCol(x, y + height, width, 1.0f, color);
    }

    public static void drawGlowBox(BlockPos blockPos, double height, Color color) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB((double)blockPos.func_177958_n() - RenderUtil.mc.func_175598_ae().viewerPosX, (double)blockPos.func_177956_o() - RenderUtil.mc.func_175598_ae().viewerPosY, (double)blockPos.func_177952_p() - RenderUtil.mc.func_175598_ae().viewerPosZ, (double)(blockPos.func_177958_n() + 1) - RenderUtil.mc.func_175598_ae().viewerPosX, (double)(blockPos.func_177956_o() + 1) - RenderUtil.mc.func_175598_ae().viewerPosY, (double)(blockPos.func_177952_p() + 1) - RenderUtil.mc.func_175598_ae().viewerPosZ);
        RenderUtil.glSetup();
        RenderUtil.glPrepare();
        RenderUtil.drawSelectionGlowFilledBox(axisAlignedBB, height, color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0));
        RenderUtil.glRestore();
        RenderUtil.glRelease();
    }

    public static void drawSelectionGlowFilledBox(AxisAlignedBB axisAlignedBB, double height, Color startColor, Color endColor) {
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        RenderUtil.addChainedGlowBoxVertices(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY + height, axisAlignedBB.maxZ, startColor, endColor);
        tessellator.draw();
    }

    public static void addChainedGlowBoxVertices(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Color startColor, Color endColor) {
        bufferbuilder.func_181662_b(minX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(maxX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, minY, minZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, minY, maxZ).func_181666_a((float)startColor.getRed() / 255.0f, (float)startColor.getGreen() / 255.0f, (float)startColor.getBlue() / 255.0f, (float)startColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, maxZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
        bufferbuilder.func_181662_b(minX, maxY, minZ).func_181666_a((float)endColor.getRed() / 255.0f, (float)endColor.getGreen() / 255.0f, (float)endColor.getBlue() / 255.0f, (float)endColor.getAlpha() / 255.0f).func_181675_d();
    }

    public static void glSetup() {
        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a((boolean)false);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)1.5f);
    }

    public static void glRelease() {
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179126_j();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static void glPrepare() {
        GlStateManager.func_179129_p();
        GlStateManager.func_179118_c();
        GlStateManager.func_179103_j((int)7425);
    }

    public static void glRestore() {
        GlStateManager.func_179089_o();
        GlStateManager.func_179141_d();
        GlStateManager.func_179103_j((int)7424);
    }

    public static void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glLineWidth((float)1.5f);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)opacity);
        GlStateManager.func_179140_f();
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)posx, (double)posy, (double)posz);
        GL11.glVertex3d((double)posx2, (double)posy2, (double)posz2);
        GL11.glVertex3d((double)posx2, (double)posy2, (double)posz2);
        GL11.glVertex3d((double)posx2, (double)(posy2 + up), (double)posz2);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glColor3d((double)1.0, (double)1.0, (double)1.0);
        GlStateManager.func_179145_e();
    }

    public static void drawCheckmark(float x, float y, Color color) {
        GL11.glPushMatrix();
        GL11.glEnable((int)2848);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glColor4f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getAlpha());
        GL11.glLineWidth((float)2.0f);
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)(x + 1.0f), (double)(y + 1.0f));
        GL11.glVertex2d((double)(x + 3.0f), (double)(y + 4.0f));
        GL11.glEnd();
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)(x + 3.0f), (double)(y + 4.0f));
        GL11.glVertex2d((double)(x + 6.0f), (double)(y - 2.0f));
        GL11.glEnd();
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2848);
        GL11.glPopMatrix();
    }

    public static void startRender() {
        GL11.glPushAttrib((int)1048575);
        GL11.glPushMatrix();
        GL11.glDisable((int)3008);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4353);
        GL11.glDisable((int)2896);
    }

    public static void endRender() {
        GL11.glEnable((int)2896);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glEnable((int)3008);
        GL11.glDepthMask((boolean)true);
        GL11.glCullFace((int)1029);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static Entity getEntity() {
        return mc.func_175606_aa() == null ? RenderUtil.mc.thePlayer : mc.func_175606_aa();
    }

    public static void color(Color color) {
        GL11.glColor4f((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
    }

    public static void color(int color) {
        float[] color4f = ColorUtil.toArray(color);
        GL11.glColor4f((float)color4f[0], (float)color4f[1], (float)color4f[2], (float)color4f[3]);
    }

    public static void color(float r, float g, float b, float a) {
        GL11.glColor4f((float)r, (float)g, (float)b, (float)a);
    }

    public static void drawOutline(AxisAlignedBB bb, float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glLineWidth((float)lineWidth);
        RenderUtil.fillOutline(bb);
        GL11.glLineWidth((float)1.0f);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB bb) {
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDisable((int)2896);
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        RenderUtil.fillBox(bb);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2896);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color) {
        RenderUtil.drawNametag(text, interpolated, scale, color, true);
    }

    public static void drawNametag(String text, AxisAlignedBB interpolated, double scale, int color, boolean rectangle) {
        double x = (interpolated.minX + interpolated.maxX) / 2.0;
        double y = (interpolated.minY + interpolated.maxY) / 2.0;
        double z = (interpolated.minZ + interpolated.maxZ) / 2.0;
        RenderUtil.drawNametag(text, x, y, z, scale, color, rectangle);
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color) {
        RenderUtil.drawNametag(text, x, y, z, scale, color, true);
    }

    public static void drawNametag(String text, double x, double y, double z, double scale, int color, boolean rectangle) {
        double dist = RenderUtil.getEntity().getDistance(x + RenderUtil.mc.func_175598_ae().viewerPosX, y + RenderUtil.mc.func_175598_ae().viewerPosY, z + RenderUtil.mc.func_175598_ae().viewerPosZ);
        int textWidth = Cascade.textManager.getStringWidth(text) / 2;
        double scaling = 0.0018 + scale * dist;
        if (dist <= 8.0) {
            scaling = 0.0245;
        }
        GlStateManager.func_179094_E();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.func_179088_q();
        GlStateManager.func_179136_a((float)1.0f, (float)-1500000.0f);
        GlStateManager.func_179140_f();
        GlStateManager.func_179137_b((double)x, (double)(y + (double)0.4f), (double)z);
        GlStateManager.func_179114_b((float)(-RenderUtil.mc.func_175598_ae().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        float xRot = RenderUtil.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f;
        GlStateManager.func_179114_b((float)RenderUtil.mc.func_175598_ae().playerViewX, (float)xRot, (float)0.0f, (float)0.0f);
        GlStateManager.func_179139_a((double)(-scaling), (double)(-scaling), (double)scaling);
        GlStateManager.func_179097_i();
        if (rectangle) {
            GlStateManager.func_179147_l();
            RenderUtil.prepare(-textWidth - 1, -Cascade.textManager.getFontHeight(), textWidth + 2, 1.0f, 1.8f, 0x55000400, 0x33000000);
            GlStateManager.func_179084_k();
        }
        GlStateManager.func_179147_l();
        Cascade.textManager.drawStringWithShadow(text, -textWidth, -(RenderUtil.mc.fontRendererObj.FONT_HEIGHT - 1), color);
        GlStateManager.func_179084_k();
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
        GlStateManager.func_179113_r();
        GlStateManager.func_179136_a((float)1.0f, (float)1500000.0f);
        GlStateManager.func_179121_F();
    }

    public static void fillBox(AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.maxY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
            GL11.glBegin((int)7);
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.minZ));
            GL11.glVertex3d((double)((float)boundingBox.minX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glVertex3d((double)((float)boundingBox.maxX), (double)((float)boundingBox.minY), (double)((float)boundingBox.maxZ));
            GL11.glEnd();
        }
    }

    public static void fillOutline(AxisAlignedBB bb) {
        if (bb != null) {
            GL11.glBegin((int)1);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.minY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.minZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.maxX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.maxZ);
            GL11.glVertex3d((double)bb.minX, (double)bb.maxY, (double)bb.minZ);
            GL11.glEnd();
        }
    }

    public static void prepare(float x, float y, float x1, float y1, float lineWidth, int color, int color1) {
        RenderUtil.startRender();
        RenderUtil.prepare(x, y, x1, y1, color);
        RenderUtil.color(color1);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glLineWidth((float)lineWidth);
        GL11.glBegin((int)3);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        RenderUtil.endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color, int color1) {
        RenderUtil.startRender();
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        RenderUtil.color(color);
        GL11.glVertex2f((float)x, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        RenderUtil.color(color1);
        GL11.glVertex2f((float)x1, (float)y);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        RenderUtil.endRender();
    }

    public static void prepare(float x, float y, float x1, float y1, int color) {
        RenderUtil.startRender();
        RenderUtil.color(color);
        RenderUtil.scissor(x, y, x1, y1);
        RenderUtil.endRender();
    }

    public static void scissor(float x, float y, float x1, float y1) {
        ScaledResolution res = new ScaledResolution(mc);
        int scale = res.getScaleFactor();
        GL11.glScissor((int)((int)(x * (float)scale)), (int)((int)(((float)res.getScaledHeight() - y1) * (float)scale)), (int)((int)((x1 - x) * (float)scale)), (int)((int)((y1 - y) * (float)scale)));
    }

    public static void drawHoleESP(BlockPos pos, boolean box, boolean outline, boolean cross, Color boxColor, Color outlineColor, Color crossColor, float lineWidth) {
        if (box) {
            RenderUtil.drawBox(pos, new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), boxColor.getAlpha()));
        }
        if (outline) {
            RenderUtil.drawBlockOutline(pos, new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha()), lineWidth, true);
        }
        if (cross) {
            RenderUtil.drawCrossESP(pos, crossColor, lineWidth, true);
        }
    }

    static {
        tessellator2 = Tessellator.func_178181_a();
        builder = tessellator2.func_178180_c();
    }

    public static class RenderTesselator
    extends Tessellator {
        public static RenderTesselator INSTANCE = new RenderTesselator();

        public RenderTesselator() {
            super(0x200000);
        }

        public static void prepare(int mode) {
            RenderTesselator.prepareGL();
            RenderTesselator.begin(mode);
        }

        public static void prepareGL() {
            GL11.glBlendFunc((int)770, (int)771);
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GlStateManager.func_187441_d((float)1.5f);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179140_f();
            GlStateManager.func_179129_p();
            GlStateManager.func_179141_d();
            GlStateManager.func_179124_c((float)1.0f, (float)1.0f, (float)1.0f);
        }

        public static void begin(int mode) {
            INSTANCE.func_178180_c().func_181668_a(mode, DefaultVertexFormats.field_181706_f);
        }

        public static void release() {
            RenderTesselator.render();
            RenderTesselator.releaseGL();
        }

        public static void render() {
            INSTANCE.draw();
        }

        public static void releaseGL() {
            GlStateManager.func_179089_o();
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179098_w();
            GlStateManager.func_179147_l();
            GlStateManager.func_179126_j();
        }

        public static void drawBox(BlockPos blockPos, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawBox(blockPos, r, g, b, a, sides);
        }

        public static void drawBox(float x, float y, float z, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawBox(INSTANCE.func_178180_c(), x, y, z, 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
        }

        public static void drawBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
            RenderTesselator.drawBox(INSTANCE.func_178180_c(), blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0f, 1.0f, 1.0f, r, g, b, a, sides);
        }

        public static BufferBuilder getBufferBuilder() {
            return INSTANCE.func_178180_c();
        }

        public static void drawBox(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
            if ((sides & 1) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 2) != 0) {
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 4) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 8) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x10) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x20) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
        }

        public static void drawLines(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, int sides) {
            if ((sides & 0x11) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x12) != 0) {
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x21) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x22) != 0) {
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 5) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 6) != 0) {
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 9) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0xA) != 0) {
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x14) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x24) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)z).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)z).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x18) != 0) {
                buffer.func_181662_b((double)x, (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)x, (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
            if ((sides & 0x28) != 0) {
                buffer.func_181662_b((double)(x + w), (double)y, (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
                buffer.func_181662_b((double)(x + w), (double)(y + h), (double)(z + d)).func_181669_b(r, g, b, a).func_181675_d();
            }
        }

        public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179120_a((int)770, (int)771, (int)0, (int)1);
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            GL11.glLineWidth((float)width);
            Tessellator tessellator = Tessellator.func_178181_a();
            BufferBuilder bufferbuilder = tessellator.func_178180_c();
            bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
            bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.maxZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.minY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.maxX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            bufferbuilder.func_181662_b(bb.minX, bb.maxY, bb.minZ).func_181666_a(red, green, blue, alpha).func_181675_d();
            tessellator.draw();
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
            GlStateManager.func_179121_F();
        }

        public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int argb, int alpha2) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawFullBox(bb, blockPos, width, r, g, b, a, alpha2);
        }

        public static void drawFullBox(AxisAlignedBB bb, BlockPos blockPos, float width, int red, int green, int blue, int alpha, int alpha2) {
            RenderTesselator.prepare(7);
            RenderTesselator.drawBox(blockPos, red, green, blue, alpha, 63);
            RenderTesselator.release();
            RenderTesselator.drawBoundingBox(bb, width, red, green, blue, alpha2);
        }

        public static void drawHalfBox(BlockPos blockPos, int argb, int sides) {
            int a = argb >>> 24 & 0xFF;
            int r = argb >>> 16 & 0xFF;
            int g = argb >>> 8 & 0xFF;
            int b = argb & 0xFF;
            RenderTesselator.drawHalfBox(blockPos, r, g, b, a, sides);
        }

        public static void drawHalfBox(BlockPos blockPos, int r, int g, int b, int a, int sides) {
            RenderTesselator.drawBox(INSTANCE.func_178180_c(), blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0f, 0.5f, 1.0f, r, g, b, a, sides);
        }
    }

    public static final class GeometryMasks {
        public static final HashMap FACEMAP = new HashMap();

        static {
            FACEMAP.put(EnumFacing.DOWN, 1);
            FACEMAP.put(EnumFacing.WEST, 16);
            FACEMAP.put(EnumFacing.NORTH, 4);
            FACEMAP.put(EnumFacing.SOUTH, 8);
            FACEMAP.put(EnumFacing.EAST, 32);
            FACEMAP.put(EnumFacing.UP, 2);
        }

        public static final class Line {
            public static final int DOWN_WEST = 17;
            public static final int UP_WEST = 18;
            public static final int DOWN_EAST = 33;
            public static final int UP_EAST = 34;
            public static final int DOWN_NORTH = 5;
            public static final int UP_NORTH = 6;
            public static final int DOWN_SOUTH = 9;
            public static final int UP_SOUTH = 10;
            public static final int NORTH_WEST = 20;
            public static final int NORTH_EAST = 36;
            public static final int SOUTH_WEST = 24;
            public static final int SOUTH_EAST = 40;
            public static final int ALL = 63;
        }

        public static final class Quad {
            public static final int DOWN = 1;
            public static final int UP = 2;
            public static final int NORTH = 4;
            public static final int SOUTH = 8;
            public static final int WEST = 16;
            public static final int EAST = 32;
            public static final int ALL = 63;
        }
    }
}


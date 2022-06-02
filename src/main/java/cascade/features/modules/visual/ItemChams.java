/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.Display
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.event.events.RenderItemInFirstPersonEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public class ItemChams
extends Module {
    Setting<Page> page = this.register(new Setting<Page>("Page", Page.Glint));
    public Setting<Boolean> glint = this.register(new Setting<Boolean>("ModifyGlint", false));
    public Setting<Float> scale = this.register(new Setting<Float>("GlintScale", Float.valueOf(8.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    public Setting<Float> glintMult = this.register(new Setting<Float>("GlintMultiplier", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Float> glintRotate = this.register(new Setting<Float>("GlintRotate", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Color> glintColor = this.register(new Setting<Color>("GlintColor", Color.RED));
    public Setting<Boolean> chams = this.register(new Setting<Boolean>("Chams", false));
    public Setting<Boolean> blur = this.register(new Setting<Boolean>("Blur", false));
    public Setting<Float> radius = this.register(new Setting<Float>("Radius", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(10.0f)));
    public Setting<Float> mix = this.register(new Setting<Float>("Mix", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    public Setting<Boolean> useImage = this.register(new Setting<Boolean>("UseImage", false));
    public Setting<Boolean> useGif = this.register(new Setting<Boolean>("UseGif", false));
    public Setting<Float> imageMix = this.register(new Setting<Float>("ImageMix", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(1.0f)));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public Setting<Color> chamColor = this.register(new Setting<Color>("Color", Color.RED));
    boolean forceRender = false;
    private static ItemChams INSTANCE;

    public ItemChams() {
        super("ItemChams", Module.Category.VISUAL, "");
        INSTANCE = this;
    }

    public static ItemChams getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemChams();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void invoke(RenderItemInFirstPersonEvent event) {
        if (event.getStage() == 0 && this.isEnabled() && !this.forceRender && this.chams.getValue().booleanValue()) {
            event.setCanceled(true);
        }
    }

    private void render(RenderItemInFirstPersonEvent event) {
        mc.func_175597_ag().func_187462_a(event.getEntity(), event.getStack(), event.getTransformType(), event.isLeftHanded());
    }

    @SubscribeEvent
    public void invoke(Render3DEvent event) {
        if ((Display.isActive() || Display.isVisible()) && this.chams.getValue().booleanValue()) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179123_a();
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GlStateManager.func_179126_j();
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179141_d();
            this.forceRender = true;
            this.forceRender = false;
            GlStateManager.func_179084_k();
            GlStateManager.func_179118_c();
            GlStateManager.func_179097_i();
            GlStateManager.func_179099_b();
            GlStateManager.func_179121_F();
        }
    }

    public static enum Page {
        Glint,
        Chams;

    }
}


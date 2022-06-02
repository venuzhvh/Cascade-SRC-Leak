/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraftforge.fml.client.config.GuiUtils
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.core;

import cascade.Cascade;
import cascade.event.events.ClientEvent;
import cascade.event.events.Render2DEvent;
import cascade.features.gui.CascadeGui;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui
extends Module {
    public Setting<Boolean> descriptions = this.register(new Setting<Boolean>("Descriptions", true));
    Setting<Boolean> gradient = this.register(new Setting<Boolean>("Gradient", false));
    Setting<Color> cTop = this.register(new Setting<Object>("Top", new Color(-1), v -> this.gradient.getValue()));
    Setting<Color> cBottom = this.register(new Setting<Object>("Bottom", new Color(0), v -> this.gradient.getValue()));
    public Setting<ModuleSuffix> moduleSuffix = this.register(new Setting<ModuleSuffix>("ModuleSuffix", ModuleSuffix.None));
    public Setting<Integer> factor = this.register(new Setting<Object>("Factor", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(7), v -> this.moduleSuffix.getValue() == ModuleSuffix.New));
    public Setting<String> closed = this.register(new Setting<Object>("Closed", "+", v -> this.moduleSuffix.getValue() == ModuleSuffix.CustomText));
    public Setting<String> opened = this.register(new Setting<Object>("Opened", "-", v -> this.moduleSuffix.getValue() == ModuleSuffix.CustomText));
    public Setting<Boolean> enumOnly = this.register(new Setting<Object>("EnumOnly", Boolean.valueOf(false), v -> this.moduleSuffix.getValue() != ModuleSuffix.None));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-8912641)));
    public Setting<Color> topc = this.register(new Setting<Color>("TopColor", new Color(-8912641)));
    public Setting<Boolean> moduleOutline = this.register(new Setting<Boolean>("ModuleOutline", true));
    public Setting<Color> cOutline = this.register(new Setting<Object>("OutlineColor", new Color(0), v -> this.moduleOutline.getValue()));
    public Setting<Color> background = this.register(new Setting<Color>("BackgroundColor", new Color(-2046688766)));
    public Setting<Color> textColor = this.register(new Setting<Color>("TextColor", new Color(-1)));
    public Setting<Integer> hoverA = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HUD Mode", (Object)rainbowMode.Static, v -> this.rainbow.getValue()));
    public Setting<rainbowModeArray> rainbowModeA = this.register(new Setting<Object>("ArrayListMode", (Object)rainbowModeArray.Static, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowSaturation = this.register(new Setting<Object>("Saturation", Integer.valueOf(150), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue()));
    private static ClickGui INSTANCE = new ClickGui();

    public ClickGui() {
        super("ClickGui", Module.Category.CORE, "Client's Click GUI");
        this.setInstance();
        this.setBind(208);
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            Cascade.colorManager.setColor(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha());
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        ScaledResolution resolution = new ScaledResolution(mc);
        if (ClickGui.mc.currentScreen instanceof CascadeGui && this.gradient.getValue().booleanValue()) {
            this.drawGradient(0.0, 0.0, resolution.getScaledWidth(), resolution.getScaledHeight(), new Color(0, 0, 0, 0).getRGB(), this.cTop.getValue().getRGB());
            this.drawGradient(0.0, 0.0, resolution.getScaledWidth(), resolution.getScaledHeight(), this.cBottom.getValue().getRGB(), new Color(0, 0, 0, 0).getRGB());
        }
    }

    void drawGradient(double left, double top, double right, double bottom, int startColor, int endColor) {
        GuiUtils.drawGradientRect((int)0, (int)((int)left), (int)((int)top), (int)((int)right), (int)((int)bottom), (int)startColor, (int)endColor);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen((GuiScreen)CascadeGui.getClickGui());
    }

    @Override
    public void onDisable() {
        if (ClickGui.mc.thePlayer != null) {
            ClickGui.mc.thePlayer.closeScreen();
        }
    }

    @Override
    public void onLoad() {
        Cascade.colorManager.setColor(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), this.c.getValue().getAlpha());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof CascadeGui)) {
            this.disable();
        }
    }

    public static enum rainbowModeArray {
        Static,
        Up;

    }

    public static enum rainbowMode {
        Static,
        Sideway;

    }

    public static enum ModuleSuffix {
        Gears,
        CustomText,
        New,
        None;

    }
}


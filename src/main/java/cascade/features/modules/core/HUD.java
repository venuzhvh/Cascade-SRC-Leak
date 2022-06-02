/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 */
package cascade.features.modules.core;

import cascade.Cascade;
import cascade.event.events.Render2DEvent;
import cascade.features.Feature;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.core.TextUtil;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.render.ColorUtil;
import cascade.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class HUD
extends Module {
    private static HUD INSTANCE = new HUD();
    Setting<Boolean> renderingUp = this.register(new Setting<Boolean>("RenderingUp", false));
    Setting<Boolean> coords = this.register(new Setting<Boolean>("Coords", false));
    Setting<Boolean> simpleCoords = this.register(new Setting<Object>("SimpleCoords", Boolean.valueOf(false), v -> this.coords.getValue()));
    Setting<Boolean> direction = this.register(new Setting<Boolean>("Direction", false));
    Setting<Boolean> totems = this.register(new Setting<Boolean>("Totems", false));
    Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", false));
    Setting<Color> armorColorFrom = this.register(new Setting<Object>("ArmorColorFrom", new Color(-8912641), v -> this.armor.getValue()));
    Setting<Color> armorColorTo = this.register(new Setting<Object>("ArmorColorTo", new Color(-8912641), v -> this.armor.getValue()));
    Setting<Boolean> potions = this.register(new Setting<Boolean>("Potions", false));
    Setting<Boolean> watermark = this.register(new Setting<Boolean>("Watermark", false));
    Setting<String> watermarkText = this.register(new Setting<Object>("WatermarkText", "Cascade 0.2.0", v -> this.watermark.getValue()));
    Setting<Integer> watermarkY = this.register(new Setting<Object>("WatermarkY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(20), v -> this.watermark.getValue()));
    Setting<Boolean> arrayList = this.register(new Setting<Boolean>("ArrayList", true));
    Setting<Integer> arrayListY = this.register(new Setting<Object>("ArrayListY", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(50), v -> this.arrayList.getValue()));
    public Setting<Ordering> ordering = this.register(new Setting<Object>("Ordering", (Object)Ordering.Length, v -> this.arrayList.getValue()));
    Setting<Boolean> pvpInfo = this.register(new Setting<Boolean>("PvpInfo", false));
    Setting<String> pvpText = this.register(new Setting<Object>("PvpText", "Cascade", v -> this.pvpInfo.getValue()));
    Setting<Welcomer> welcomer = this.register(new Setting<Welcomer>("Welcomer", Welcomer.None));
    Setting<String> welcomerText = this.register(new Setting<Object>("WelcomerText", "UID:-1", v -> this.welcomer.getValue() == Welcomer.Custom));
    Setting<Boolean> ping = this.register(new Setting<Boolean>("Ping", false));
    Setting<Boolean> tps = this.register(new Setting<Boolean>("TPS", false));
    Setting<Boolean> fps = this.register(new Setting<Boolean>("FPS", false));
    Setting<Boolean> time = this.register(new Setting<Boolean>("Time", false));
    Setting<Boolean> speed = this.register(new Setting<Boolean>("Speed", false));
    Setting<Integer> speedTicks = this.register(new Setting<Object>("Ticks", Integer.valueOf(20), Integer.valueOf(5), Integer.valueOf(100), v -> this.speed.getValue()));
    Setting<TextUtil.Color> infoColor = this.register(new Setting<Object>("InfoColor", (Object)TextUtil.Color.GRAY, v -> this.ping.getValue() != false || this.tps.getValue() != false || this.fps.getValue() != false || this.time.getValue() != false || this.speed.getValue() != false));
    Setting<Boolean> lagNotify = this.register(new Setting<Boolean>("LagNotify", false));
    public Setting<Integer> lagTime = this.register(new Setting<Object>("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(5000), v -> this.lagNotify.getValue()));
    static ItemStack totem = new ItemStack(Items.field_190929_cY);
    ArrayDeque<Double> speedDeque = new ArrayDeque();
    int color;

    public HUD() {
        super("HUD", Module.Category.CORE, "Clients HUD");
        this.setInstance();
    }

    public static HUD getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HUD();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        String str1;
        String fpsText;
        String str;
        ArrayList effects;
        int i;
        int j;
        if (Feature.fullNullCheck()) {
            return;
        }
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().c.getValue().getRed(), ClickGui.getInstance().c.getValue().getGreen(), ClickGui.getInstance().c.getValue().getBlue());
        if (this.pvpInfo.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(this.pvpText.getValue(), 2.0f, 250.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = this.pvpText.getValue().toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, 250.0f, ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(this.pvpText.getValue(), 2.0f, 250.0f, this.color, true);
            }
            int totems = HUD.mc.thePlayer.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum() + (InventoryUtil.heldItem(Items.field_190929_cY, InventoryUtil.Hand.Off) ? 1 : 0);
            this.renderer.drawString(totems == 0 ? ChatFormatting.RED + "" + totems : ChatFormatting.GREEN + "" + totems, 2.0f, 260.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            int ping = Cascade.serverManager.getPing();
            String pingString = null;
            if (ping <= 50) {
                pingString = ChatFormatting.GREEN + "" + ping;
            }
            if (ping > 50 && ping <= 100) {
                pingString = ChatFormatting.YELLOW + "" + ping;
            }
            if (ping > 100) {
                pingString = ChatFormatting.RED + "" + ping;
            }
            this.renderer.drawString(pingString, 2.0f, 270.0f, this.color, true);
        }
        if (this.watermark.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(this.watermarkText.getValue(), 2.0f, this.watermarkY.getValue().intValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = new int[]{1};
                    char[] stringToCharArray = this.watermarkText.getValue().toCharArray();
                    float f = 0.0f;
                    for (char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.watermarkY.getValue().intValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += (float)this.renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                this.renderer.drawString(this.watermarkText.getValue(), 2.0f, this.watermarkY.getValue().intValue(), this.color, true);
            }
        }
        int[] counter1 = new int[]{1};
        int n = j = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() == false ? 14 : 0;
        if (this.arrayList.getValue().booleanValue()) {
            Module module;
            String str2;
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.ordering.getValue() == Ordering.Alphabet) {
                    for (int k = 0; k < Cascade.moduleManager.sortedModulesABC.size(); ++k) {
                        str2 = Cascade.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), 2 + j * 10 + this.arrayListY.getValue(), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < Cascade.moduleManager.sortedModules.size(); ++k) {
                        module = Cascade.moduleManager.sortedModules.get(k);
                        String str3 = module.getName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                        this.renderer.drawString(str3, width - 2 - this.renderer.getStringWidth(str3), 2 + j * 10 + this.arrayListY.getValue(), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.ordering.getValue() == Ordering.Alphabet) {
                for (int k = 0; k < Cascade.moduleManager.sortedModulesABC.size(); ++k) {
                    str2 = Cascade.moduleManager.sortedModulesABC.get(k);
                    this.renderer.drawString(str2, width - 2 - this.renderer.getStringWidth(str2), height - (j += 10) + this.arrayListY.getValue(), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < Cascade.moduleManager.sortedModules.size(); ++k) {
                    module = Cascade.moduleManager.sortedModules.get(k);
                    String str4 = module.getName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    this.renderer.drawString(str4, width - 2 - this.renderer.getStringWidth(str4), height - (j += 10) + this.arrayListY.getValue(), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        int n2 = HUD.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue() != false ? 13 : (i = this.renderingUp.getValue() != false ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(HUD.mc.thePlayer.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str5 = Cascade.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str5, width - this.renderer.getStringWidth(str5) - 2, height - 2 - (i += 10), potionEffect.func_188419_a().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                double speed;
                double displaySpeed = speed = this.calcSpeed(HUD.mc.thePlayer);
                if (speed > 0.0 || HUD.mc.thePlayer.ticksExisted % 4 == 0) {
                    this.speedDeque.add(speed);
                } else {
                    this.speedDeque.pollFirst();
                }
                while (!this.speedDeque.isEmpty() && this.speedDeque.size() > this.speedTicks.getValue()) {
                    this.speedDeque.poll();
                }
                displaySpeed = this.average(this.speedDeque);
                str = "Speed " + TextUtil.coloredString(String.format("%.1f", displaySpeed) + " km/h", this.infoColor.getValue());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str6 = "Time " + TextUtil.coloredString(String.format(new SimpleDateFormat("h:mm a").format(new Date()), new Object[0]), this.infoColor.getValue());
                this.renderer.drawString(str6, width - this.renderer.getStringWidth(str6) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str7 = "TPS " + TextUtil.coloredString(String.format(Cascade.serverManager.getTPS() + "", new Object[0]), this.infoColor.getValue());
                this.renderer.drawString(str7, width - this.renderer.getStringWidth(str7) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = "FPS " + TextUtil.coloredString(String.format(Minecraft.debugFPS + "", new Object[0]), this.infoColor.getValue());
            str1 = "Ping " + TextUtil.coloredString(String.format(Cascade.serverManager.getPing() + "", new Object[0]), this.infoColor.getValue());
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, height - 2 - (i += 10), ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                effects = new ArrayList(HUD.mc.thePlayer.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str8 = Cascade.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str8, width - this.renderer.getStringWidth(str8) - 2, 2 + i++ * 10, potionEffect.func_188419_a().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                double speed;
                double displaySpeed = speed = this.calcSpeed(HUD.mc.thePlayer);
                if (speed > 0.0 || HUD.mc.thePlayer.ticksExisted % 4 == 0) {
                    this.speedDeque.add(speed);
                } else {
                    this.speedDeque.pollFirst();
                }
                while (!this.speedDeque.isEmpty() && this.speedDeque.size() > this.speedTicks.getValue()) {
                    this.speedDeque.poll();
                }
                displaySpeed = this.average(this.speedDeque);
                str = "Speed " + TextUtil.coloredString(String.format("%.1f", displaySpeed) + " km/h", this.infoColor.getValue());
                this.renderer.drawString(str, width - this.renderer.getStringWidth(str) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.time.getValue().booleanValue()) {
                String str9 = "Time " + TextUtil.coloredString(String.format(new SimpleDateFormat("h:mm a").format(new Date()), new Object[0]), this.infoColor.getValue());
                this.renderer.drawString(str9, width - this.renderer.getStringWidth(str9) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str10 = "TPS " + TextUtil.coloredString(String.format(Cascade.serverManager.getTPS() + "", new Object[0]), this.infoColor.getValue());
                this.renderer.drawString(str10, width - this.renderer.getStringWidth(str10) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            fpsText = "FPS " + TextUtil.coloredString(String.format(Minecraft.debugFPS + "", new Object[0]), this.infoColor.getValue());
            str1 = "Ping " + TextUtil.coloredString(String.format(Cascade.serverManager.getPing() + "", new Object[0]), this.infoColor.getValue());
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, width - this.renderer.getStringWidth(fpsText) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, width - this.renderer.getStringWidth(str1) - 2, 2 + i++ * 10, ClickGui.getInstance().rainbow.getValue().booleanValue() ? (ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = HUD.mc.theWorld.func_180494_b(HUD.mc.thePlayer.func_180425_c()).func_185359_l() == "Hell";
        int posX = (int)HUD.mc.thePlayer.posX;
        int posY = (int)HUD.mc.thePlayer.posY;
        int posZ = (int)HUD.mc.thePlayer.posZ;
        float nether = !inHell ? 0.125f : 8.0f;
        int hposX = (int)(HUD.mc.thePlayer.posX * (double)nether);
        int hposZ = (int)(HUD.mc.thePlayer.posZ * (double)nether);
        i = HUD.mc.currentScreen instanceof GuiChat ? 14 : 0;
        String coordinates = ChatFormatting.WHITE + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]");
        String direction = this.direction.getValue() != false ? ChatFormatting.WHITE + Cascade.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue() != false ? coordinates : "";
        String simpleCoord = ChatFormatting.WHITE + (inHell ? posX + " " + posY + " " + posZ : posX + " " + posY + " " + posZ);
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            String rainbowCoords;
            String string = this.coords.getValue() != false ? "XYZ " + (inHell ? posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]" : posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (rainbowCoords = "");
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, height - i - 11, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(this.simpleCoords.getValue() != false ? simpleCoord : rainbowCoords, 2.0f, height - i, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter2 = new int[]{1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + s, height - i - 11, ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = new int[]{1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0f + u, height - i, ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0f, height - i - 11, this.color, true);
            this.renderer.drawString(this.simpleCoords.getValue() != false ? simpleCoord : coords, 2.0f, height - i, this.color, true);
        }
        if (this.armor.getValue().booleanValue()) {
            this.renderArmor(true);
        }
        if (this.totems.getValue().booleanValue()) {
            this.renderTotem();
        }
        if (this.welcomer.getValue() != Welcomer.None) {
            this.renderWelcomer();
        }
        if (this.lagNotify.getValue().booleanValue()) {
            this.renderLag();
        }
    }

    void renderArmor(boolean percent) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        GlStateManager.func_179098_w();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (EntityUtil.isInLiquid() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : HUD.mc.thePlayer.inventory.armorInventory) {
            ++iteration;
            if (is.func_190926_b()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.func_180450_b(is, x, y);
            RenderUtil.itemRender.func_180453_a(HUD.mc.fontRendererObj, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            this.renderer.drawStringWithShadow(s, x + 19 - 2 - this.renderer.getStringWidth(s), y + 9, 0xFFFFFF);
            if (!percent) continue;
            float from = ((float)is.getMaxDurability() - (float)is.getCurrentDurability()) / (float)is.getMaxDurability();
            float to = 1.0f - from;
            int dmg = 100 - (int)(to * 100.0f);
            if (from > 1.0f) {
                from = 1.0f;
            } else if (from < 0.0f) {
                from = 0.0f;
            }
            if (to > 1.0f) {
                to = 1.0f;
            }
            if (dmg < 0) {
                dmg = 0;
            }
            this.renderer.drawStringWithShadow(dmg + "", x + 8 - this.renderer.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA((int)((float)this.armorColorFrom.getValue().getRed() * from - (float)this.armorColorTo.getValue().getRed()), (int)((float)this.armorColorFrom.getValue().getGreen() * from - (float)this.armorColorTo.getValue().getGreen()), (int)((float)this.armorColorFrom.getValue().getBlue() * from - (float)this.armorColorTo.getValue().getBlue())));
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179140_f();
    }

    void renderTotem() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = HUD.mc.thePlayer.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (HUD.mc.thePlayer.func_184592_cb().getItem() == Items.field_190929_cY) {
            totems += HUD.mc.thePlayer.func_184592_cb().func_190916_E();
        }
        if (totems > 0) {
            GlStateManager.func_179098_w();
            int i = width / 2;
            int y = height - 55 - (HUD.mc.thePlayer.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.func_180450_b(totem, x, y);
            RenderUtil.itemRender.func_180453_a(HUD.mc.fontRendererObj, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            this.renderer.drawStringWithShadow(totems + "", x + 19 - 2 - this.renderer.getStringWidth(totems + ""), y + 9, 0xFFFFFF);
            GlStateManager.func_179126_j();
            GlStateManager.func_179140_f();
        }
    }

    void renderLag() {
        int width = this.renderer.scaledWidth;
        if (Cascade.serverManager.isServerNotResponding()) {
            String text = ChatFormatting.RED + "Server not responding " + MathUtil.round((float)Cascade.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }

    void renderWelcomer() {
        int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.welcomer.getValue()) {
            case None: {
                text = "";
            }
            case Custom: {
                text = this.welcomerText.getValue();
                break;
            }
            case Calendar: {
                text = MathUtil.getTimeOfDay() + HUD.mc.thePlayer.getDisplayNameString();
            }
        }
        if (ClickGui.getInstance().rainbow.getValue().booleanValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            } else {
                int[] counter1 = new int[]{1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0f;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f + i, 2.0f, ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    i += (float)this.renderer.getStringWidth(String.valueOf(c));
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(text, (float)width / 2.0f - (float)this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
        }
    }

    @Override
    public void onDisable() {
        this.speedDeque.clear();
    }

    private double calcSpeed(EntityPlayerSP player) {
        double tps = 1000.0 / (double)HUD.mc.timer.field_194149_e;
        double x = player.posX - player.prevPosX;
        double z = player.posZ - player.prevPosZ;
        double speed = Math.hypot(x, z) * tps;
        return speed *= 3.6;
    }

    private double average(Collection<Double> collection) {
        if (collection.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        int size = 0;
        for (double element : collection) {
            sum += element;
            ++size;
        }
        return sum / (double)size;
    }

    static enum Welcomer {
        None,
        Custom,
        Calendar;

    }

    public static enum Ordering {
        Length,
        Alphabet;

    }
}


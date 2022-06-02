/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.client.network.NetworkPlayerInfo
 *  net.minecraft.client.renderer.BufferBuilder
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.Tessellator
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.culling.ICamera
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.math.MathHelper
 *  org.lwjgl.opengl.GL11
 */
package cascade.features.modules.visual;

import cascade.Cascade;
import cascade.event.events.Render3DEvent;
import cascade.features.gui.font.CustomFont;
import cascade.features.modules.Module;
import cascade.features.modules.core.FontMod;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.render.ColorUtil;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Nametags
extends Module {
    private final Setting<Boolean> outline;
    private final Setting<Boolean> inside;
    private final Setting<Integer> Ored;
    private final Setting<Integer> Ogreen;
    private final Setting<Integer> Oblue;
    private final Setting<Integer> Oalpha;
    private final Setting<Float> Owidth;
    private final Setting<EnchantMode> enchantMode;
    private final Setting<Boolean> reversed;
    private final Setting<Boolean> reversedHand;
    private final Setting<Boolean> health;
    private final Setting<Boolean> gameMode;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> pingColor;
    private final Setting<Boolean> armor;
    private final Setting<Boolean> durability;
    private final Setting<Boolean> item;
    private final Setting<Boolean> invisibles;
    private final Setting<Float> scale;
    private final Setting<Float> height;
    private final Setting<FriendMode> friendMode;
    private final Setting<Boolean> friends;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, true);
    public Setting<OutLineMode> outlineMode;
    private ICamera camera;
    boolean shownItem;
    private Map<Integer, Boolean> glCapMap = new HashMap<Integer, Boolean>();
    private static Nametags INSTANCE;

    public Nametags() {
        super("Nametags", Module.Category.VISUAL, "Displays info above players");
        this.camera = new Frustum();
        this.outline = this.register(new Setting<Boolean>("Outline", true));
        this.inside = this.register(new Setting<Boolean>("Background", true));
        this.outlineMode = this.register(new Setting<OutLineMode>("Outline Mode", OutLineMode.DEPEND));
        this.Ored = this.register(new Setting<Object>("Outline Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.outlineMode.getValue() == OutLineMode.NORMAL));
        this.Ogreen = this.register(new Setting<Object>("Outline Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.outlineMode.getValue() == OutLineMode.NORMAL));
        this.Oblue = this.register(new Setting<Object>("Outline Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue() != false && this.outlineMode.getValue() == OutLineMode.NORMAL));
        this.Oalpha = this.register(new Setting<Object>("Outline Alpha", Integer.valueOf(155), Integer.valueOf(0), Integer.valueOf(255), v -> this.outline.getValue()));
        this.Owidth = this.register(new Setting<Object>("Outline Width", Float.valueOf(1.5f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.outline.getValue()));
        this.reversed = this.register(new Setting<Boolean>("Reversed", false));
        this.reversedHand = this.register(new Setting<Boolean>("Reversed Hand", false));
        this.enchantMode = this.register(new Setting<EnchantMode>("Enchant Mode", EnchantMode.MAX));
        this.health = this.register(new Setting<Boolean>("Health", true));
        this.gameMode = this.register(new Setting<Boolean>("GameMode", true));
        this.ping = this.register(new Setting<Boolean>("Ping", true));
        this.pingColor = this.register(new Setting<Boolean>("Ping Color", true));
        this.armor = this.register(new Setting<Boolean>("Armor", true));
        this.durability = this.register(new Setting<Boolean>("Durability", true));
        this.item = this.register(new Setting<Boolean>("Item Name", true));
        this.invisibles = this.register(new Setting<Boolean>("Invisibles", false));
        this.scale = this.register(new Setting<Float>("Scale", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(9.0f)));
        this.height = this.register(new Setting<Float>("Height", Float.valueOf(2.5f), Float.valueOf(0.5f), Float.valueOf(5.0f)));
        this.friends = this.register(new Setting<Boolean>("Friends", true));
        this.friendMode = this.register(new Setting<Object>("Friend Mode", (Object)FriendMode.TEXT, v -> this.friends.getValue()));
        this.red = this.register(new Setting<Object>("Friend Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.friends.getValue()));
        this.green = this.register(new Setting<Object>("Friend Green", Integer.valueOf(130), Integer.valueOf(0), Integer.valueOf(255), v -> this.friends.getValue()));
        this.blue = this.register(new Setting<Object>("Friend Blue", Integer.valueOf(130), Integer.valueOf(0), Integer.valueOf(255), v -> this.friends.getValue()));
        INSTANCE = this;
    }

    public static Nametags getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Nametags();
        }
        return INSTANCE;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Nametags.fullNullCheck() || event == null) {
            return;
        }
        EntityPlayerSP entityPlayerSP = mc.func_175606_aa() == null ? Nametags.mc.thePlayer : mc.func_175606_aa();
        double d3 = entityPlayerSP.lastTickPosX + (entityPlayerSP.posX - entityPlayerSP.lastTickPosX) * (double)event.getPartialTicks();
        double d4 = entityPlayerSP.lastTickPosY + (entityPlayerSP.posY - entityPlayerSP.lastTickPosY) * (double)event.getPartialTicks();
        double d5 = entityPlayerSP.lastTickPosZ + (entityPlayerSP.posZ - entityPlayerSP.lastTickPosZ) * (double)event.getPartialTicks();
        this.camera.setPosition(d3, d4, d5);
        ArrayList<Object> players = new ArrayList<Object>(Nametags.mc.theWorld.playerEntities);
        players.sort(Comparator.comparing(entityPlayer -> Float.valueOf(entityPlayerSP.getDistanceToEntity((Entity)entityPlayer))).reversed());
        for (EntityPlayer entityPlayer2 : players) {
            NetworkPlayerInfo npi = Nametags.mc.thePlayer.sendQueue.func_175102_a(entityPlayer2.getGameProfile().getId());
            if (!this.camera.isBoundingBoxInFrustum(entityPlayer2.func_174813_aQ()) && !this.camera.isBoundingBoxInFrustum(entityPlayer2.func_174813_aQ().offset(0.0, 2.0, 0.0)) || entityPlayer2 == mc.func_175606_aa() || !entityPlayer2.isEntityAlive()) continue;
            double pX = entityPlayer2.lastTickPosX + (entityPlayer2.posX - entityPlayer2.lastTickPosX) * (double)Nametags.mc.timer.field_194147_b - Nametags.mc.field_175616_W.renderPosX;
            double pY = entityPlayer2.lastTickPosY + (entityPlayer2.posY - entityPlayer2.lastTickPosY) * (double)Nametags.mc.timer.field_194147_b - Nametags.mc.field_175616_W.renderPosY;
            double pZ = entityPlayer2.lastTickPosZ + (entityPlayer2.posZ - entityPlayer2.lastTickPosZ) * (double)Nametags.mc.timer.field_194147_b - Nametags.mc.field_175616_W.renderPosZ;
            if (npi != null && this.getShortName(npi.func_178848_b().getName()).equalsIgnoreCase("SP") && !this.invisibles.getValue().booleanValue() || entityPlayer2.getCommandSenderName().startsWith("Body #")) continue;
            try {
                this.renderNametag(entityPlayer2, pX, pY, pZ);
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from Nametags");
                ex.printStackTrace();
            }
        }
    }

    public String getShortName(String gameType) {
        if (gameType.equalsIgnoreCase("survival")) {
            return "S";
        }
        if (gameType.equalsIgnoreCase("creative")) {
            return "C";
        }
        if (gameType.equalsIgnoreCase("adventure")) {
            return "A";
        }
        if (gameType.equalsIgnoreCase("spectator")) {
            return "SP";
        }
        return "NONE";
    }

    public String getHealth(float health) {
        if (health > 18.0f) {
            return "a";
        }
        if (health > 16.0f) {
            return "2";
        }
        if (health > 12.0f) {
            return "e";
        }
        if (health > 8.0f) {
            return "6";
        }
        if (health > 5.0f) {
            return "c";
        }
        return "4";
    }

    public String getPing(float ping) {
        if (ping > 200.0f) {
            return "c";
        }
        if (ping > 100.0f) {
            return "e";
        }
        return "a";
    }

    private String getName(EntityPlayer player) {
        return player.getCommandSenderName();
    }

    public void renderNametag(EntityPlayer player, double x, double y, double z) {
        this.shownItem = false;
        GlStateManager.func_179094_E();
        NetworkPlayerInfo npi = Nametags.mc.thePlayer.sendQueue.func_175102_a(player.getGameProfile().getId());
        boolean isFriend = Cascade.friendManager.isFriend(player.getCommandSenderName()) && this.friends.getValue() != false;
        StringBuilder append = new StringBuilder().append(isFriend && this.friendMode.getValue() == FriendMode.TEXT ? "\u00a7" + (isFriend ? "b" : "c") : (player.isSneaking() ? "\u00a77" : "\u00a7r")).append(this.getName(player)).append(this.gameMode.getValue() != false && npi != null ? " [" + this.getShortName(npi.func_178848_b().getName()) + "]" : "").append(this.ping.getValue() != false && npi != null ? " " + (this.pingColor.getValue() != false ? "\u00a7" + this.getPing(npi.func_178853_c()) : "") + npi.func_178853_c() + "ms" : "").append(this.health.getValue() != false ? " \u00a7" + this.getHealth(player.getHealth() + player.getAbsorptionAmount()) + MathHelper.ceiling_float_int((float)(player.getHealth() + player.getAbsorptionAmount())) : "");
        String sting = "";
        EntityPlayerSP entityPlayerSP = mc.func_175606_aa() == null ? Nametags.mc.thePlayer : mc.func_175606_aa();
        float distance = entityPlayerSP.getDistanceToEntity((Entity)player);
        float var14 = (distance / 5.0f <= 2.0f ? 2.0f : distance / 5.0f * (this.scale.getValue().floatValue() / 100.0f * 10.0f + 1.0f)) * 2.5f * (this.scale.getValue().floatValue() / 100.0f / 10.0f);
        if ((double)distance <= 8.0) {
            var14 = 0.0245f;
        }
        GL11.glTranslated((double)((float)x), (double)((double)((float)y + this.height.getValue().floatValue()) - (player.isSneaking() ? 0.4 : 0.0) + (distance / 5.0f > 2.0f ? (double)(distance / 12.0f) - 0.7 : 0.0)), (double)((float)z));
        GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)(-Nametags.mc.func_175598_ae().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)Nametags.mc.func_175598_ae().playerViewX, (float)(Nametags.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GL11.glScalef((float)(-var14), (float)(-var14), (float)var14);
        this.disableGlCap(2896, 2929);
        this.enableGlCap(3042);
        GL11.glBlendFunc((int)770, (int)771);
        int width = FontMod.getInstance().isOn() ? this.customFont.getStringWidth(this.name) / 2 + 1 : Nametags.mc.fontRendererObj.getStringWidth(this.name) / 2 + 1;
        int color = isFriend && this.friendMode.getValue() == FriendMode.BOX ? new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB() : 0;
        int outlineColor = new Color(this.Ored.getValue(), this.Ogreen.getValue(), this.Oblue.getValue(), this.Oalpha.getValue()).getRGB();
        if (this.outlineMode.getValue() == OutLineMode.DEPEND) {
            outlineColor = Cascade.friendManager.isFriend(player.getCommandSenderName()) ? new Color(0, 191, 230, this.Oalpha.getValue()).getRGB() : (EntityUtil.isBurrow((Entity)player) ? new Color(177, 27, 196, this.Oalpha.getValue()).getRGB() : (EntityUtil.isSafe((Entity)player) ? new Color(0, 255, 0, this.Oalpha.getValue()).getRGB() : new Color(255, 0, 0, this.Oalpha.getValue()).getRGB()));
        }
        if (this.inside.getValue().booleanValue()) {
            Gui.drawRect((int)(-width - 1), (int)8, (int)(width + 1), (int)19, (int)Nametags.changeAlpha(color, 120));
        }
        if (this.outline.getValue().booleanValue()) {
            this.drawOutlineLine(-width - 1, 8.0, width + 1, 19.0, this.Owidth.getValue().floatValue(), outlineColor);
        }
        if (FontMod.getInstance().isOn()) {
            this.customFont.drawStringWithShadow(this.name, -width, 8.65f, -1);
        } else {
            Nametags.mc.fontRendererObj.func_175063_a(this.name, (float)(-width), 9.2f, -1);
        }
        if (this.armor.getValue().booleanValue()) {
            int index;
            int xOffset = -8;
            Item mainhand = player.func_184614_ca().getItem();
            Item offhand = player.func_184592_cb().getItem();
            if (mainhand != Items.field_190931_a && offhand == Items.field_190931_a) {
                xOffset = -16;
            } else if (mainhand == Items.field_190931_a && offhand != Items.field_190931_a) {
                xOffset = 0;
            }
            int count = 0;
            for (ItemStack armourStack : player.inventory.armorInventory) {
                if (armourStack == null) continue;
                xOffset -= 8;
                if (armourStack.getItem() == Items.field_190931_a) continue;
                ++count;
            }
            if (player.func_184592_cb().getItem() != Items.field_190931_a) {
                ++count;
            }
            int cacheX = xOffset - 8;
            xOffset += 8 * (5 - count) - (count == 0 ? 4 : 0);
            if (!(this.reversedHand.getValue() != false ? player.func_184592_cb().getItem() == Items.field_190931_a : player.func_184614_ca().getItem() == Items.field_190931_a)) {
                ItemStack renderStack;
                xOffset -= 10;
                if (this.reversedHand.getValue().booleanValue()) {
                    renderStack = player.func_184592_cb().copy();
                    this.renderItem(player, renderStack, xOffset, -8, cacheX, false);
                } else {
                    renderStack = player.func_184614_ca().copy();
                    this.renderItem(player, renderStack, xOffset, -8, cacheX, true);
                }
                xOffset += 18;
            } else if (!this.reversedHand.getValue().booleanValue()) {
                this.shownItem = true;
            }
            if (this.reversed.getValue().booleanValue()) {
                for (index = 0; index <= 3; ++index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.field_190931_a) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, -8, cacheX, false);
                    xOffset += 16;
                }
            } else {
                for (index = 3; index >= 0; --index) {
                    ItemStack armourStack2 = (ItemStack)player.inventory.armorInventory.get(index);
                    if (armourStack2 == null || armourStack2.getItem() == Items.field_190931_a) continue;
                    ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(player, renderStack2, xOffset, -8, cacheX, false);
                    xOffset += 16;
                }
            }
            if (!(this.reversedHand.getValue() != false ? player.func_184614_ca().getItem() == Items.field_190931_a : player.func_184592_cb().getItem() == Items.field_190931_a)) {
                ItemStack renderOffhand;
                xOffset += 0;
                if (this.reversedHand.getValue().booleanValue()) {
                    renderOffhand = player.func_184614_ca().copy();
                    this.renderItem(player, renderOffhand, xOffset, -8, cacheX, true);
                } else {
                    renderOffhand = player.func_184592_cb().copy();
                    this.renderItem(player, renderOffhand, xOffset, -8, cacheX, false);
                }
                xOffset += 8;
            }
            GlStateManager.func_179147_l();
            GlStateManager.func_179097_i();
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a((boolean)false);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
        } else if (this.durability.getValue().booleanValue()) {
            int xOffset = -6;
            int count2 = 0;
            for (ItemStack armourStack3 : player.inventory.armorInventory) {
                if (armourStack3 == null) continue;
                xOffset -= 8;
                if (armourStack3.getItem() == Items.field_190931_a) continue;
                ++count2;
            }
            if (player.func_184592_cb().getItem() != Items.field_190931_a) {
                ++count2;
            }
            int cacheX2 = xOffset - 8;
            xOffset += 8 * (5 - count2) - (count2 == 0 ? 4 : 0);
            if (this.reversed.getValue().booleanValue()) {
                for (int index2 = 0; index2 <= 3; ++index2) {
                    ItemStack armourStack4 = (ItemStack)player.inventory.armorInventory.get(index2);
                    if (armourStack4 == null || armourStack4.getItem() == Items.field_190931_a) continue;
                    ItemStack renderStack3 = armourStack4.copy();
                    this.renderDurabilityText(player, renderStack3, xOffset, -8);
                    xOffset += 16;
                }
            } else {
                for (int index2 = 3; index2 >= 0; --index2) {
                    ItemStack armourStack4 = (ItemStack)player.inventory.armorInventory.get(index2);
                    if (armourStack4 == null || armourStack4.getItem() == Items.field_190931_a) continue;
                    ItemStack renderStack3 = armourStack4.copy();
                    this.renderDurabilityText(player, renderStack3, xOffset, -8);
                    xOffset += 16;
                }
            }
            GL11.glDisable((int)2848);
            GlStateManager.func_179132_a((boolean)true);
            GlStateManager.func_179126_j();
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
        }
        this.resetCaps();
        GlStateManager.func_179117_G();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glPopMatrix();
    }

    public float getNametagSize(EntityLivingBase player) {
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = (double)scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
        EntityPlayerSP entityPlayerSP = mc.func_175606_aa() == null ? Nametags.mc.thePlayer : mc.func_175606_aa();
        return (float)twoDscale + entityPlayerSP.getDistanceToEntity((Entity)player) / 7.0f;
    }

    public void drawBorderRect(float left, float top, float right, float bottom, int bcolor, int icolor, float f) {
        Nametags.drawGuiRect(left + f, top + f, right - f, bottom - f, icolor);
        Nametags.drawGuiRect(left, top, left + f, bottom, bcolor);
        Nametags.drawGuiRect(left + f, top, right, top + f, bcolor);
        Nametags.drawGuiRect(left + f, bottom - f, right, bottom, bcolor);
        Nametags.drawGuiRect(right - f, top + f, right, bottom - f, bcolor);
    }

    public static void drawGuiRect(double x1, double y1, double x2, double y2, int color) {
        float red = (float)(color >> 24 & 0xFF) / 255.0f;
        float green = (float)(color >> 16 & 0xFF) / 255.0f;
        float blue = (float)(color >> 8 & 0xFF) / 255.0f;
        float alpha = (float)(color & 0xFF) / 255.0f;
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2848);
        GL11.glPushMatrix();
        GL11.glColor4f((float)green, (float)blue, (float)alpha, (float)red);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
    }

    public static void fakeGuiRect(double left, double top, double right, double bottom, int color) {
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
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f4 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f5 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f6 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.func_179131_c((float)f4, (float)f5, (float)f6, (float)f3);
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(left, bottom, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(right, bottom, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(right, top, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(left, top, 0.0).func_181675_d();
        tessellator.draw();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int internalColor, int borderColor) {
        GlStateManager.func_179094_E();
        Nametags.enableGL2D();
        Nametags.fakeGuiRect(x + width, y + width, x1 - width, y1 - width, internalColor);
        Nametags.fakeGuiRect(x + width, y, x1 - width, y + width, borderColor);
        Nametags.fakeGuiRect(x, y, x + width, y1, borderColor);
        Nametags.fakeGuiRect(x1 - width, y, x1, y1, borderColor);
        Nametags.fakeGuiRect(x + width, y1 - width, x1 - width, y1, borderColor);
        Nametags.disableGL2D();
        GlStateManager.func_179121_F();
    }

    public void renderItem(EntityPlayer player, ItemStack stack, int x, int y, int nameX, boolean showHeldItemText) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.func_179086_m((int)256);
        GlStateManager.func_179097_i();
        GlStateManager.func_179126_j();
        RenderHelper.enableStandardItemLighting();
        Nametags.mc.func_175599_af().zLevel = -100.0f;
        GlStateManager.func_179152_a((float)1.0f, (float)1.0f, (float)0.01f);
        mc.func_175599_af().func_180450_b(stack, x, y / 2 - 12);
        if (this.durability.getValue().booleanValue()) {
            mc.func_175599_af().func_175030_a(Nametags.mc.fontRendererObj, stack, x, y / 2 - 12);
        }
        Nametags.mc.func_175599_af().zLevel = 0.0f;
        GlStateManager.func_179152_a((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.func_179141_d();
        GlStateManager.func_179084_k();
        GlStateManager.func_179140_f();
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.func_179097_i();
        this.renderEnchantText(player, stack, x, y - 18);
        if (!this.shownItem && this.item.getValue().booleanValue() && showHeldItemText) {
            if (FontMod.getInstance().isOn()) {
                this.customFont.drawString(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), nameX * 2 + 95 - this.customFont.getStringWidth(stack.getDisplayName()) / 2, y - 37, -1, true);
            } else {
                Nametags.mc.fontRendererObj.func_175063_a(stack.getDisplayName().equalsIgnoreCase("Air") ? "" : stack.getDisplayName(), (float)(nameX * 2 + 95 - Nametags.mc.fontRendererObj.getStringWidth(stack.getDisplayName()) / 2), (float)(y - 37), -1);
            }
            this.shownItem = true;
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179152_a((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    public boolean isMaxEnchants(ItemStack stack) {
        int maxnum;
        NBTTagList enchants = stack.getEnchantmentTagList();
        ArrayList<String> enchantments = new ArrayList<String>();
        int count = 0;
        if (enchants == null) {
            return false;
        }
        for (int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.func_185262_c((int)id);
            if (enc == null) continue;
            enchantments.add(enc.getTranslatedName((int)level));
        }
        if (stack.getItem() == Items.diamond_helmet) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Respiration III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Aqua Affinity")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 5;
        }
        if (stack.getItem() == Items.diamond_chestplate) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 3;
        }
        if (stack.getItem() == Items.diamond_leggings) {
            maxnum = 3;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Blast Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 3;
        }
        if (stack.getItem() == Items.diamond_boots) {
            maxnum = 5;
            for (String s : enchantments) {
                if (s.equalsIgnoreCase("Protection IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Feather Falling IV")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Depth Strider III")) {
                    ++count;
                }
                if (s.equalsIgnoreCase("Unbreaking III")) {
                    ++count;
                }
                if (!s.equalsIgnoreCase("Mending")) continue;
                ++count;
            }
            return count >= 5;
        }
        return false;
    }

    private void renderDurabilityText(EntityPlayer player, ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask((boolean)true);
        GlStateManager.func_179086_m((int)256);
        GlStateManager.func_179097_i();
        GlStateManager.func_179126_j();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.func_179152_a((float)1.0f, (float)1.0f, (float)0.01f);
        GlStateManager.func_179152_a((float)1.0f, (float)1.0f, (float)1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.func_179141_d();
        GlStateManager.func_179084_k();
        GlStateManager.func_179140_f();
        GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
        GlStateManager.func_179097_i();
        if (stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
            float green = ((float)stack.getMaxDurability() - (float)stack.getCurrentDurability()) / (float)stack.getMaxDurability();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (FontMod.getInstance().isOn()) {
                this.customFont.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10, ColorUtil.ColorHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            } else {
                Nametags.mc.fontRendererObj.func_175063_a(dmg + "%", (float)(x * 2 + 4), (float)(y - 10), ColorUtil.ColorHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        }
        GlStateManager.func_179126_j();
        GlStateManager.func_179152_a((float)2.0f, (float)2.0f, (float)2.0f);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(EntityPlayer player, ItemStack stack, int x, int y) {
        short id;
        int encY = y;
        int yCount = y;
        if ((stack.getItem() instanceof ItemArmor || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) && this.durability.getValue().booleanValue()) {
            float green = ((float)stack.getMaxDurability() - (float)stack.getCurrentDurability()) / (float)stack.getMaxDurability();
            float red = 1.0f - green;
            int dmg = 100 - (int)(red * 100.0f);
            if (FontMod.getInstance().isOn()) {
                this.customFont.drawStringWithShadow(dmg + "%", x * 2 + 4, y - 10, ColorUtil.ColorHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            } else {
                Nametags.mc.fontRendererObj.func_175063_a(dmg + "%", (float)(x * 2 + 4), (float)(y - 10), ColorUtil.ColorHolder.toHex((int)(red * 255.0f), (int)(green * 255.0f), 0));
            }
        }
        if (this.enchantMode.getValue() == EnchantMode.NONE) {
            return;
        }
        if (this.enchantMode.getValue() == EnchantMode.MAX && this.isMaxEnchants(stack)) {
            GL11.glPushMatrix();
            GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
            if (FontMod.getInstance().isOn()) {
                this.customFont.drawStringWithShadow("Max", x * 2 + 7, yCount + 24, 0xFF0000);
            } else {
                Nametags.mc.fontRendererObj.func_175063_a("Max", (float)(x * 2 + 7), (float)(yCount + 24), 0xFF0000);
            }
            GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
            return;
        }
        if (this.enchantMode.getValue() == EnchantMode.PROT) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            if (enchants != null) {
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    id = enchants.getCompoundTagAt(index).getShort("id");
                    short level = enchants.getCompoundTagAt(index).getShort("lvl");
                    Enchantment enc = Enchantment.func_185262_c((int)id);
                    if (enc == null || enc.func_190936_d()) continue;
                    String encName = level == 1 ? enc.getTranslatedName((int)level).substring(0, 3).toLowerCase() : enc.getTranslatedName((int)level).substring(0, 2).toLowerCase() + level;
                    if (!(encName = encName.substring(0, 1).toUpperCase() + encName.substring(1)).contains("Pr") && !encName.contains("Bl")) continue;
                    GL11.glPushMatrix();
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
                    if (FontMod.getInstance().isOn()) {
                        this.customFont.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                    } else {
                        Nametags.mc.fontRendererObj.func_175063_a(encName, (float)(x * 2 + 3), (float)yCount, -1);
                    }
                    GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount += 8;
                }
            }
            return;
        }
        NBTTagList enchants = stack.getEnchantmentTagList();
        if (enchants != null) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.func_185262_c((int)id);
                if (enc == null || enc.func_190936_d()) continue;
                String encName = level == 1 ? enc.getTranslatedName((int)level).substring(0, 3).toLowerCase() : enc.getTranslatedName((int)level).substring(0, 2).toLowerCase() + level;
                encName = encName.substring(0, 1).toUpperCase() + encName.substring(1);
                GL11.glPushMatrix();
                GL11.glScalef((float)1.0f, (float)1.0f, (float)0.0f);
                if (FontMod.getInstance().isEnabled()) {
                    this.customFont.drawStringWithShadow(encName, x * 2 + 3, yCount, -1);
                } else {
                    Nametags.mc.fontRendererObj.func_175063_a(encName, (float)(x * 2 + 3), (float)yCount, -1);
                }
                GL11.glScalef((float)1.0f, (float)1.0f, (float)1.0f);
                GL11.glPopMatrix();
                encY += 8;
                yCount += 8;
            }
        }
    }

    public static final int changeAlpha(int origColor, int userInputedAlpha) {
        return userInputedAlpha << 24 | (origColor &= 0xFFFFFF);
    }

    public static void enableGL2D() {
        GL11.glDisable((int)2929);
        GL11.glEnable((int)3042);
        GL11.glDisable((int)3553);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
    }

    public static void disableGL2D() {
        GL11.glEnable((int)3553);
        GL11.glDisable((int)3042);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glHint((int)3155, (int)4352);
    }

    public void glColor(int red, int green, int blue, int alpha) {
        GlStateManager.func_179131_c((float)((float)red / 255.0f), (float)((float)green / 255.0f), (float)((float)blue / 255.0f), (float)((float)alpha / 255.0f));
    }

    public void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GlStateManager.func_179131_c((float)red, (float)green, (float)blue, (float)alpha);
    }

    private void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GlStateManager.func_179131_c((float)red, (float)green, (float)blue, (float)alpha);
    }

    public void resetCaps() {
        this.glCapMap.forEach(this::setGlState);
    }

    public void enableGlCap(int cap) {
        this.setGlCap(cap, true);
    }

    public void enableGlCap(int ... caps) {
        for (int cap : caps) {
            this.setGlCap(cap, true);
        }
    }

    public void disableGlCap(int cap) {
        this.setGlCap(cap, false);
    }

    public void disableGlCap(int ... caps) {
        for (int cap : caps) {
            this.setGlCap(cap, false);
        }
    }

    public void setGlCap(int cap, boolean state) {
        this.glCapMap.put(cap, GL11.glGetBoolean((int)cap));
        this.setGlState(cap, state);
    }

    public void setGlState(int cap, boolean state) {
        if (state) {
            GL11.glEnable((int)cap);
        } else {
            GL11.glDisable((int)cap);
        }
    }

    public void drawOutlineLine(double left, double top, double right, double bottom, float width, int color) {
        float b4;
        float g4;
        float r4;
        float a4;
        int rainbow2;
        int rainbow;
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
        float a1 = 0.0f;
        float r1 = 0.0f;
        float g1 = 0.0f;
        float b1 = 0.0f;
        float a2 = 0.0f;
        float r2 = 0.0f;
        float g2 = 0.0f;
        float b2 = 0.0f;
        float a3 = 0.0f;
        float r3 = 0.0f;
        float g3 = 0.0f;
        float b3 = 0.0f;
        if (this.outlineMode.getValue() == OutLineMode.RAINBOW) {
            rainbow = Nametags.rainbow(1).getRGB();
            rainbow2 = Nametags.rainbow(1000).getRGB();
            int rainbow3 = Nametags.rainbow(500).getRGB();
            int rainbow4 = Nametags.rainbow(1).getRGB();
            a4 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
            r4 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
            g4 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
            b4 = (float)(rainbow & 0xFF) / 255.0f;
            a1 = (float)(rainbow2 >> 24 & 0xFF) / 255.0f;
            r1 = (float)(rainbow2 >> 16 & 0xFF) / 255.0f;
            g1 = (float)(rainbow2 >> 8 & 0xFF) / 255.0f;
            b1 = (float)(rainbow2 & 0xFF) / 255.0f;
            a2 = (float)(rainbow3 >> 24 & 0xFF) / 255.0f;
            r2 = (float)(rainbow3 >> 16 & 0xFF) / 255.0f;
            g2 = (float)(rainbow3 >> 8 & 0xFF) / 255.0f;
            b2 = (float)(rainbow3 & 0xFF) / 255.0f;
            a3 = (float)(rainbow4 >> 24 & 0xFF) / 255.0f;
            r3 = (float)(rainbow4 >> 16 & 0xFF) / 255.0f;
            g3 = (float)(rainbow4 >> 8 & 0xFF) / 255.0f;
            b3 = (float)(rainbow4 & 0xFF) / 255.0f;
        } else if (this.outlineMode.getValue() == OutLineMode.RAINBOW2) {
            rainbow = Nametags.rainbow(1).getRGB();
            rainbow2 = Nametags.rainbow(1000).getRGB();
            a4 = (float)(rainbow >> 24 & 0xFF) / 255.0f;
            r4 = (float)(rainbow >> 16 & 0xFF) / 255.0f;
            g4 = (float)(rainbow >> 8 & 0xFF) / 255.0f;
            b4 = (float)(rainbow & 0xFF) / 255.0f;
            a1 = (float)(rainbow2 >> 24 & 0xFF) / 255.0f;
            r1 = (float)(rainbow2 >> 16 & 0xFF) / 255.0f;
            g1 = (float)(rainbow2 >> 8 & 0xFF) / 255.0f;
            b1 = (float)(rainbow2 & 0xFF) / 255.0f;
        } else {
            a4 = (float)(color >> 24 & 0xFF) / 255.0f;
            r4 = (float)(color >> 16 & 0xFF) / 255.0f;
            g4 = (float)(color >> 8 & 0xFF) / 255.0f;
            b4 = (float)(color & 0xFF) / 255.0f;
        }
        Tessellator tessellator = Tessellator.func_178181_a();
        BufferBuilder bufferbuilder = tessellator.func_178180_c();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        if (this.outlineMode.getValue() == OutLineMode.RAINBOW) {
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, bottom, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
            bufferbuilder.func_181662_b(right, top, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
            bufferbuilder.func_181662_b(left, top, 0.0).func_181666_a(r2, g2, b2, a2).func_181675_d();
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r3, g3, b3, a3).func_181675_d();
        } else if (this.outlineMode.getValue() == OutLineMode.DEPEND) {
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(left, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        } else if (this.outlineMode.getValue() == OutLineMode.NORMAL) {
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(left, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        } else if (this.outlineMode.getValue() == OutLineMode.RAINBOW2) {
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(right, bottom, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
            bufferbuilder.func_181662_b(right, top, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
            bufferbuilder.func_181662_b(left, top, 0.0).func_181666_a(r1, g1, b1, a1).func_181675_d();
            bufferbuilder.func_181662_b(left, bottom, 0.0).func_181666_a(r4, g4, b4, a4).func_181675_d();
        }
        tessellator.draw();
        GL11.glDisable((int)2848);
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    public static Color rainbow(int delay) {
        double rainbowState = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((rainbowState %= 360.0) / 360.0), 1.0f, 1.0f);
    }

    public static enum OutLineMode {
        NORMAL,
        DEPEND,
        RAINBOW,
        RAINBOW2;

    }

    public static enum FriendMode {
        TEXT,
        BOX;

    }

    public static enum EnchantMode {
        PROT,
        LIST,
        MAX,
        NONE;

    }
}


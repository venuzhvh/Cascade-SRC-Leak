/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.GlStateManager$DestFactor
 *  net.minecraft.client.renderer.GlStateManager$SourceFactor
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.settings.GameSettings$Options
 *  net.minecraft.inventory.ItemStackHelper
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FOVModifier
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.PerspectiveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.render.ColorUtil;
import cascade.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Visual
extends Module {
    Setting<Page> page = this.register(new Setting<Page>("Page", Page.Player));
    Setting<Boolean> instantSwap = this.register(new Setting<Object>("InstantSwap", Boolean.valueOf(false), v -> this.page.getValue() == Page.Player));
    public Setting<Boolean> shulkerPreview = this.register(new Setting<Object>("ShulkerPreview", Boolean.valueOf(true), v -> this.page.getValue() == Page.Player));
    Setting<Boolean> fovChanger = this.register(new Setting<Object>("FovChanger", Boolean.valueOf(false), v -> this.page.getValue() == Page.Player));
    Setting<Boolean> stay = this.register(new Setting<Object>("Stay", Boolean.valueOf(false), v -> this.page.getValue() == Page.Player && this.fovChanger.getValue() != false));
    Setting<Integer> fov = this.register(new Setting<Object>("Fov", Integer.valueOf(137), Integer.valueOf(-180), Integer.valueOf(180), v -> this.page.getValue() == Page.Player && this.fovChanger.getValue() != false));
    public Setting<Swing> swing = this.register(new Setting<Object>("Swing", (Object)Swing.Mainhand, v -> this.page.getValue() == Page.Player));
    public Setting<Boolean> aspect = this.register(new Setting<Object>("Aspect", Boolean.valueOf(false), v -> this.page.getValue() == Page.Player));
    public Setting<Float> aspectValue = this.register(new Setting<Object>("Value", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.page.getValue() == Page.Player && this.aspect.getValue() != false));
    Setting<Boolean> fullBright = this.register(new Setting<Object>("FullBright", Boolean.valueOf(true), v -> this.page.getValue() == Page.World));
    Setting<Boolean> skyChanger = this.register(new Setting<Object>("SkyChanger", Boolean.valueOf(false), v -> this.page.getValue() == Page.World));
    Setting<Color> c = this.register(new Setting<Object>("SkyColor", new Color(-1), v -> this.page.getValue() == Page.World));
    static ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    static Visual INSTANCE = new Visual();
    float originalBrightness;

    public Visual() {
        super("Visual", Module.Category.VISUAL, "Visual tweaks");
        INSTANCE = this;
    }

    public static Visual getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Visual();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (Visual.fullNullCheck()) {
            return;
        }
        if (this.instantSwap.getValue().booleanValue() && (double)Visual.mc.entityRenderer.itemRenderer.field_187470_g >= 0.9) {
            Visual.mc.entityRenderer.itemRenderer.field_187469_f = 1.0f;
            Visual.mc.entityRenderer.itemRenderer.field_187467_d = Visual.mc.thePlayer.func_184614_ca();
        }
        if (this.fovChanger.getValue().booleanValue() && !this.stay.getValue().booleanValue()) {
            Visual.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, (float)this.fov.getValue().intValue());
        }
        if (this.fullBright.getValue().booleanValue() && Visual.mc.gameSettings.gammaSetting != 42069.0f) {
            Visual.mc.gameSettings.gammaSetting = 42069.0f;
        }
    }

    @Override
    public void onEnable() {
        if (Visual.fullNullCheck()) {
            return;
        }
        this.originalBrightness = Visual.mc.gameSettings.gammaSetting;
        if (this.fullBright.getValue().booleanValue()) {
            Visual.mc.gameSettings.gammaSetting = 42069.0f;
        }
    }

    @Override
    public void onDisable() {
        if (Visual.fullNullCheck()) {
            return;
        }
        Visual.mc.gameSettings.gammaSetting = this.originalBrightness;
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors e) {
        if (this.skyChanger.getValue().booleanValue() && this.isEnabled() && e != null) {
            e.setRed((float)this.c.getValue().getRed() / 255.0f);
            e.setGreen((float)this.c.getValue().getGreen() / 255.0f);
            e.setBlue((float)this.c.getValue().getBlue() / 255.0f);
        }
    }

    @SubscribeEvent
    public void onFovChange(EntityViewRenderEvent.FOVModifier e) {
        if (this.fovChanger.getValue().booleanValue() && this.stay.getValue().booleanValue() && this.isEnabled() && e != null) {
            e.setFOV((float)this.fov.getValue().intValue());
        }
    }

    @SubscribeEvent
    public void onPerspectiveEvent(PerspectiveEvent e) {
        if (this.aspect.getValue().booleanValue() && this.isEnabled() && e != null) {
            e.setAspect(this.aspectValue.getValue().floatValue());
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
            RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
            RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54, 500);
            RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
            GlStateManager.func_179097_i();
            Color color = new Color(-1);
            this.renderer.drawStringWithShadow(name == null ? stack.getDisplayName() : name, x + 8, y + 6, ColorUtil.toRGBA(color));
            GlStateManager.func_179126_j();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.func_179091_B();
            GlStateManager.func_179142_g();
            GlStateManager.func_179145_e();
            NonNullList nonnulllist = NonNullList.func_191197_a((int)27, (Object)ItemStack.field_190927_a);
            ItemStackHelper.func_191283_b((NBTTagCompound)blockEntityTag, (NonNullList)nonnulllist);
            for (int i = 0; i < nonnulllist.size(); ++i) {
                int iX = x + i % 9 * 18 + 8;
                int iY = y + i / 9 * 18 + 18;
                ItemStack itemStack = (ItemStack)nonnulllist.get(i);
                Visual.mc.func_175599_af().zLevel = 501.0f;
                RenderUtil.itemRender.func_180450_b(itemStack, iX, iY);
                RenderUtil.itemRender.func_180453_a(Visual.mc.fontRendererObj, itemStack, iX, iY, null);
                Visual.mc.func_175599_af().zLevel = 0.0f;
            }
            GlStateManager.func_179140_f();
            GlStateManager.func_179084_k();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    public static enum Swing {
        Mainhand,
        Offhand,
        Packet;

    }

    static enum Page {
        Player,
        World;

    }
}


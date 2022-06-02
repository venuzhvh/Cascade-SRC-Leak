/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.common.ForgeModContainer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.event.events.ClientEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Wallhack
extends Module {
    public static Wallhack INSTANCE;
    public Setting<Integer> opacity = this.register(new Setting<Integer>("Opacity", 120, 0, 255));
    public Setting<Integer> light = this.register(new Setting<Integer>("Light", 100, 0, 100));
    public Setting<Reload> reload = this.register(new Setting<Reload>("Reload", Reload.Soft));
    private boolean needsReload = false;
    public static ArrayList<Block> blocks;

    public Wallhack() {
        super("Wallhack", Module.Category.VISUAL, "Sets opacity for blocks");
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ForgeModContainer.forgeLightPipelineEnabled = false;
        if (Wallhack.fullNullCheck()) {
            this.reload();
        } else {
            this.needsReload = true;
        }
    }

    @Override
    public void onDisable() {
        this.needsReload = false;
        if (!Wallhack.fullNullCheck()) {
            this.reload();
        }
        Wallhack.mc.field_175612_E = false;
        ForgeModContainer.forgeLightPipelineEnabled = true;
    }

    @Override
    public void onUpdate() {
        if (this.needsReload) {
            this.needsReload = false;
            this.reload();
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled()) {
            this.reload();
        }
    }

    private void reload() {
        Wallhack.mc.field_175612_E = true;
        if (this.reload.getValue() == Reload.All) {
            Wallhack.mc.renderGlobal.loadRenderers();
        }
        if (this.reload.getValue() == Reload.Soft) {
            Vec3d pos = Wallhack.mc.thePlayer.func_174791_d();
            int dist = Wallhack.mc.gameSettings.renderDistanceChunks * 16;
            Wallhack.mc.renderGlobal.markBlockRangeForRenderUpdate((int)pos.xCoord - dist, (int)pos.yCoord - dist, (int)pos.zCoord - dist, (int)pos.xCoord + dist, (int)pos.yCoord + dist, (int)pos.zCoord + dist);
        }
    }

    static {
        blocks = Lists.newArrayList((Object[])new Block[]{Blocks.coal_ore, Blocks.iron_ore, Blocks.gold_ore, Blocks.lapis_ore, Blocks.redstone_ore, Blocks.diamond_ore, Blocks.coal_block, Blocks.iron_block, Blocks.gold_block, Blocks.lapis_block, Blocks.redstone_block, Blocks.diamond_block, Blocks.iron_bars, Blocks.redstone_lamp, Blocks.lit_redstone_lamp, Blocks.furnace, Blocks.lit_furnace, Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest});
    }

    public static enum Reload {
        Soft,
        All;

    }
}


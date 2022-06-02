/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.Cascade;
import cascade.event.events.BlockEvent;
import cascade.event.events.PacketEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Mine
extends Module {
    Setting<Boolean> render = this.register(new Setting<Boolean>("Render", true));
    Setting<Integer> boxAlpha = this.register(new Setting<Object>("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> this.render.getValue()));
    Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.0f), Float.valueOf(5.0f), v -> this.render.getValue()));
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(50.0f)));
    static Mine INSTANCE = new Mine();
    IBlockState blockState;
    EnumFacing lastFacing = null;
    Timer timer = new Timer();
    boolean isMining = false;
    BlockPos lastPos = null;
    public BlockPos currentPos;
    Boolean switched;
    EnumFacing facing;

    public Mine() {
        super("Mine", Module.Category.PLAYER, "tweaks mining shit");
        INSTANCE = this;
    }

    public static Mine getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Mine();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.switched = false;
    }

    @Override
    public void onDisable() {
        this.switched = false;
        this.facing = null;
    }

    @Override
    public void onUpdate() {
        if (Mine.fullNullCheck()) {
            return;
        }
        Mine.mc.playerController.blockHitDelay = 0;
        if (this.isMining && this.lastPos != null && this.lastFacing != null) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
        }
        if (this.currentPos != null) {
            if (Mine.mc.thePlayer.func_174818_b(this.currentPos) > MathUtil.square(this.range.getValue().floatValue())) {
                this.currentPos = null;
                this.blockState = null;
                return;
            }
            if (Mine.mc.theWorld.func_180495_p(this.currentPos) != this.blockState || Mine.mc.theWorld.func_180495_p(this.currentPos).func_177230_c() == Blocks.air) {
                this.currentPos = null;
                this.blockState = null;
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (Mine.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.render.getValue().booleanValue() && this.currentPos != null) {
            Color color = new Color(this.timer.passedMs((int)(2000.0f * Cascade.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0f * Cascade.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawBoxESP(this.currentPos, color, false, color, this.lineWidth.getValue().floatValue(), true, true, this.boxAlpha.getValue(), false);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        CPacketPlayerDigging packet;
        if (Mine.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)e.getPacket()) != null && packet.func_179715_a() != null) {
            try {
                for (Entity entity : Mine.mc.theWorld.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet.func_179715_a()))) {
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.showAnimation(false, null, null);
                    return;
                }
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from Mine");
                ex.printStackTrace();
            }
            if (packet.func_180762_c().equals((Object)CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                this.showAnimation(true, packet.func_179715_a(), packet.func_179714_b());
            }
            if (packet.func_180762_c().equals((Object)CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                this.showAnimation(false, null, null);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getStage() == 4 && BlockUtil.canBreak(e.pos)) {
            if (this.currentPos == null) {
                this.currentPos = e.pos;
                this.blockState = Mine.mc.theWorld.func_180495_p(this.currentPos);
                this.timer.reset();
            }
            Mine.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, e.pos, e.facing));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, e.pos, e.facing));
            this.facing = e.facing;
            e.setCanceled(true);
        }
    }

    void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
        this.isMining = isMining;
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }
}


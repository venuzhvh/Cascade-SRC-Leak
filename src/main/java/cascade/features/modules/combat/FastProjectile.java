/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.event.events.PacketEvent;
import cascade.event.events.RightClickItemEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.player.MovementUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastProjectile
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Bow));
    Setting<Boolean> cancelRotate = this.register(new Setting<Boolean>("CancelRotate", false));
    Setting<Boolean> move = this.register(new Setting<Boolean>("Move", false));
    Setting<Boolean> blink = this.register(new Setting<Boolean>("Blink", true));
    Setting<Boolean> staticS = this.register(new Setting<Boolean>("Static", true));
    Setting<Boolean> always = this.register(new Setting<Boolean>("Always", false));
    Setting<Integer> runs = this.register(new Setting<Integer>("Runs", 8, 1, 200));
    Setting<Integer> buffer = this.register(new Setting<Integer>("Buffer", 10, 0, 200));
    Setting<Integer> teleports = this.register(new Setting<Integer>("Teleports", 0, 0, 100));
    Setting<Integer> interval = this.register(new Setting<Integer>("Interval", 25, 0, 100));
    int packetsSent = 0;
    boolean cancelling;

    public FastProjectile() {
        super("FastProjectile", Module.Category.COMBAT, "Increases arrow velocity");
    }

    @Override
    public String getDisplayInfo() {
        if (this.cancelling) {
            if (this.packetsSent >= this.runs.getValue() * 2 || this.always.getValue().booleanValue()) {
                return "" + ChatFormatting.GREEN + this.packetsSent;
            }
            return "" + ChatFormatting.RED + this.packetsSent;
        }
        return null;
    }

    @Override
    public void onEnable() {
        this.packetsSent = 0;
        this.cancelling = false;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (this.isDisabled() || FastProjectile.fullNullCheck() || !FastProjectile.mc.thePlayer.isCollidedVertically) {
            return;
        }
        if (event.getStage() != 0) {
            if (FastProjectile.mc.thePlayer.func_184607_cu().getItem() != Items.bow && this.mode.getValue() == Mode.Bow || FastProjectile.mc.thePlayer.func_184607_cu().getItem() != Items.egg && this.mode.getValue() == Mode.Egg || FastProjectile.mc.thePlayer.func_184607_cu().getItem() != Items.snowball && this.mode.getValue() == Mode.Snowball) {
                this.cancelling = false;
                this.packetsSent = 0;
            } else if (FastProjectile.mc.thePlayer.func_184607_cu().getItem() == Items.bow && FastProjectile.mc.thePlayer.func_184587_cr() && this.cancelling) {
                ++this.packetsSent;
                if (this.packetsSent <= this.runs.getValue() * 2 || !this.always.getValue().booleanValue()) {
                    // empty if block
                }
            }
        }
    }

    public boolean heldIemCheck() {
        if (FastProjectile.mc.thePlayer.func_184607_cu().getItem() == Items.bow && this.mode.getValue() == Mode.Bow) {
            return true;
        }
        if (FastProjectile.mc.thePlayer.func_184607_cu().getItem() == Items.egg && this.mode.getValue() == Mode.Egg) {
            return true;
        }
        return FastProjectile.mc.thePlayer.func_184607_cu().getItem() == Items.snowball && this.mode.getValue() == Mode.Snowball;
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.isDisabled() || FastProjectile.fullNullCheck() || !FastProjectile.mc.thePlayer.isCollidedVertically) {
            return;
        }
        if (this.staticS.getValue().booleanValue() && this.heldIemCheck()) {
            FastProjectile.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
            e.setX(0.0);
            e.setY(0.0);
            e.setZ(0.0);
        }
    }

    @SubscribeEvent
    public void onRightClickItem(RightClickItemEvent e) {
        if (this.isDisabled() || FastProjectile.fullNullCheck() || !FastProjectile.mc.thePlayer.isCollidedVertically) {
            return;
        }
        if (FastProjectile.mc.thePlayer.func_184586_b(e.getHand()).getItem() == Items.bow) {
            this.cancelling = true;
        }
    }

    @SubscribeEvent
    protected void onPacketSend(PacketEvent.Send e) {
        if (this.isDisabled() || FastProjectile.fullNullCheck() || !FastProjectile.mc.thePlayer.onGround) {
            return;
        }
        if ((e.getPacket() instanceof CPacketPlayer.Position || e.getPacket() instanceof CPacketPlayer.PositionRotation) && this.blink.getValue().booleanValue() && this.cancelling) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketPlayer.Rotation && this.cancelRotate.getValue().booleanValue() && this.blink.getValue().booleanValue() && this.cancelling) {
            e.setCanceled(true);
        }
        if (e.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging)e.getPacket()).func_180762_c() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && this.heldIemCheck()) {
            this.cancelling = false;
            if (this.packetsSent >= this.runs.getValue() * 2 || this.always.getValue().booleanValue()) {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)FastProjectile.mc.thePlayer, CPacketEntityAction.Action.START_SPRINTING));
                if (this.cancelRotate.getValue().booleanValue()) {
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Rotation(FastProjectile.mc.thePlayer.rotationYaw, FastProjectile.mc.thePlayer.rotationPitch, true));
                }
                for (int i = 0; i < this.runs.getValue() + this.buffer.getValue(); ++i) {
                    if (i != 0 && i % this.interval.getValue() == 0) {
                        int id = Cascade.positionManager.getTeleportID();
                        for (int j = 0; j < this.teleports.getValue(); ++j) {
                            mc.getNetHandler().addToSendQueue((Packet)new CPacketConfirmTeleport(++id));
                        }
                    }
                    double[] dir = MovementUtil.strafe(0.001);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.PositionRotation(FastProjectile.mc.thePlayer.posX + (this.move.getValue() != false ? dir[0] : 0.0), FastProjectile.mc.thePlayer.posY + 1.3E-13, FastProjectile.mc.thePlayer.posZ + (this.move.getValue() != false ? dir[1] : 0.0), FastProjectile.mc.thePlayer.rotationYaw, FastProjectile.mc.thePlayer.rotationPitch, true));
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.PositionRotation(FastProjectile.mc.thePlayer.posX + (this.move.getValue() != false ? dir[0] * 2.0 : 0.0), FastProjectile.mc.thePlayer.posY + 2.7E-13, FastProjectile.mc.thePlayer.posZ + (this.move.getValue() != false ? dir[1] * 2.0 : 0.0), FastProjectile.mc.thePlayer.rotationYaw, FastProjectile.mc.thePlayer.rotationPitch, false));
                }
            }
            this.packetsSent = 0;
        }
    }

    static enum Mode {
        Bow,
        Egg,
        Snowball;

    }
}


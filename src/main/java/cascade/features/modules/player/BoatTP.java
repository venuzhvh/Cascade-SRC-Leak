/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketVehicleMove
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.player;

import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.util.math.BlockPos;

public class BoatTP
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Sand));
    EntityBoat boat;

    public BoatTP() {
        super("BoatTP", Module.Category.PLAYER, "Teleports yo boatz");
    }

    @Override
    public void onEnable() {
        if (BoatTP.fullNullCheck() || !BoatTP.mc.thePlayer.func_184218_aH()) {
            return;
        }
        if (this.mode.getValue() == Mode.Sand) {
            double cos = Math.cos(Math.toRadians(BoatTP.mc.thePlayer.rotationYaw + 90.0f));
            double sin = Math.sin(Math.toRadians(BoatTP.mc.thePlayer.rotationYaw + 90.0f));
            this.boat.setPosition(this.boat.posX + 0.00625 * cos, this.boat.posY, this.boat.posZ + 0.00625 * sin);
        }
        if (this.mode.getValue() == Mode.Packet) {
            if (BoatTP.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                this.clip(this.findNextAvailableSpaceUp((int)this.boat.posY));
            }
            if (BoatTP.mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
                this.clip(this.findNextAvailableSpaceDown((int)this.boat.posY));
            }
        }
    }

    @Override
    public void onUpdate() {
        if (BoatTP.fullNullCheck() || !BoatTP.mc.thePlayer.func_184218_aH()) {
            return;
        }
        if (BoatTP.mc.thePlayer.func_184187_bx() != null && BoatTP.mc.thePlayer.func_184187_bx() instanceof EntityBoat) {
            this.boat = (EntityBoat)BoatTP.mc.thePlayer.func_184187_bx();
        }
        if (this.boat == null) {
            Command.sendMessage(ChatFormatting.RED + "You are not in a boat", true, false);
            this.disable();
        }
        if (this.mode.getValue() == Mode.Sand && BoatTP.mc.gameSettings.keyBindJump.getIsKeyPressed() && this.boat.onGround) {
            this.boat.motionY = 0.4012312889099121;
        }
    }

    public void clip(int y) {
        this.boat.setPositionAndUpdate(BoatTP.mc.thePlayer.posX, (double)y, BoatTP.mc.thePlayer.posZ);
        mc.getNetHandler().addToSendQueue((Packet)new CPacketVehicleMove((Entity)this.boat));
    }

    public int findNextAvailableSpaceUp(int start) {
        int up = start;
        int playerX = (int)BoatTP.mc.thePlayer.posX;
        int playerZ = (int)BoatTP.mc.thePlayer.posZ;
        for (int i = start + 1; i < (BoatTP.mc.thePlayer.dimension != 1 ? 257 : 125); ++i) {
            if (!BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)i, (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air) || !BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)(i + 1), (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air) || BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)(i - 1), (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air)) continue;
            up = i;
            return up;
        }
        return up;
    }

    public int findNextAvailableSpaceDown(int start) {
        int down = start;
        int playerX = (int)BoatTP.mc.thePlayer.posX;
        int playerZ = (int)BoatTP.mc.thePlayer.posZ;
        for (int i = start - 1; i > 4; --i) {
            if (!BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)i, (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air) || !BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)(i + 1), (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air) || BoatTP.mc.theWorld.func_180495_p((BlockPos)new BlockPos((int)playerX, (int)(i - 1), (int)playerZ)).func_177230_c().blockMaterial.equals(Material.air)) continue;
            down = i;
            return down;
        }
        return down;
    }

    static enum Mode {
        Sand,
        Packet;

    }
}


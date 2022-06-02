/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;

public class Anchor
extends Module {
    public Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Instant));
    public Setting<Boolean> onlySafe = this.register(new Setting<Boolean>("OnlySafe", true));
    public Setting<Boolean> onlyOnGround = this.register(new Setting<Boolean>("OnlyOnGround", true));
    Vec3d center = Vec3d.field_186680_a;

    public Anchor() {
        super("Anchor", Module.Category.COMBAT, "Helps you with entering h***s");
    }

    @Override
    public void onEnable() {
        if (Anchor.fullNullCheck()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("Strafe") || Cascade.moduleManager.isModuleEnabled("Boost") || Cascade.moduleManager.isModuleEnabled("YPortOld") || Cascade.moduleManager.isModuleEnabled("Tickshift")) {
            this.disable();
            return;
        }
        if (!Anchor.mc.thePlayer.onGround && this.onlyOnGround.getValue().booleanValue() || Anchor.mc.thePlayer.isOnLadder() || Anchor.mc.thePlayer.posY < 0.0) {
            this.disable();
            return;
        }
        if (this.onlySafe.getValue().booleanValue() && !EntityUtil.isPlayerSafe((EntityPlayer)Anchor.mc.thePlayer)) {
            this.disable();
            return;
        }
        if (this.mode.getValue() == Mode.Instant) {
            this.center = EntityUtil.getCenter(Anchor.mc.thePlayer.posX, Anchor.mc.thePlayer.posY, Anchor.mc.thePlayer.posZ);
            MovementUtil.setMotion(0.0, 0.0, 0.0);
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(this.center.xCoord, this.center.yCoord, this.center.zCoord, Anchor.mc.thePlayer.onGround));
            Anchor.mc.thePlayer.setPosition(this.center.xCoord, this.center.yCoord, this.center.zCoord);
        } else {
            MovementUtil.setMotion((this.center.xCoord - Anchor.mc.thePlayer.posX) / 2.0, (this.center.yCoord - Anchor.mc.thePlayer.posY) / 2.0, (this.center.zCoord - Anchor.mc.thePlayer.posZ) / 2.0);
        }
        this.disable();
    }

    public static enum Mode {
        Instant,
        NCP;

    }
}


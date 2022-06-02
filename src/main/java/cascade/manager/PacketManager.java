/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 */
package cascade.manager;

import cascade.event.events.PacketEvent;
import cascade.features.Feature;
import cascade.util.misc.Timer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PacketManager
extends Feature {
    SPacketExplosion pExplosion = null;
    Timer timerExplosion = new Timer();
    boolean caughtPExplosion = false;
    SPacketPlayerPosLook pPlayerPosLook = null;
    Timer timerPlayerPosLook = new Timer();
    boolean caughtPlayerPosLook = false;

    public void load() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (PacketManager.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.pPlayerPosLook = (SPacketPlayerPosLook)e.getPacket();
            this.timerPlayerPosLook.reset();
            this.caughtPlayerPosLook = true;
        }
        if (e.getPacket() instanceof SPacketExplosion) {
            this.pExplosion = (SPacketExplosion)e.getPacket();
            this.timerExplosion.reset();
            this.caughtPExplosion = true;
        }
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e) {
        if (PacketManager.fullNullCheck()) {
            return;
        }
        if (this.timerExplosion.passedMs(250L)) {
            this.pExplosion = null;
            this.caughtPExplosion = false;
        }
        if (this.timerPlayerPosLook.passedMs(250L)) {
            this.pPlayerPosLook = null;
            this.caughtPlayerPosLook = false;
        }
    }

    public boolean caughtPlayerPosLook() {
        return this.caughtPlayerPosLook;
    }

    public boolean caughtPExplosion() {
        return this.caughtPExplosion;
    }

    public SPacketPlayerPosLook pPlayerPosLook() {
        return this.pPlayerPosLook;
    }

    public SPacketExplosion pExplosion() {
        return this.pExplosion;
    }

    public Timer timerExplosion() {
        return this.timerExplosion;
    }

    public Timer timerPlayerPosLook() {
        return this.timerPlayerPosLook;
    }
}


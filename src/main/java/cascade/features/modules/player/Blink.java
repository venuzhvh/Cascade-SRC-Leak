/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink
extends Module {
    Queue<Packet<?>> packets = new LinkedList();
    boolean shouldSend;

    public Blink() {
        super("Blink", Module.Category.PLAYER, "Simulates lag");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (this.isDisabled() || Blink.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer) {
            mc.addScheduledTask(() -> this.packets.add((Packet<?>)e.getPacket()));
            e.setCanceled(true);
        }
    }

    @Override
    public void onDisable() {
        if (Blink.fullNullCheck()) {
            return;
        }
        if (this.shouldSend && mc.getNetHandler() != null) {
            this.emptyQueue(this.packets, p -> mc.getNetHandler().addToSendQueue(p));
        } else {
            this.packets.clear();
        }
        this.shouldSend = true;
    }

    <T> void emptyQueue(Queue<T> queue, Consumer<T> onPoll) {
        while (!queue.isEmpty()) {
            T polled = queue.poll();
            if (polled == null) continue;
            onPoll.accept(polled);
        }
    }
}


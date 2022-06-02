/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.MovementUtil;
import cascade.util.player.PhysicsUtil;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TickShift
extends Module {
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    Setting<Integer> factor = this.register(new Setting<Integer>("Factor", 5, 1, 100));
    Setting<Integer> toggle = this.register(new Setting<Integer>("AutoDisable", 0, 0, 500));
    Setting<Boolean> blink = this.register(new Setting<Boolean>("Blink", false));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));
    Queue<Packet<?>> packets = new LinkedList();
    Timer toggleTimer = new Timer();
    boolean shouldSend;
    int runs = 0;

    public TickShift() {
        super("TickShift", Module.Category.MOVEMENT, "A movement exploit");
    }

    @Override
    public void onUpdate() {
        if (TickShift.fullNullCheck()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        MovementUtil.strafe(MovementUtil.getSpeed());
        if (this.runs < this.factor.getValue()) {
            ++this.runs;
            PhysicsUtil.runPhysicsTick();
        } else {
            this.runs = 0;
        }
        if (this.toggle.getValue() != 0 && this.toggleTimer.passedMs(this.toggle.getValue().intValue())) {
            this.disable();
            return;
        }
    }

    @Override
    public void onToggle() {
        if (TickShift.fullNullCheck()) {
            return;
        }
        if (this.step.getValue().booleanValue()) {
            TickShift.mc.thePlayer.stepHeight = 0.6f;
        }
        this.runs = 0;
        this.toggleTimer.reset();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (this.isDisabled() || TickShift.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer && this.blink.getValue().booleanValue()) {
            mc.addScheduledTask(() -> this.packets.add((Packet<?>)e.getPacket()));
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled() || TickShift.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
            return;
        }
    }

    @Override
    public void onLogout() {
        if (TickShift.fullNullCheck()) {
            return;
        }
        if (this.blink.getValue().booleanValue()) {
            this.disable();
            return;
        }
    }

    @Override
    public void onDisable() {
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


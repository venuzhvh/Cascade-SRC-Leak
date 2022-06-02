/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.EnumHand
 */
package cascade.features.modules.player;

import cascade.Cascade;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.Util;
import cascade.util.player.InventoryUtil;
import java.util.concurrent.TimeUnit;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

public class FastUse
extends Module {
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", false));
    Setting<Integer> packets = this.register(new Setting<Object>("Packets", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(16), v -> this.packet.getValue()));
    Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), v -> this.packet.getValue()));

    public FastUse() {
        super("FastUse", Module.Category.PLAYER, "fast use,");
    }

    @Override
    public void onUpdate() {
        if (FastUse.fullNullCheck()) {
            return;
        }
        if (InventoryUtil.heldItem(Items.experience_bottle, InventoryUtil.Hand.Both) && FastUse.mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
            if (this.packet.getValue().booleanValue()) {
                FastUse.mc.rightClickDelayTimer = 0;
                for (int s = 1; s < this.packets.getValue(); ++s) {
                    this.sendPacket();
                }
            } else {
                FastUse.mc.rightClickDelayTimer = 0;
            }
        }
    }

    void sendPacket() {
        PacketThread packetThread = new PacketThread(this.delay.getValue());
        if (this.delay.getValue() == 0) {
            packetThread.run();
        } else {
            packetThread.start();
        }
    }

    static class PacketThread
    extends Thread {
        int delay;

        public PacketThread(int delayIn) {
            this.delay = delayIn;
        }

        @Override
        public void run() {
            try {
                if (this.delay != 0) {
                    TimeUnit.MILLISECONDS.sleep(this.delay);
                }
                Util.mc.addScheduledTask(() -> Util.mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
            }
            catch (InterruptedException ex) {
                Cascade.LOGGER.info("Caught an exception from FastUse");
                ex.printStackTrace();
            }
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import java.util.Objects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Crits
extends Module {
    public Setting<Integer> packets = this.register(new Setting<Integer>("Packets", 1, 1, 5));

    public Crits() {
        super("Crits", Module.Category.COMBAT, "Scores criticals for you");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (Crits.fullNullCheck() || this.isDisabled() || Crits.mc.thePlayer.func_175149_v()) {
            return;
        }
        if (!(e.getPacket() instanceof CPacketUseEntity)) {
            return;
        }
        CPacketUseEntity packet = (CPacketUseEntity)e.getPacket();
        if (packet.getEntityFromWorld((World)Crits.mc.theWorld) instanceof EntityLivingBase && (packet = (CPacketUseEntity)e.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
            if (!Crits.mc.thePlayer.onGround || EntityUtil.isInLiquid()) {
                return;
            }
            switch (this.packets.getValue()) {
                case 1: {
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + (double)0.1f, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    break;
                }
                case 2: {
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.0625101, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 1.1E-5, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    break;
                }
                case 3: {
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.0625101, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.0125, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    break;
                }
                case 4: {
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.05, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.03, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    break;
                }
                case 5: {
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 0.1625, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 4.0E-6, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY + 1.0E-6, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Position(Crits.mc.thePlayer.posX, Crits.mc.thePlayer.posY, Crits.mc.thePlayer.posZ, false));
                    Crits.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer());
                    Crits.mc.thePlayer.onCriticalHit(Objects.requireNonNull(packet.getEntityFromWorld((World)Crits.mc.theWorld)));
                }
            }
        }
    }
}


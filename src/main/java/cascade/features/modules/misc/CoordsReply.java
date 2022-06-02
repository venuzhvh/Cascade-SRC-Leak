/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketChat
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CoordsReply
extends Module {
    Setting<Integer> radius = this.register(new Setting<Integer>("Radius", 2000, 0, 2000));

    public CoordsReply() {
        super("CoordsReply", Module.Category.MISC, "Replies to friends asking for coords.");
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (CoordsReply.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketChat) {
            String chatMessage = ((SPacketChat)e.getPacket()).func_148915_c().getUnformattedText();
            String chatMessageLowercase = chatMessage.toLowerCase();
            if (chatMessage.contains("says: ") || chatMessage.contains("whispers: ")) {
                String username = chatMessage.split(" ")[0];
                if (username == CoordsReply.mc.thePlayer.getCommandSenderName() || !Cascade.friendManager.isFriend(username) || CoordsReply.mc.thePlayer.getDistance(0.0, CoordsReply.mc.thePlayer.posY, 0.0) > (double)this.radius.getValue().intValue()) {
                    return;
                }
                if (chatMessageLowercase.contains("cord") || chatMessageLowercase.contains("coord") || chatMessageLowercase.contains("coords") || chatMessageLowercase.contains("cords") || chatMessageLowercase.contains("wya") || chatMessageLowercase.contains("where are you") || chatMessageLowercase.contains("where r u") || chatMessageLowercase.contains("where ru")) {
                    if (chatMessageLowercase.contains("discord")) {
                        return;
                    }
                    CoordsReply.mc.thePlayer.sendChatMessage("/msg " + username + " X: " + (int)CoordsReply.mc.thePlayer.posX + " Y: " + (int)CoordsReply.mc.thePlayer.posY + " Z: " + (int)CoordsReply.mc.thePlayer.posZ);
                }
            }
        }
    }
}


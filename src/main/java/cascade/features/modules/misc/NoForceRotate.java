/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiDownloadTerrain
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketPlayerPosLook$EnumFlags
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.mixin.mixins.accessor.ISPacketPlayerPosLook;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoForceRotate
extends Module {
    public NoForceRotate() {
        super("NoForceRotate", Module.Category.MISC, "Ignores servers force rotation");
    }

    @SubscribeEvent
    public void onReceive(PacketEvent.Receive event) {
        if (NoForceRotate.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook && !(NoForceRotate.mc.currentScreen instanceof GuiDownloadTerrain)) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            ((ISPacketPlayerPosLook)packet).setYaw(NoForceRotate.mc.thePlayer.rotationYaw);
            ((ISPacketPlayerPosLook)packet).setPitch(NoForceRotate.mc.thePlayer.rotationPitch);
            packet.func_179834_f().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
            packet.func_179834_f().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
        }
    }
}


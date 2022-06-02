/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;

public class Quiver
extends Module {
    Setting<Integer> ticks = this.register(new Setting<Integer>("Ticks", 3, 0, 6));

    public Quiver() {
        super("Quiver", Module.Category.COMBAT, "Shoots urself with arrows");
    }

    @Override
    public void onUpdate() {
        if (Quiver.fullNullCheck() || this.shouldReturn()) {
            return;
        }
        if (Quiver.mc.thePlayer.func_184612_cw() >= this.ticks.getValue() && InventoryUtil.heldItem((Item)Items.bow, InventoryUtil.Hand.Both)) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Rotation(Quiver.mc.thePlayer.rotationYaw, -90.0f, true));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, Quiver.mc.thePlayer.func_174811_aO()));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(Quiver.mc.thePlayer.func_184600_cs()));
            Quiver.mc.thePlayer.func_184597_cx();
        }
    }

    boolean shouldReturn() {
        return Cascade.moduleManager.isModuleEnabled("FastProjectile") || Cascade.moduleManager.isModuleEnabled("BowSpam") || EntityUtil.isMoving() || !Quiver.mc.thePlayer.onGround;
    }
}


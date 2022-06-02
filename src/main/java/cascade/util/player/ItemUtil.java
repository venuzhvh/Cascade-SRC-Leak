/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 */
package cascade.util.player;

import cascade.util.Util;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class ItemUtil
implements Util {
    public static void silentSwap(int slot) {
        if (slot != -1) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(slot));
        }
    }

    public static void normalSwap(int slot) {
        if (slot != -1 && ItemUtil.mc.thePlayer.inventory.currentItem != slot) {
            ItemUtil.mc.thePlayer.inventory.currentItem = slot;
            ItemUtil.mc.playerController.updateController();
        }
    }

    public static int getItemFromHotbar(Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = ItemUtil.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack.getItem() != item) continue;
            slot = i;
        }
        return slot;
    }

    public static int getBlockFromHotbar(Block block) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (ItemUtil.mc.thePlayer.inventory.getStackInSlot(i).getItem() != Item.getItemFromBlock((Block)block)) continue;
            slot = i;
        }
        return slot;
    }
}


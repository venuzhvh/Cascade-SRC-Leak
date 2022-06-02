/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBeacon
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.tileentity.TileEntityHopper
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.player.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoInteract
extends Module {
    Setting<Boolean> pickaxe = this.register(new Setting<Boolean>("Pickaxe", false));

    public NoInteract() {
        super("NoInteract", Module.Category.MISC, "Prevents u from interacting with blocks");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (NoInteract.fullNullCheck() || this.isDisabled()) {
            return;
        }
        try {
            if (e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && !NoInteract.mc.thePlayer.isSneaking() && NoInteract.mc.gameSettings.keyBindUseItem.getIsKeyPressed() && (InventoryUtil.heldItem(Items.experience_bottle, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.golden_apple, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.field_185161_cS, InventoryUtil.Hand.Both) || InventoryUtil.heldItem((Item)Items.bow, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.writable_book, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.written_book, InventoryUtil.Hand.Both) || InventoryUtil.heldItem((Item)Items.potionitem, InventoryUtil.Hand.Both) || this.pickaxe.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_pickaxe, InventoryUtil.Hand.Main))) {
                for (TileEntity entity : NoInteract.mc.theWorld.loadedTileEntityList) {
                    if (!(entity instanceof TileEntityEnderChest) && !(entity instanceof TileEntityBeacon) && !(entity instanceof TileEntityFurnace) && !(entity instanceof TileEntityHopper) && !(entity instanceof TileEntityChest) || !NoInteract.mc.objectMouseOver.func_178782_a().equals((Object)entity.func_174877_v())) continue;
                    e.setCanceled(true);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
                if (NoInteract.mc.theWorld.func_180495_p(NoInteract.mc.objectMouseOver.func_178782_a()).func_177230_c() == Blocks.anvil) {
                    e.setCanceled(true);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                }
            }
        }
        catch (Exception ex) {
            Cascade.LOGGER.info("Caught an exception from NoInteract");
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onBlockInteract(PlayerInteractEvent.RightClickBlock e) {
        if (NoInteract.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if ((NoInteract.mc.theWorld.func_180495_p(e.getPos()).func_177230_c() == Blocks.anvil || NoInteract.mc.theWorld.func_180495_p(e.getPos()).func_177230_c() == Blocks.ender_chest) && !NoInteract.mc.thePlayer.isSneaking() && NoInteract.mc.gameSettings.keyBindUseItem.getIsKeyPressed() && (InventoryUtil.heldItem(Items.experience_bottle, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.golden_apple, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.field_185161_cS, InventoryUtil.Hand.Both) || InventoryUtil.heldItem((Item)Items.bow, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.writable_book, InventoryUtil.Hand.Both) || InventoryUtil.heldItem(Items.written_book, InventoryUtil.Hand.Both) || InventoryUtil.heldItem((Item)Items.potionitem, InventoryUtil.Hand.Both) || this.pickaxe.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_pickaxe, InventoryUtil.Hand.Main))) {
            e.setCanceled(true);
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }
}


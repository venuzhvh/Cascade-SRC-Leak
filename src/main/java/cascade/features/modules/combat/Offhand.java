/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.util.EnumHand
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.InventoryUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Offhand
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Crystal));
    Setting<Boolean> forceClose = this.register(new Setting<Boolean>("ForceClose", false));
    Setting<Float> totemHealth = this.register(new Setting<Object>("TotemHealth", Float.valueOf(20.1f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.mode.getValue() != Mode.Totem));
    Setting<Float> totemHoleHealth = this.register(new Setting<Object>("TotemHoleHealth", Float.valueOf(13.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.mode.getValue() != Mode.Totem));
    Setting<Float> fallDistance = this.register(new Setting<Object>("FallDistance", Float.valueOf(40.0f), Float.valueOf(0.1f), Float.valueOf(90.0f), v -> this.mode.getValue() != Mode.Totem));
    Setting<Boolean> totemElytra = this.register(new Setting<Object>("TotemElytra", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.Totem));
    Setting<Boolean> gapSword = this.register(new Setting<Object>("GapSword", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.GApple));
    Setting<Boolean> noWaste = this.register(new Setting<Object>("NoWaste", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.GApple && this.gapSword.getValue() != false));
    Setting<Boolean> forceGap = this.register(new Setting<Object>("ForceGap", Boolean.valueOf(true), v -> this.mode.getValue() != Mode.GApple && this.gapSword.getValue() != false));

    public Offhand() {
        super("Offhand", Module.Category.COMBAT, "AutoTotem and Offhand combined");
    }

    @Override
    public void onUpdate() {
        if (Offhand.fullNullCheck()) {
            return;
        }
        this.SwitchOffHandIfNeed();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (Offhand.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.noWaste.getValue().booleanValue() && this.gapSword.getValue().booleanValue() && this.mode.getValue() != Mode.GApple && e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
            CPacketPlayerTryUseItemOnBlock p = (CPacketPlayerTryUseItemOnBlock)e.getPacket();
            if (p.field_187027_c == EnumHand.OFF_HAND) {
                e.setCanceled(true);
            }
        }
    }

    private void SwitchOffHandIfNeed() {
        Item i = this.getItemType();
        if (Offhand.mc.thePlayer.func_184592_cb().getItem() != i) {
            int l_Slot;
            if (Offhand.mc.currentScreen instanceof GuiContainer || Offhand.mc.currentScreen instanceof GuiInventory) {
                if (this.forceClose.getValue().booleanValue()) {
                    Offhand.mc.thePlayer.closeScreen();
                } else {
                    return;
                }
            }
            if ((l_Slot = this.GetItemSlot(i)) != -1) {
                Offhand.mc.playerController.func_187098_a(Offhand.mc.thePlayer.inventoryContainer.windowId, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.thePlayer);
                Offhand.mc.playerController.func_187098_a(Offhand.mc.thePlayer.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.thePlayer);
                Offhand.mc.playerController.func_187098_a(Offhand.mc.thePlayer.inventoryContainer.windowId, l_Slot, 0, ClickType.PICKUP, (EntityPlayer)Offhand.mc.thePlayer);
                Offhand.mc.playerController.updateController();
            }
        }
    }

    public Item getItemType() {
        switch (this.mode.getValue()) {
            case Totem: {
                if (this.gapSword.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    return Items.golden_apple;
                }
                return Items.field_190929_cY;
            }
            case Crystal: {
                if (!(!(EntityUtil.getHealth((Entity)Offhand.mc.thePlayer) < this.totemHealth.getValue().floatValue()) || EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.thePlayer) || !this.forceGap.getValue().booleanValue() || this.gapSword.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed())) {
                    return Items.field_190929_cY;
                }
                if (EntityUtil.getHealth((Entity)Offhand.mc.thePlayer) < this.totemHoleHealth.getValue().floatValue() && EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.thePlayer) && this.forceGap.getValue().booleanValue() && (!this.gapSword.getValue().booleanValue() || !InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) || !Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed())) {
                    return Items.field_190929_cY;
                }
                if (!(!(Offhand.mc.thePlayer.motionY < 0.0) || !(Offhand.mc.thePlayer.fallDistance > this.fallDistance.getValue().floatValue()) || Offhand.mc.thePlayer.func_184613_cA() || !this.forceGap.getValue().booleanValue() || this.gapSword.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed())) {
                    return Items.field_190929_cY;
                }
                if (this.totemElytra.getValue().booleanValue() && Offhand.mc.thePlayer.func_184613_cA() && this.forceGap.getValue().booleanValue() && (!this.gapSword.getValue().booleanValue() || !InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) || !Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed())) {
                    return Items.field_190929_cY;
                }
                if (this.gapSword.getValue().booleanValue() && InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) && Offhand.mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    return Items.golden_apple;
                }
                return Items.field_185158_cP;
            }
            case GApple: {
                if (EntityUtil.getHealth((Entity)Offhand.mc.thePlayer) < this.totemHealth.getValue().floatValue() && !EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.thePlayer)) {
                    return Items.field_190929_cY;
                }
                if (EntityUtil.getHealth((Entity)Offhand.mc.thePlayer) < this.totemHoleHealth.getValue().floatValue() && EntityUtil.isPlayerSafe((EntityPlayer)Offhand.mc.thePlayer)) {
                    return Items.field_190929_cY;
                }
                if (Offhand.mc.thePlayer.motionY < 0.0 && Offhand.mc.thePlayer.fallDistance > this.fallDistance.getValue().floatValue() && !Offhand.mc.thePlayer.func_184613_cA()) {
                    return Items.field_190929_cY;
                }
                if (this.totemElytra.getValue().booleanValue() && Offhand.mc.thePlayer.func_184613_cA()) {
                    return Items.field_190929_cY;
                }
                return Items.golden_apple;
            }
        }
        return Items.field_190929_cY;
    }

    public int GetItemSlot(Item item) {
        if (Offhand.mc.thePlayer == null) {
            return 0;
        }
        for (int i = 0; i < Offhand.mc.thePlayer.inventoryContainer.getInventory().size(); ++i) {
            ItemStack s;
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8 || (s = (ItemStack)Offhand.mc.thePlayer.inventoryContainer.getInventory().get(i)).func_190926_b() || s.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    static enum Mode {
        Totem,
        Crystal,
        GApple;

    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.Timer;
import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Refill
extends Module {
    Setting<Integer> threshold = this.register(new Setting<Integer>("Threshold", 10, 0, 64));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 10, 0, 500));
    Timer timer = new Timer();
    ArrayList<Item> Hotbar = new ArrayList();

    public Refill() {
        super("Refill", Module.Category.PLAYER, "automatically refills ur hotbar");
    }

    @Override
    public void onEnable() {
        if (Refill.fullNullCheck()) {
            return;
        }
        this.Hotbar.clear();
        for (int l_I = 0; l_I < 9; ++l_I) {
            ItemStack l_Stack = Refill.mc.thePlayer.inventory.getStackInSlot(l_I);
            if (!l_Stack.func_190926_b() && !this.Hotbar.contains(l_Stack.getItem())) {
                this.Hotbar.add(l_Stack.getItem());
                continue;
            }
            this.Hotbar.add(Items.field_190931_a);
        }
    }

    @Override
    public void onUpdate() {
        if (Refill.mc.currentScreen != null || Refill.fullNullCheck()) {
            return;
        }
        if (!this.timer.passedMs(this.delay.getValue() * 1000)) {
            return;
        }
        for (int l_I = 0; l_I < 9; ++l_I) {
            if (!this.RefillSlotIfNeed(l_I)) continue;
            this.timer.reset();
            return;
        }
    }

    private boolean RefillSlotIfNeed(int p_Slot) {
        ItemStack l_Stack = Refill.mc.thePlayer.inventory.getStackInSlot(p_Slot);
        if (l_Stack.func_190926_b() || l_Stack.getItem() == Items.field_190931_a) {
            return false;
        }
        if (!l_Stack.isStackable()) {
            return false;
        }
        if (l_Stack.func_190916_E() >= l_Stack.getMaxStackSize()) {
            return false;
        }
        if ((l_Stack.getItem().equals(Items.golden_apple) || l_Stack.getItem().equals(Items.experience_bottle)) && l_Stack.func_190916_E() >= this.threshold.getValue()) {
            return false;
        }
        for (int l_I = 9; l_I < 36; ++l_I) {
            ItemStack l_Item = Refill.mc.thePlayer.inventory.getStackInSlot(l_I);
            if (l_Item.func_190926_b() || !this.CanItemBeMergedWith(l_Stack, l_Item)) continue;
            Refill.mc.playerController.func_187098_a(Refill.mc.thePlayer.inventoryContainer.windowId, l_I, 0, ClickType.QUICK_MOVE, (EntityPlayer)Refill.mc.thePlayer);
            Refill.mc.playerController.updateController();
            return true;
        }
        return false;
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target) {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getDisplayName().equals(p_Target.getDisplayName());
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.player.EntityPlayer
 */
package cascade.features.modules.misc;

import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.HashMap;
import net.minecraft.entity.player.EntityPlayer;

public class PopCounter
extends Module {
    public Setting<Boolean> clearOnLogout = this.register(new Setting<Boolean>("ClearOnLogout", false));
    public static HashMap<String, Integer> TotemPopContainer;
    private static PopCounter INSTANCE;

    public PopCounter() {
        super("PopCounter", Module.Category.MISC, "counts pops");
        INSTANCE = this;
    }

    public static PopCounter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PopCounter();
        }
        return INSTANCE;
    }

    public void onDeath(EntityPlayer player) {
        if (TotemPopContainer.containsKey(player.getCommandSenderName())) {
            int l_Count = TotemPopContainer.get(player.getCommandSenderName());
            TotemPopContainer.remove(player.getCommandSenderName());
            if (this.isOn()) {
                Command.sendMessage("\u00a7d" + player.getCommandSenderName() + ChatFormatting.AQUA + " died after popping their " + ChatFormatting.GREEN + l_Count + this.getPopString(l_Count) + ChatFormatting.AQUA + " Totem!");
            }
        }
    }

    public void onTotemPop(EntityPlayer player) {
        if (PopCounter.fullNullCheck()) {
            return;
        }
        int l_Count = 1;
        if (TotemPopContainer.containsKey(player.getCommandSenderName())) {
            l_Count = TotemPopContainer.get(player.getCommandSenderName());
            TotemPopContainer.put(player.getCommandSenderName(), ++l_Count);
        } else {
            TotemPopContainer.put(player.getCommandSenderName(), l_Count);
        }
        if (this.isOn()) {
            Command.sendMessage("\u00a7d" + player.getCommandSenderName() + ChatFormatting.AQUA + " popped their " + ChatFormatting.RED + l_Count + this.getPopString(l_Count) + ChatFormatting.AQUA + " Totem.");
        }
    }

    public String getPopString(int pops) {
        if (pops == 1) {
            return "st";
        }
        if (pops == 2) {
            return "nd";
        }
        if (pops == 3) {
            return "rd";
        }
        if (pops >= 4 && pops < 21) {
            return "th";
        }
        int lastDigit = pops % 10;
        if (lastDigit == 1) {
            return "st";
        }
        if (lastDigit == 2) {
            return "nd";
        }
        if (lastDigit == 3) {
            return "rd";
        }
        return "th";
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package cascade.manager;

import cascade.features.Feature;
import cascade.features.modules.misc.PopCounter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class PopManager
extends Feature {
    private Map<EntityPlayer, Integer> poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
    private Set<EntityPlayer> toAnnounce = new HashSet<EntityPlayer>();

    public void onUpdate() {
        if (PopCounter.getInstance().isEnabled()) {
            for (EntityPlayer player : this.toAnnounce) {
                if (player == null) continue;
                int playerNumber = 0;
                for (char character : player.getCommandSenderName().toCharArray()) {
                    playerNumber += character;
                    playerNumber *= 10;
                }
                this.sendMessage("\u00a7c" + player.getCommandSenderName() + " popped \u00a7a" + this.getTotemPops(player) + "\u00a7c Totem" + (this.getTotemPops(player) == 1 ? "" : "s") + ".", playerNumber);
                this.toAnnounce.remove(player);
                break;
            }
        }
    }

    public void onLogout() {
        this.clearList();
    }

    public void onTotemPop(EntityPlayer player) {
        this.popTotem(player);
        if (!player.equals((Object)PopManager.mc.thePlayer)) {
            this.toAnnounce.add(player);
        }
    }

    public void onDeath(EntityPlayer player) {
        if (this.getTotemPops(player) != 0 && !player.equals((Object)PopManager.mc.thePlayer) && PopCounter.getInstance().isEnabled()) {
            int playerNumber = 0;
            for (char character : player.getCommandSenderName().toCharArray()) {
                playerNumber += character;
                playerNumber *= 10;
            }
            this.sendMessage("\u00a7c" + player.getCommandSenderName() + " died after popping \u00a7a" + this.getTotemPops(player) + "\u00a7c Totem" + (this.getTotemPops(player) == 1 ? "" : "s") + ".", playerNumber);
            this.toAnnounce.remove(player);
        }
        this.resetPops(player);
    }

    public void onLogout(EntityPlayer player, boolean clearOnLogout) {
        if (clearOnLogout) {
            this.resetPops(player);
        }
    }

    public void clearList() {
        this.poplist = new ConcurrentHashMap<EntityPlayer, Integer>();
    }

    public void resetPops(EntityPlayer player) {
        this.setTotemPops(player, 0);
    }

    public void popTotem(EntityPlayer player) {
        this.poplist.merge(player, 1, Integer::sum);
    }

    public void setTotemPops(EntityPlayer player, int amount) {
        this.poplist.put(player, amount);
    }

    public int getTotemPops(EntityPlayer player) {
        Integer pops = this.poplist.get(player);
        if (pops == null) {
            return 0;
        }
        return pops;
    }

    public String getTotemPopString(EntityPlayer player) {
        return "\u00a7f" + (this.getTotemPops(player) <= 0 ? "" : "-" + this.getTotemPops(player) + " ");
    }

    void sendMessage(String message, int id) {
        TextComponentString component = new TextComponentString(message);
        PopManager.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)component, id);
    }
}


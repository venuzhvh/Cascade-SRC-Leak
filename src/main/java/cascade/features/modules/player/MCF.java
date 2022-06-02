/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 */
package cascade.features.modules.player;

import cascade.Cascade;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.Timer;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

public class MCF
extends Module {
    public Setting<Boolean> hold = this.register(new Setting<Boolean>("Hold", false));
    public Setting<Integer> holdFor = this.register(new Setting<Object>("HoldFor", Integer.valueOf(500), Integer.valueOf(100), Integer.valueOf(1000), v -> this.hold.getValue()));
    private boolean clicked = false;
    Timer timer = new Timer();

    public MCF() {
        super("MCF ", Module.Category.PLAYER, "Middle Click Friend");
    }

    @Override
    public void onUpdate() {
        if (MCF.mc.gameSettings.keyBindPickBlock.getIsKeyPressed()) {
            if (!this.clicked && MCF.mc.currentScreen == null) {
                if (this.hold.getValue().booleanValue()) {
                    if (this.timer.passedMs(this.holdFor.getValue().intValue())) {
                        this.onClick();
                        this.timer.reset();
                    }
                } else {
                    this.onClick();
                }
            }
            this.clicked = true;
        } else {
            this.clicked = false;
        }
    }

    private void onClick() {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            if (Cascade.friendManager.isFriend(entity.getCommandSenderName())) {
                Cascade.friendManager.removeFriend(entity.getCommandSenderName());
                Command.sendMessage(ChatFormatting.BOLD + "" + ChatFormatting.RED + entity.getCommandSenderName() + " has been removed from friends", true, false);
            } else {
                Cascade.friendManager.addFriend(entity.getCommandSenderName());
                Command.sendMessage(ChatFormatting.BOLD + "" + ChatFormatting.GREEN + entity.getCommandSenderName() + " has been added to friends", true, false);
            }
        }
        this.clicked = true;
    }
}


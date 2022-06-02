/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.features.command.commands;

import cascade.Cascade;
import cascade.features.command.Command;
import com.mojang.realmsclient.gui.ChatFormatting;

public class RemoveCommand
extends Command {
    public RemoveCommand() {
        super("remove", new String[]{"<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length >= 2) {
            Cascade.friendManager.removeFriend(commands[1]);
            RemoveCommand.sendMessage(ChatFormatting.BOLD + "" + ChatFormatting.RED + commands[1] + " has been removed from friends", true, false);
            return;
        }
    }
}


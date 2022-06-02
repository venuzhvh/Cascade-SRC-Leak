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

public class AddCommand
extends Command {
    public AddCommand() {
        super("add", new String[]{"<name>"});
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length >= 2) {
            Cascade.friendManager.addFriend(commands[1]);
            AddCommand.sendMessage(ChatFormatting.BOLD + "" + ChatFormatting.GREEN + commands[1] + " has been added to friends", true, false);
            return;
        }
    }
}


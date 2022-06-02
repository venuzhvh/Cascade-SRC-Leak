/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 */
package cascade.features.command.commands;

import cascade.Cascade;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import com.mojang.realmsclient.gui.ChatFormatting;

public class ModuleCommand
extends Command {
    public ModuleCommand() {
        super("module", new String[0]);
    }

    @Override
    public void execute(String[] commands) {
        if (commands.length == 1) {
            for (Module.Category category : Cascade.moduleManager.getCategories()) {
                String modules = category.getName() + ": ";
                for (Module module : Cascade.moduleManager.getModulesByCategory(category)) {
                    modules = modules + (module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + ChatFormatting.RESET + ", ";
                }
                Command.sendMessage(modules, true, false);
            }
            return;
        }
    }
}


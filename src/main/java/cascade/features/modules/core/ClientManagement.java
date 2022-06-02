/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.Display
 */
package cascade.features.modules.core;

import cascade.Cascade;
import cascade.event.events.ClientEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.core.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

public class ClientManagement
extends Module {
    Setting<Page> page = this.register(new Setting<Page>("Page", Page.Chat));
    public Setting<String> prefix = this.register(new Setting<Object>("Prefix", ".", v -> this.page.getValue() == Page.Client));
    public Setting<Boolean> noPacketKick = this.register(new Setting<Object>("NoPacketKick", Boolean.valueOf(true), v -> this.page.getValue() == Page.Client));
    public Setting<Boolean> unfocusedCPU = this.register(new Setting<Object>("UnfocusedCPU", Boolean.valueOf(true), v -> this.page.getValue() == Page.Client));
    public Setting<Integer> cpuFPS = this.register(new Setting<Object>("FPS", Integer.valueOf(60), Integer.valueOf(1), Integer.valueOf(144), v -> this.page.getValue() == Page.Client && this.unfocusedCPU.getValue() != false));
    Setting<Boolean> customTitle = this.register(new Setting<Object>("CustomTitle", Boolean.valueOf(false), v -> this.page.getValue() == Page.Client));
    public Setting<String> title = this.register(new Setting<Object>("Title", "Casacde 1.12.2", v -> this.page.getValue() == Page.Client && this.customTitle.getValue() != false));
    public Setting<Boolean> toggleName = this.register(new Setting<Object>("ToggleName", Boolean.valueOf(false), v -> this.page.getValue() == Page.Chat));
    public Setting<String> name = this.register(new Setting<Object>("Name", "Cascade", v -> this.page.getValue() == Page.Chat));
    Setting<TextUtil.Color> bracketColor = this.register(new Setting<Object>("BracketColor", (Object)TextUtil.Color.BLUE, v -> this.page.getValue() == Page.Chat));
    Setting<TextUtil.Color> nameColor = this.register(new Setting<Object>("NameColor", (Object)TextUtil.Color.BLUE, v -> this.page.getValue() == Page.Chat));
    Setting<String> lBracket = this.register(new Setting<Object>("LBracket", "[", v -> this.page.getValue() == Page.Chat));
    Setting<String> rBracket = this.register(new Setting<Object>("RBracket", "]", v -> this.page.getValue() == Page.Chat));
    private static ClientManagement INSTANCE = new ClientManagement();

    public ClientManagement() {
        super("ClientManagement", Module.Category.CORE, "Manages the client");
        INSTANCE = this;
    }

    public static ClientManagement getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientManagement();
        }
        return INSTANCE;
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent e) {
        if (e.getStage() == 2 && e.getSetting().getFeature().equals(this) && e.getSetting() == this.prefix) {
            Cascade.chatManager.setPrefix(this.prefix.getPlannedValue());
            Command.sendMessage("Prefix set to " + Cascade.chatManager.getPrefix(), true, false);
        }
        if (e.getStage() == 2 && this.equals(e.getSetting().getFeature())) {
            Cascade.chatManager.setClientMessage(this.getCommandMessage());
        }
    }

    @Override
    public void onLoad() {
        Cascade.chatManager.setClientMessage(this.getCommandMessage());
        Cascade.chatManager.setPrefix(this.prefix.getValue());
    }

    public String getCommandMessage() {
        return TextUtil.coloredString(this.lBracket.getValue(), this.bracketColor.getValue()) + TextUtil.coloredString(this.name.getValue(), this.nameColor.getValue()) + TextUtil.coloredString(this.rBracket.getValue(), this.bracketColor.getValue());
    }

    @Override
    public void onUpdate() {
        if (this.customTitle.getValue().booleanValue()) {
            Display.setTitle((String)this.title.getValueAsString());
        }
    }

    static enum Page {
        Chat,
        Client;

    }
}


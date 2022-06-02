/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.Cascade;
import cascade.event.events.DeathEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShitTalker
extends Module {
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 250, 0, 1000));
    Setting<Boolean> reload = this.register(new Setting<Boolean>("Reload", false));
    List<String> messages = new ArrayList<String>();
    Timer timer = new Timer();

    public ShitTalker() {
        super("ShitTalker ", Module.Category.MISC, "autoez");
        File file = new File("cascade/shittalker.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from ShitTalker");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        this.loadMessages();
        this.timer.reset();
    }

    @Override
    public void onUpdate() {
        if (this.reload.getValue().booleanValue()) {
            this.loadMessages();
            this.reload.setValue(false);
        }
    }

    @SubscribeEvent
    public void onDeath(DeathEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.player != ShitTalker.mc.thePlayer && !Cascade.friendManager.isFriend(e.player) && this.timer.passedMs(this.delay.getValue().intValue())) {
            this.announceDeath(e.player);
            this.timer.reset();
        }
    }

    public void loadMessages() {
        this.messages = this.readTextFileAllLines("cascade/shittalker.txt");
    }

    public String getRandomMessage() {
        this.loadMessages();
        Random rand = new Random();
        if (this.messages.size() == 0) {
            return "LOL";
        }
        if (this.messages.size() == 1) {
            return this.messages.get(0);
        }
        return this.messages.get(MathUtil.clamp(rand.nextInt(this.messages.size()), 0, this.messages.size() - 1));
    }

    public void announceDeath(EntityPlayer target) {
        mc.getNetHandler().addToSendQueue((Packet)new CPacketChatMessage(this.getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
    }

    List<String> readTextFileAllLines(String file) {
        try {
            Path path = Paths.get(file, new String[0]);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to read file, creating new file: " + file);
            this.appendTextFile("", file);
            return Collections.emptyList();
        }
    }

    boolean appendTextFile(String data, String file) {
        try {
            Path path = Paths.get(file, new String[0]);
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path, new LinkOption[0]) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        }
        catch (IOException e) {
            System.out.println("WARNING: Unable to write file: " + file);
            return false;
        }
        return true;
    }
}


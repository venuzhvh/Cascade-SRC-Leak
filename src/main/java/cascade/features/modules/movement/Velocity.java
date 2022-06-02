/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.network.play.server.SPacketEntityVelocity
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.PacketEvent;
import cascade.event.events.PushEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity
extends Module {
    public Setting<Boolean> knockBack = this.register(new Setting<Boolean>("KnockBack", true));
    public Setting<Boolean> noPush = this.register(new Setting<Boolean>("NoPush", true));
    public Setting<Boolean> bobbers = this.register(new Setting<Boolean>("Bobbers", true));
    public Setting<Boolean> water = this.register(new Setting<Boolean>("Water", false));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", false));
    private static Velocity INSTANCE;

    public Velocity() {
        super("Velocity", Module.Category.MOVEMENT, "Player Tweaks");
        INSTANCE = this;
    }

    public static Velocity getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Velocity();
        }
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        Blocks.ice.slipperiness = 0.98f;
        Blocks.packed_ice.slipperiness = 0.98f;
        Blocks.field_185778_de.slipperiness = 0.98f;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getStage() == 0 && Velocity.mc.thePlayer != null) {
            Entity entity;
            SPacketEntityStatus packet;
            SPacketEntityVelocity velocity;
            if (this.knockBack.getValue().booleanValue() && e.getPacket() instanceof SPacketEntityVelocity && (velocity = (SPacketEntityVelocity)e.getPacket()).func_149412_c() == Velocity.mc.thePlayer.entityId) {
                e.setCanceled(true);
            }
            if (e.getPacket() instanceof SPacketEntityStatus && this.bobbers.getValue().booleanValue() && (packet = (SPacketEntityStatus)e.getPacket()).func_149160_c() == 31 && (entity = packet.func_149161_a((World)Velocity.mc.theWorld)) instanceof EntityFishHook) {
                EntityFishHook fishHook = (EntityFishHook)entity;
                if (fishHook.caughtEntity == Velocity.mc.thePlayer) {
                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getStage() == 0 && this.noPush.getValue().booleanValue() && e.entity == Velocity.mc.thePlayer) {
            e.setCanceled(true);
        } else if (e.getStage() == 1 && this.blocks.getValue().booleanValue()) {
            e.setCanceled(true);
        } else if (e.getStage() == 2 && this.water.getValue().booleanValue() && Velocity.mc.thePlayer != null && Velocity.mc.thePlayer == e.entity) {
            e.setCanceled(true);
        }
    }
}


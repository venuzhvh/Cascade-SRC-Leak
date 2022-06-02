/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.features.modules.player.Freecam;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ITimer;
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastSwim
extends Module {
    Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Double> whorizontal = this.register(new Setting<Double>("WaterHorizontal", 2.0, 0.0, 20.0));
    Setting<Double> wup = this.register(new Setting<Double>("WaterUp", 2.0, 0.0, 20.0));
    Setting<Double> wdown = this.register(new Setting<Double>("WaterDown", 2.0, 0.0, 20.0));
    Setting<Double> lhorizontal = this.register(new Setting<Double>("LavaHorizontal", 1.0, 0.0, 20.0));
    Setting<Double> lup = this.register(new Setting<Double>("LavaUp", 1.0, 0.0, 20.0));
    Setting<Double> ldown = this.register(new Setting<Double>("LavaDown", 1.0, 0.0, 20.0));
    Setting<Boolean> kbBoost = this.register(new Setting<Boolean>("KbBoost", true));
    Setting<Float> factor = this.register(new Setting<Object>("Factor", Float.valueOf(16.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.kbBoost.getValue()));
    Setting<Double> range = this.register(new Setting<Object>("Range", Double.valueOf(6.0), Double.valueOf(0.0), Double.valueOf(20.0), v -> this.kbBoost.getValue()));

    public FastSwim() {
        super("FastSwim", Module.Category.MOVEMENT, "Makes u go faster in liquids");
    }

    @Override
    public void onDisable() {
        if (this.useTimer.getValue().booleanValue() && ((ITimer)FastSwim.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (Freecam.getInstance().isEnabled()) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (EntityUtil.isInLiquid() && !Cascade.moduleManager.isModuleEnabled("FastMotion")) {
            if (this.useTimer.getValue().booleanValue()) {
                Cascade.timerManager.set(1.0888f);
            }
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)FastSwim.mc.thePlayer, CPacketEntityAction.Action.START_SPRINTING));
            if (FastSwim.mc.thePlayer.func_180799_ab()) {
                if (MovementUtil.isMoving()) {
                    if (this.shouldBoost()) {
                        e.setX(e.getX() * (double)this.factor.getValue().floatValue() * 5.0);
                        e.setZ(e.getZ() * (double)this.factor.getValue().floatValue() * 5.0);
                    } else {
                        e.setX(e.getX() * this.lhorizontal.getValue());
                        e.setZ(e.getZ() * this.lhorizontal.getValue());
                    }
                }
                if (FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed() && !FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
                    e.setY(e.getY() * this.lup.getValue());
                }
                if (FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed() && !FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                    e.setY(e.getY() * this.ldown.getValue());
                }
                if (FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed() && FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                    e.setY(0.0);
                }
            } else {
                if (MovementUtil.isMoving()) {
                    e.setX(e.getX() * this.whorizontal.getValue() / 2.0);
                    e.setZ(e.getZ() * this.whorizontal.getValue() / 2.0);
                }
                if (FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed() && !FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
                    e.setY(e.getY() * this.wup.getValue());
                }
                if (FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed() && !FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                    e.setY(e.getY() * this.wdown.getValue());
                }
                if (FastSwim.mc.gameSettings.keyBindSneak.getIsKeyPressed() && FastSwim.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
                    e.setY(0.0);
                }
            }
        }
    }

    boolean shouldBoost() {
        return this.kbBoost.getValue() != false && Cascade.packetManager.caughtPExplosion() && FastSwim.mc.thePlayer.getDistance(Cascade.packetManager.pExplosion().func_149148_f(), Cascade.packetManager.pExplosion().func_149143_g(), Cascade.packetManager.pExplosion().func_149145_h()) <= this.range.getValue() && !FastSwim.mc.thePlayer.onGround;
    }
}


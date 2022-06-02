/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.ItemElytra
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.PlayerUtil;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ElytraFly
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Control));
    Setting<Double> speed = this.register(new Setting<Double>("Speed", 10.0, 0.1, 100.0));
    Setting<Boolean> infDura = this.register(new Setting<Object>("InfDurability", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Packet));
    Setting<Boolean> ncp = this.register(new Setting<Object>("NCP", Boolean.valueOf(false), v -> this.mode.getValue() == Mode.Packet));
    Setting<Boolean> accel = this.register(new Setting<Object>("Acceleration", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Packet));
    Setting<Boolean> autoStart = this.register(new Setting<Boolean>("AutoStart", false));
    Setting<TimerMode> timerMode = this.register(new Setting<Object>("Timer", (Object)TimerMode.None, v -> this.autoStart.getValue()));
    Setting<Integer> delay = this.register(new Setting<Object>("Delay", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> this.autoStart.getValue()));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", false));
    Setting<Boolean> noGround = this.register(new Setting<Boolean>("NoGround", true));
    Timer timer = new Timer();
    boolean lag;

    public ElytraFly() {
        super("ElytraFly", Module.Category.MOVEMENT, "Elyta flight?");
    }

    @Override
    public void onToggle() {
        if (this.timerMode.getValue() != TimerMode.None) {
            Cascade.timerManager.reset();
        }
        this.timer.reset();
        this.lag = false;
    }

    @SubscribeEvent
    void onMove(MoveEvent e) {
        if (this.isDisabled()) {
            return;
        }
        Random RANDOM = new Random();
        ItemStack stack = ElytraFly.mc.thePlayer.func_184582_a(EntityEquipmentSlot.CHEST);
        if (!PlayerUtil.isElytraEquipped() || !ItemElytra.func_185069_d((ItemStack)stack)) {
            return;
        }
        if (ElytraFly.mc.thePlayer.func_184613_cA() && (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid() || this.noGround.getValue().booleanValue() && ElytraFly.mc.thePlayer.onGround)) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
            return;
        }
        if (this.autoStart.getValue().booleanValue() && ElytraFly.mc.gameSettings.keyBindJump.getIsKeyPressed() && !ElytraFly.mc.thePlayer.func_184613_cA() && ElytraFly.mc.thePlayer.motionY < 0.0) {
            if (this.timerMode.getValue() != TimerMode.None) {
                if (this.timerMode.getValue() == TimerMode.Slow) {
                    Cascade.timerManager.set(0.42f);
                }
                if (this.timerMode.getValue() == TimerMode.DSlow) {
                    Cascade.timerManager.set(0.17f);
                }
                if (this.timer.passedMs(this.delay.getValue().intValue())) {
                    ElytraFly.mc.gameSettings.keyBindJump.pressed = true;
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
                    this.timer.reset();
                } else {
                    ElytraFly.mc.gameSettings.keyBindJump.pressed = false;
                }
                return;
            }
            if (this.infDura.getValue().booleanValue() && ElytraFly.mc.thePlayer.func_184613_cA()) {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
            }
        }
        switch (this.mode.getValue()) {
            case Control: {
                if (!ElytraFly.mc.thePlayer.func_184613_cA()) break;
                if (!ElytraFly.mc.thePlayer.movementInput.field_187255_c && !ElytraFly.mc.thePlayer.movementInput.sneak) {
                    ElytraFly.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                    break;
                }
                if (!ElytraFly.mc.thePlayer.movementInput.field_187255_c) break;
                float yaw = (float)Math.toRadians(ElytraFly.mc.thePlayer.rotationYaw);
                double sped = this.speed.getValue() / 10.0;
                ElytraFly.mc.thePlayer.motionX = (double)MathHelper.sin((float)yaw) * -sped;
                ElytraFly.mc.thePlayer.motionZ = (double)MathHelper.cos((float)yaw) * sped;
                break;
            }
            case Packet: {
                if (!ElytraFly.mc.thePlayer.onGround || !this.noGround.getValue().booleanValue()) {
                    double sped = 0.0;
                    if (this.accel.getValue().booleanValue()) {
                        if (this.lag) {
                            sped = 1.0;
                            this.lag = false;
                        }
                        if (sped < this.speed.getValue()) {
                            sped += 0.1;
                        }
                        if (sped - 0.1 > this.speed.getValue()) {
                            sped -= 0.1;
                        }
                    } else {
                        sped = this.speed.getValue();
                    }
                    if (this.ncp.getValue().booleanValue()) {
                        if (ElytraFly.mc.thePlayer.ticksExisted % 32 != 0 || this.lag || !(Math.abs(e.getX()) >= 0.05) && !(Math.abs(e.getZ()) >= 0.05)) {
                            ElytraFly.mc.thePlayer.motionY = -2.0E-4;
                            e.setY(-2.0E-4);
                        } else {
                            sped -= sped / 2.0 * 0.1;
                            ElytraFly.mc.thePlayer.motionY = -2.0E-4;
                            e.setY(0.006200000000000001);
                        }
                    } else {
                        ElytraFly.mc.thePlayer.motionY = 0.0;
                        e.setY(0.0);
                    }
                    e.setX(e.getX() * (this.lag ? 0.5 : sped));
                    e.setZ(e.getZ() * (this.lag ? 0.5 : sped));
                }
                boolean falling = false;
                if (this.infDura.getValue().booleanValue() || !ElytraFly.mc.thePlayer.func_184613_cA()) {
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.thePlayer, CPacketEntityAction.Action.START_FALL_FLYING));
                    falling = true;
                }
                if (this.ncp.getValue().booleanValue() && !this.lag && (Math.abs(e.getX()) >= 0.05 || Math.abs(e.getZ()) >= 0.05)) {
                    double y = 1.0E-8 + 1.0E-8 * (1.0 + (double)RANDOM.nextInt(1 + (RANDOM.nextBoolean() ? RANDOM.nextInt(34) : RANDOM.nextInt(43))));
                    if (ElytraFly.mc.thePlayer.onGround || ElytraFly.mc.thePlayer.ticksExisted % 2 == 0) {
                        e.setY(e.getY() + y);
                        return;
                    }
                    e.setY(e.getY() - y);
                    return;
                }
                if (!falling) break;
                return;
            }
            case Boost: {
                if (!ElytraFly.mc.thePlayer.movementInput.jump || !ElytraFly.mc.thePlayer.func_184613_cA()) break;
                float yaw = ElytraFly.mc.thePlayer.rotationYaw * ((float)Math.PI / 180);
                ElytraFly.mc.thePlayer.motionX -= (double)(MathHelper.sin((float)yaw) * 0.15f);
                ElytraFly.mc.thePlayer.motionZ += (double)(MathHelper.cos((float)yaw) * 0.15f);
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook && this.mode.getValue() == Mode.Packet && PlayerUtil.isElytraEquipped()) {
            this.lag = true;
        }
    }

    static enum TimerMode {
        None,
        Slow,
        DSlow;

    }

    static enum Mode {
        Control,
        Packet,
        Boost;

    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.event.events.PacketEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ITimer;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LongJump
extends Module {
    Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    Setting<Float> factor = this.register(new Setting<Float>("Factor", Float.valueOf(4.6f), Float.valueOf(0.0f), Float.valueOf(100.0f)));
    Setting<Boolean> lowHop = this.register(new Setting<Boolean>("LowHop", false));
    Setting<Boolean> lagDisable = this.register(new Setting<Boolean>("LagDisable", true));
    int groundTicks;
    double distance;
    double speed;
    int airTicks;
    int stage;

    public LongJump() {
        super("LongJump", Module.Category.MOVEMENT, "Long jump dude");
    }

    @Override
    public void onEnable() {
        if (LongJump.mc.thePlayer != null) {
            this.distance = MovementUtil.getDistance2D();
            this.speed = MovementUtil.getSpeed();
        }
        this.groundTicks = 0;
        this.airTicks = 0;
        this.stage = 0;
    }

    @Override
    public void onDisable() {
        if (LongJump.fullNullCheck()) {
            return;
        }
        if (this.step.getValue().booleanValue()) {
            LongJump.mc.thePlayer.stepHeight = 0.6f;
        }
        if (((ITimer)LongJump.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.isDisabled() || e.isCanceled()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (LongJump.mc.thePlayer != mc.func_175606_aa()) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (this.useTimer.getValue().booleanValue()) {
            Cascade.timerManager.set(1.0888f);
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(2.0f);
        }
        if (LongJump.mc.thePlayer.moveStrafing <= 0.0f && LongJump.mc.thePlayer.field_191988_bg <= 0.0f) {
            this.stage = 1;
        }
        if (MathUtil.round(LongJump.mc.thePlayer.posY - (double)((int)LongJump.mc.thePlayer.posY), 3) == MathUtil.round(0.943, 3)) {
            LongJump.mc.thePlayer.motionY -= 0.03;
            e.setY(e.getY() - 0.03);
        }
        if (this.stage == 1 && MovementUtil.isMoving()) {
            this.stage = 2;
            this.speed = (double)this.factor.getValue().floatValue() * MovementUtil.getSpeed() - 0.01;
        } else if (this.stage == 2) {
            this.stage = 3;
            if (!EntityUtil.isInLiquid() && !LongJump.mc.thePlayer.isInWeb && LongJump.mc.thePlayer.onGround) {
                if (!this.lowHop.getValue().booleanValue()) {
                    LongJump.mc.thePlayer.motionY = 0.424;
                }
                e.setY(0.424);
            }
            this.speed *= 2.149802;
        } else if (this.stage == 3) {
            this.stage = 4;
            double difference = 0.66 * (this.distance - MovementUtil.getSpeed());
            this.speed = this.distance - difference;
        } else {
            if (LongJump.mc.theWorld.func_184144_a((Entity)LongJump.mc.thePlayer, LongJump.mc.thePlayer.func_174813_aQ().offset(0.0, LongJump.mc.thePlayer.motionY, 0.0)).size() > 0 || LongJump.mc.thePlayer.isCollidedVertically) {
                this.stage = 1;
            }
            this.speed = this.distance - this.distance / 159.0;
        }
        this.speed = Math.max(this.speed, MovementUtil.getSpeed());
        MovementUtil.strafe(e, this.speed);
        float moveForward = LongJump.mc.thePlayer.movementInput.field_192832_b;
        float moveStrafe = LongJump.mc.thePlayer.movementInput.moveStrafe;
        float rotationYaw = LongJump.mc.thePlayer.rotationYaw;
        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            e.setX(0.0);
            e.setZ(0.0);
        } else if (moveForward != 0.0f) {
            if (moveStrafe >= 1.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
                moveStrafe = 0.0f;
            } else if (moveStrafe <= -1.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
                moveStrafe = 0.0f;
            }
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
        double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
        e.setX((double)moveForward * this.speed * cos + (double)moveStrafe * this.speed * sin);
        e.setZ((double)moveForward * this.speed * sin - (double)moveStrafe * this.speed * cos);
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getStage() == 0) {
            this.distance = MovementUtil.getDistance2D();
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.groundTicks = 0;
            this.speed = 0.0;
            this.airTicks = 0;
            this.stage = 0;
            if (this.lagDisable.getValue().booleanValue()) {
                this.disable();
                return;
            }
        }
    }
}


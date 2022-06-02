/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class YPort
extends Module {
    Setting<Timer> timer = this.register(new Setting<Timer>("Timer", Timer.Strict));
    Setting<Double> yPortSpeed = this.register(new Setting<Double>("Speed", 0.1, 0.0, 1.0));
    cascade.util.misc.Timer lagTimer = new cascade.util.misc.Timer();
    boolean lagging;
    private static YPort INSTANCE;

    public YPort() {
        super("YPort", Module.Category.MOVEMENT, "OnGround exploit");
        INSTANCE = this;
    }

    public static YPort getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YPort();
        }
        return INSTANCE;
    }

    @Override
    public void onDisable() {
        if (YPort.fullNullCheck()) {
            return;
        }
        YPort.mc.thePlayer.stepHeight = 0.6f;
        if (this.timer.getValue() != Timer.None) {
            Cascade.timerManager.reset();
        }
        this.lagging = false;
        this.lagTimer.reset();
    }

    @Override
    public void onUpdate() {
        if (YPort.fullNullCheck()) {
            return;
        }
        if (this.lagging && this.lagTimer.passedMs(200L)) {
            this.lagging = false;
            this.lagTimer.reset();
        }
        if (YPort.mc.thePlayer.isSneaking() || EntityUtil.isInLiquid() || YPort.mc.thePlayer.isOnLadder() || Cascade.moduleManager.isModuleEnabled("Strafe")) {
            return;
        }
        this.handleYPortSpeed();
        if (!YPort.mc.thePlayer.isOnLadder() || YPort.mc.thePlayer.isInWater() || YPort.mc.thePlayer.func_180799_ab()) {
            YPort.mc.thePlayer.stepHeight = 2.0f;
            return;
        }
    }

    void handleYPortSpeed() {
        if (!MovementUtil.isMoving() || YPort.mc.thePlayer.isCollidedHorizontally) {
            return;
        }
        if (YPort.mc.thePlayer.onGround) {
            if (this.timer.getValue() != Timer.None) {
                Cascade.timerManager.set(this.timer.getValue() == Timer.Fast ? 1.15f : 1.0888f);
            }
            YPort.mc.thePlayer.motionY = 0.3999 + MovementUtil.getJumpSpeed();
            double[] dir = MovementUtil.forward(MovementUtil.getSpeed() + this.yPortSpeed.getValue());
            MovementUtil.setMotion(dir[0], 0.0, dir[1]);
        } else {
            RayTraceResult trace = YPort.mc.theWorld.rayTraceBlocks(YPort.mc.thePlayer.func_174791_d(), new Vec3d(YPort.mc.thePlayer.posX, YPort.mc.thePlayer.posY - 5.0, YPort.mc.thePlayer.posZ), false, false, false);
            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK && !this.lagging) {
                YPort.mc.thePlayer.motionY = -4.0;
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (YPort.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.lagging = true;
        }
    }

    static enum Timer {
        Strict,
        Fast,
        None;

    }
}


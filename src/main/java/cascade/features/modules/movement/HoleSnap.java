/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ITimer;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.HoleUtil;
import cascade.util.player.MovementUtil;
import cascade.util.player.RotationUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleSnap
extends Module {
    public Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(3.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Float> factor = this.register(new Setting<Float>("Factor", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(15.0f)));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));
    Timer timer = new Timer();
    HoleUtil.Hole holes;

    public HoleSnap() {
        super("HoleSnap", Module.Category.MOVEMENT, "drags u to the nearest hole");
    }

    @Override
    public void onEnable() {
        if (HoleSnap.fullNullCheck()) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }

    @Override
    public void onDisable() {
        if (HoleSnap.fullNullCheck()) {
            return;
        }
        this.timer.reset();
        this.holes = null;
        if (this.step.getValue().booleanValue()) {
            HoleSnap.mc.thePlayer.stepHeight = 0.6f;
        }
        if (((ITimer)HoleSnap.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (HoleSnap.fullNullCheck()) {
            return;
        }
        if (EntityUtil.isInLiquid()) {
            this.disable();
            return;
        }
        this.holes = RotationUtil.getTargetHoleVec3D(this.range.getValue().floatValue());
        if (this.holes == null) {
            Command.sendMessage("Unable to find hole, disabling HoleSnap");
            this.disable();
            return;
        }
        if (this.timer.passedMs(500L)) {
            this.disable();
            return;
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        Cascade.timerManager.set(this.factor.getValue().floatValue());
        if (HoleUtil.isObbyHole(RotationUtil.getPlayerPos()) || HoleUtil.isBedrockHoles(RotationUtil.getPlayerPos())) {
            this.disable();
            return;
        }
        if (HoleSnap.mc.theWorld.func_180495_p(this.holes.pos1).func_177230_c() != Blocks.air) {
            this.disable();
            return;
        }
        BlockPos it = this.holes.pos1;
        Vec3d playerPos = HoleSnap.mc.thePlayer.func_174791_d();
        Vec3d targetPos = new Vec3d((double)it.func_177958_n() + 0.5, HoleSnap.mc.thePlayer.posY, (double)it.func_177952_p() + 0.5);
        double yawRad = Math.toRadians(RotationUtil.getRotationTo((Vec3d)playerPos, (Vec3d)targetPos).field_189982_i);
        double dist = playerPos.distanceTo(targetPos);
        double speed = HoleSnap.mc.thePlayer.onGround ? -Math.min(0.2805, dist / 2.0) : -EntityUtil.getMaxSpeed() + 0.02;
        HoleSnap.mc.thePlayer.motionX = -Math.sin(yawRad) * speed;
        HoleSnap.mc.thePlayer.motionZ = Math.cos(yawRad) * speed;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
            return;
        }
    }
}


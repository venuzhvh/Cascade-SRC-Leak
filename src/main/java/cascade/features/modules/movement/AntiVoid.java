/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class AntiVoid
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.MotionStop));
    Setting<Integer> distance = this.register(new Setting<Integer>("Distance", 10, 1, 256));
    Setting<Integer> height = this.register(new Setting<Object>("Height", Integer.valueOf(4), Integer.valueOf(0), Integer.valueOf(10), v -> this.mode.getValue() == Mode.Packet));
    Setting<Float> speed = this.register(new Setting<Object>("Speed", Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.Motion || this.mode.getValue() == Mode.Glide));
    Setting<Float> timer = this.register(new Setting<Object>("Timer", Float.valueOf(8.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), v -> this.mode.getValue() == Mode.Timer));

    public AntiVoid() {
        super("AntiVoid", Module.Category.MOVEMENT, "Prevents u from falling in the void");
    }

    @Override
    public void onToggle() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (this.mode.getValue() == Mode.Timer) {
            Cascade.timerManager.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (AntiVoid.fullNullCheck()) {
            return;
        }
        if (AntiVoid.mc.thePlayer.noClip || AntiVoid.mc.thePlayer.posY > (double)this.distance.getValue().intValue() || AntiVoid.mc.thePlayer.func_184218_aH()) {
            return;
        }
        RayTraceResult trace = AntiVoid.mc.theWorld.rayTraceBlocks(AntiVoid.mc.thePlayer.func_174791_d(), new Vec3d(AntiVoid.mc.thePlayer.posX, 0.0, AntiVoid.mc.thePlayer.posZ), false, false, false);
        if (trace == null || trace.typeOfHit != RayTraceResult.Type.BLOCK) {
            switch (this.mode.getValue()) {
                case MotionStop: {
                    AntiVoid.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                    AntiVoid.mc.thePlayer.motionY = 0.0;
                    break;
                }
                case Motion: {
                    AntiVoid.mc.thePlayer.motionY = this.speed.getValue().floatValue();
                    break;
                }
                case Timer: {
                    Cascade.timerManager.set(this.timer.getValue().floatValue());
                    break;
                }
                case Glide: {
                    AntiVoid.mc.thePlayer.motionY *= (double)this.speed.getValue().floatValue();
                    break;
                }
                case Packet: {
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(AntiVoid.mc.thePlayer.posX, (double)this.height.getValue().intValue(), AntiVoid.mc.thePlayer.posZ, AntiVoid.mc.thePlayer.onGround));
                }
            }
        }
    }

    static enum Mode {
        MotionStop,
        Motion,
        Timer,
        Glide,
        Packet;

    }
}


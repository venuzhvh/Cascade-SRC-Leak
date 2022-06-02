/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 */
package cascade.util.player;

import cascade.Cascade;
import cascade.features.modules.player.Freecam;
import cascade.util.Util;
import cascade.util.misc.MathUtil;
import cascade.util.player.HoleUtil;
import java.util.Comparator;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class RotationUtil
implements Util {
    public static Vec3d getEyesPos() {
        return new Vec3d(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), RotationUtil.mc.thePlayer.posZ);
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = RotationUtil.getEyesPos();
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{RotationUtil.mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(yaw - RotationUtil.mc.thePlayer.rotationYaw)), RotationUtil.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(pitch - RotationUtil.mc.thePlayer.rotationPitch))};
    }

    public static void faceYawAndPitch(float yaw, float pitch) {
        RotationUtil.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Rotation(yaw, pitch, RotationUtil.mc.thePlayer.onGround));
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = RotationUtil.getLegitRotations(vec);
        RotationUtil.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float)MathHelper.func_180184_b((int)((int)rotations[1]), (int)360) : rotations[1], RotationUtil.mc.thePlayer.onGround));
    }

    public static void faceEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationUtil.mc.thePlayer.func_174824_e(mc.func_184121_ak()), entity.func_174824_e(mc.func_184121_ak()));
        RotationUtil.faceYawAndPitch(angle[0], angle[1]);
    }

    public static float[] getAngle(Entity entity) {
        return MathUtil.calcAngle(RotationUtil.mc.thePlayer.func_174824_e(mc.func_184121_ak()), entity.func_174824_e(mc.func_184121_ak()));
    }

    public static int getDirection4D() {
        return MathHelper.floor_double((double)((double)(RotationUtil.mc.thePlayer.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
    }

    public static String getDirection4D(boolean northRed) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0) {
            return "South (+Z)";
        }
        if (dirnumber == 1) {
            return "West (-X)";
        }
        if (dirnumber == 2) {
            return (northRed ? "\u00c2\u00a7c" : "") + "North (-Z)";
        }
        if (dirnumber == 3) {
            return "East (+X)";
        }
        return "Loading...";
    }

    public static boolean isInFov(BlockPos pos) {
        return pos != null && (RotationUtil.mc.thePlayer.func_174818_b(pos) < 4.0 || RotationUtil.yawDist(pos) < (double)(RotationUtil.getHalvedfov() + 2.0f));
    }

    public static boolean isInFov(Entity entity) {
        return entity != null && (RotationUtil.mc.thePlayer.getDistanceSqToEntity(entity) < 4.0 || RotationUtil.yawDist(entity) < (double)(RotationUtil.getHalvedfov() + 2.0f));
    }

    public static double yawDist(BlockPos pos) {
        if (pos != null) {
            Vec3d difference = new Vec3d((Vec3i)pos).func_178788_d(RotationUtil.mc.thePlayer.func_174824_e(mc.func_184121_ak()));
            double d = Math.abs((double)RotationUtil.mc.thePlayer.rotationYaw - (Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static double yawDist(Entity e) {
        if (e != null) {
            Vec3d difference = e.func_174791_d().addVector(0.0, (double)(e.getEyeHeight() / 2.0f), 0.0).func_178788_d(RotationUtil.mc.thePlayer.func_174824_e(mc.func_184121_ak()));
            double d = Math.abs((double)RotationUtil.mc.thePlayer.rotationYaw - (Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static float getHalvedfov() {
        return RotationUtil.getFov() / 2.0f;
    }

    public static float getFov() {
        return RotationUtil.mc.gameSettings.fovSetting;
    }

    public static EntityPlayer getRotationPlayer() {
        EntityPlayerSP e = RotationUtil.mc.thePlayer;
        if (Freecam.getInstance().isEnabled()) {
            // empty if block
        }
        return e == null ? RotationUtil.mc.thePlayer : e;
    }

    public static float[] getRotations(double x, double y, double z, Entity f) {
        return RotationUtil.getRotations(x, y, z, f.posX, f.posY, f.posZ, f.getEyeHeight());
    }

    public static float[] getRotations(double x, double y, double z, double fromX, double fromY, double fromZ, float fromHeight) {
        double xDiff = x - fromX;
        double yDiff = y - (fromY + (double)fromHeight);
        double zDiff = z - fromZ;
        double dist = MathHelper.sqrt_double((double)(xDiff * xDiff + zDiff * zDiff));
        float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / Math.PI));
        float prevYaw = Cascade.rotationManager.getYaw();
        float diff = yaw - prevYaw;
        if (diff < -180.0f || diff > 180.0f) {
            float round = Math.round(Math.abs(diff / 360.0f));
            diff = diff < 0.0f ? diff + 360.0f * round : diff - 360.0f * round;
        }
        return new float[]{prevYaw + diff, pitch};
    }

    public static double normalizeAngle(Double angleIn) {
        double d;
        double angle = angleIn;
        angle %= 360.0;
        if (d >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    public static Vec2f getRotationTo(Vec3d posTo, Vec3d posFrom) {
        return RotationUtil.getRotationFromVec(posTo.func_178788_d(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double xz = Math.hypot(vec.xCoord, vec.zCoord);
        float yaw = (float)RotationUtil.normalizeAngle(Math.toDegrees(Math.atan2(vec.zCoord, vec.xCoord)) - 90.0);
        float pitch = (float)RotationUtil.normalizeAngle(Math.toDegrees(-Math.atan2(vec.yCoord, xz)));
        return new Vec2f(yaw, pitch);
    }

    public static HoleUtil.Hole getTargetHoleVec3D(double targetRange) {
        return HoleUtil.getHoles(targetRange, RotationUtil.getPlayerPos(), false).stream().filter(hole -> RotationUtil.mc.thePlayer.func_174791_d().distanceTo(new Vec3d((double)hole.pos1.func_177958_n() + 0.5, RotationUtil.mc.thePlayer.posY, (double)hole.pos1.func_177952_p() + 0.5)) <= targetRange).min(Comparator.comparingDouble(hole -> RotationUtil.mc.thePlayer.func_174791_d().distanceTo(new Vec3d((double)hole.pos1.func_177958_n() + 0.5, RotationUtil.mc.thePlayer.posY, (double)hole.pos1.func_177952_p() + 0.5)))).orElse(null);
    }

    public static BlockPos getPlayerPos() {
        double decimalPoint = RotationUtil.mc.thePlayer.posY - Math.floor(RotationUtil.mc.thePlayer.posY);
        return new BlockPos(RotationUtil.mc.thePlayer.posX, decimalPoint > 0.8 ? Math.floor(RotationUtil.mc.thePlayer.posY) + 1.0 : Math.floor(RotationUtil.mc.thePlayer.posY), RotationUtil.mc.thePlayer.posZ);
    }
}


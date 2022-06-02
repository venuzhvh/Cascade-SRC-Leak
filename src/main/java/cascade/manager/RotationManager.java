/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package cascade.manager;

import cascade.features.Feature;
import cascade.util.misc.MathUtil;
import cascade.util.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RotationManager
extends Feature {
    private float yaw;
    private float pitch;

    public void updateRotations() {
        this.yaw = RotationManager.mc.thePlayer.rotationYaw;
        this.pitch = RotationManager.mc.thePlayer.rotationPitch;
    }

    public void restoreRotations() {
        RotationManager.mc.thePlayer.rotationYaw = this.yaw;
        RotationManager.mc.thePlayer.rotationYawHead = this.yaw;
        RotationManager.mc.thePlayer.rotationPitch = this.pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.thePlayer.rotationYaw = yaw;
        RotationManager.mc.thePlayer.rotationYawHead = yaw;
        RotationManager.mc.thePlayer.rotationPitch = pitch;
    }

    public void setPlayerYaw(float yaw) {
        RotationManager.mc.thePlayer.rotationYaw = yaw;
        RotationManager.mc.thePlayer.rotationYawHead = yaw;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.thePlayer.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() + 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.thePlayer.func_174824_e(mc.func_184121_ak()), new Vec3d(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.thePlayer.func_174824_e(mc.func_184121_ak()), entity.func_174824_e(mc.func_184121_ak()));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void setPlayerPitch(float pitch) {
        RotationManager.mc.thePlayer.rotationPitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getDirection4D() {
        return RotationUtil.getDirection4D();
    }

    public String getDirection4D(boolean northRed) {
        return RotationUtil.getDirection4D(northRed);
    }
}


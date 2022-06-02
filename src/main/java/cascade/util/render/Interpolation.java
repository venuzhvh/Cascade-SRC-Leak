/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Vec3d
 */
package cascade.util.render;

import cascade.util.Util;
import cascade.util.misc.IMinecraft;
import cascade.util.misc.IRenderManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class Interpolation
implements Util {
    public static Vec3d interpolateEntity(Entity entity) {
        double x = Interpolation.interpolateLastTickPos(entity.posX, entity.lastTickPosX) - Interpolation.getRenderPosX();
        double y = Interpolation.interpolateLastTickPos(entity.posY, entity.lastTickPosY) - Interpolation.getRenderPosY();
        double z = Interpolation.interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - Interpolation.getRenderPosZ();
        return new Vec3d(x, y, z);
    }

    public static double getRenderPosX() {
        return ((IRenderManager)mc.func_175598_ae()).getRenderPosX();
    }

    public static double getRenderPosY() {
        return ((IRenderManager)mc.func_175598_ae()).getRenderPosY();
    }

    public static double getRenderPosZ() {
        return ((IRenderManager)mc.func_175598_ae()).getRenderPosZ();
    }

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * (double)((IMinecraft)Interpolation.mc).getTimer().field_194147_b;
    }

    public static Frustum createFrustum(Entity entity) {
        Frustum frustum = new Frustum();
        double x = Interpolation.interpolateLastTickPos(entity.posX, entity.lastTickPosX);
        double y = Interpolation.interpolateLastTickPos(entity.posY, entity.lastTickPosY);
        double z = Interpolation.interpolateLastTickPos(entity.posZ, entity.lastTickPosZ);
        frustum.setPosition(x, y, z);
        return frustum;
    }
}


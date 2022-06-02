/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.MobEffects
 *  net.minecraft.util.MovementInput
 *  net.minecraft.util.math.BlockPos
 *  org.lwjgl.input.Keyboard
 */
package cascade.util.player;

import cascade.event.events.MoveEvent;
import cascade.util.Util;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;

public class MovementUtil
implements Util {
    public static boolean isMoving() {
        return (double)MovementUtil.mc.thePlayer.field_191988_bg != 0.0 || (double)MovementUtil.mc.thePlayer.moveStrafing != 0.0;
    }

    public static boolean anyMovementKeys() {
        return MovementUtil.mc.thePlayer.movementInput.field_187255_c || MovementUtil.mc.thePlayer.movementInput.field_187256_d || MovementUtil.mc.thePlayer.movementInput.field_187257_e || MovementUtil.mc.thePlayer.movementInput.field_187258_f || MovementUtil.mc.thePlayer.movementInput.jump || MovementUtil.mc.thePlayer.movementInput.sneak;
    }

    public static boolean noMovementKeysOrJump() {
        return !MovementUtil.isWasdPressed() && !Keyboard.isKeyDown((int)MovementUtil.mc.gameSettings.keyBindJump.getKeyCode());
    }

    public static void setMoveSpeed(double speed) {
        double forward = MovementUtil.mc.thePlayer.movementInput.field_192832_b;
        double strafe = MovementUtil.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MovementUtil.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtil.mc.thePlayer.motionX = 0.0;
            MovementUtil.mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            MovementUtil.mc.thePlayer.motionX = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            MovementUtil.mc.thePlayer.motionZ = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
        }
    }

    public static void strafe(MoveEvent event, double speed) {
        if (MovementUtil.isMoving()) {
            double[] strafe = MovementUtil.strafe(speed);
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        } else {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static double[] strafe(double speed) {
        return MovementUtil.strafe((Entity)MovementUtil.mc.thePlayer, speed);
    }

    public static double[] strafe(Entity entity, double speed) {
        return MovementUtil.strafe(entity, MovementUtil.mc.thePlayer.movementInput, speed);
    }

    public static double[] strafe(Entity entity, MovementInput movementInput, double speed) {
        float moveForward = movementInput.field_192832_b;
        float moveStrafe = movementInput.moveStrafe;
        float rotationYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * mc.func_184121_ak();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float)(moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double)moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double)moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double)moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double)moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public static MovementInput inverse(Entity entity, double speed) {
        MovementInput input = new MovementInput();
        input.sneak = entity.isSneaking();
        block0: for (float d = -1.0f; d <= 1.0f; d += 1.0f) {
            for (float e = -1.0f; e <= 1.0f; e += 1.0f) {
                MovementInput dummyInput = new MovementInput();
                dummyInput.field_192832_b = d;
                dummyInput.moveStrafe = e;
                dummyInput.sneak = entity.isSneaking();
                double[] moveVec = MovementUtil.strafe(entity, dummyInput, speed);
                if (entity.isSneaking()) {
                    moveVec[0] = moveVec[0] * (double)0.3f;
                    moveVec[1] = moveVec[1] * (double)0.3f;
                }
                double targetMotionX = moveVec[0];
                double targetMotionZ = moveVec[1];
                if (!(targetMotionX < 0.0 ? entity.motionX <= targetMotionX : entity.motionX >= targetMotionX) || !(targetMotionZ < 0.0 ? entity.motionZ <= targetMotionZ : entity.motionZ >= targetMotionZ)) continue;
                input.field_192832_b = d;
                input.moveStrafe = e;
                continue block0;
            }
        }
        return input;
    }

    public static double getDistance2D() {
        double xDist = MovementUtil.mc.thePlayer.posX - MovementUtil.mc.thePlayer.prevPosX;
        double zDist = MovementUtil.mc.thePlayer.posZ - MovementUtil.mc.thePlayer.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static double getDistance3D() {
        double xDist = MovementUtil.mc.thePlayer.posX - MovementUtil.mc.thePlayer.prevPosX;
        double yDist = MovementUtil.mc.thePlayer.posY - MovementUtil.mc.thePlayer.prevPosY;
        double zDist = MovementUtil.mc.thePlayer.posZ - MovementUtil.mc.thePlayer.prevPosZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public static double getSpeed() {
        return MovementUtil.getSpeed(false);
    }

    public static double getSpeed(double defaultSpeed) {
        int amplifier;
        if (MovementUtil.mc.thePlayer.isPotionActive(MobEffects.moveSpeed)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.thePlayer.getActivePotionEffect(MobEffects.moveSpeed)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (MovementUtil.mc.thePlayer.isPotionActive(MobEffects.moveSlowdown)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.thePlayer.getActivePotionEffect(MobEffects.moveSlowdown)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getSpeed(boolean slowness) {
        int amplifier;
        double defaultSpeed = 0.2873;
        if (MovementUtil.mc.thePlayer.isPotionActive(MobEffects.moveSpeed)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.thePlayer.getActivePotionEffect(MobEffects.moveSpeed)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (slowness && MovementUtil.mc.thePlayer.isPotionActive(MobEffects.moveSlowdown)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.thePlayer.getActivePotionEffect(MobEffects.moveSlowdown)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return defaultSpeed;
    }

    public static double getJumpSpeed() {
        double defaultSpeed = 0.0;
        if (MovementUtil.mc.thePlayer.isPotionActive(MobEffects.jump)) {
            int amplifier = MovementUtil.mc.thePlayer.getActivePotionEffect(MobEffects.jump).getAmplifier();
            defaultSpeed += (double)(amplifier + 1) * 0.1;
        }
        return defaultSpeed;
    }

    public static boolean isInMovementDirection(double x, double y, double z) {
        if (MovementUtil.mc.thePlayer.motionX != 0.0 || MovementUtil.mc.thePlayer.motionZ != 0.0) {
            BlockPos movingPos = new BlockPos((Entity)MovementUtil.mc.thePlayer).func_177963_a(MovementUtil.mc.thePlayer.motionX * 10000.0, 0.0, MovementUtil.mc.thePlayer.motionZ * 10000.0);
            BlockPos antiPos = new BlockPos((Entity)MovementUtil.mc.thePlayer).func_177963_a(MovementUtil.mc.thePlayer.motionX * -10000.0, 0.0, MovementUtil.mc.thePlayer.motionY * -10000.0);
            return movingPos.func_177954_c(x, y, z) < antiPos.func_177954_c(x, y, z);
        }
        return true;
    }

    public static boolean isWasdPressed() {
        return MovementUtil.mc.gameSettings.keyBindForward.getIsKeyPressed() || MovementUtil.mc.gameSettings.keyBindBack.getIsKeyPressed() || MovementUtil.mc.gameSettings.keyBindLeft.getIsKeyPressed() || MovementUtil.mc.gameSettings.keyBindRight.getIsKeyPressed();
    }

    public static void step(float height) {
        if (!MovementUtil.mc.thePlayer.isCollidedVertically || (double)MovementUtil.mc.thePlayer.fallDistance > 0.1 || MovementUtil.mc.thePlayer.isOnLadder() || !MovementUtil.mc.thePlayer.onGround) {
            return;
        }
        MovementUtil.mc.thePlayer.stepHeight = height;
    }

    public static void setMotion(double x, double y, double z) {
        if (MovementUtil.mc.thePlayer != null) {
            if (MovementUtil.mc.thePlayer.func_184218_aH()) {
                MovementUtil.mc.thePlayer.field_184239_as.motionX = x;
                MovementUtil.mc.thePlayer.field_184239_as.motionY = y;
                MovementUtil.mc.thePlayer.field_184239_as.motionZ = x;
            } else {
                MovementUtil.mc.thePlayer.motionX = x;
                MovementUtil.mc.thePlayer.motionY = y;
                MovementUtil.mc.thePlayer.motionZ = z;
            }
        }
    }

    public static double[] forward(double speed, float yaw) {
        float forward = 1.0f;
        float side = 0.0f;
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static double[] forward(double speed) {
        float forward = Minecraft.getMinecraft().thePlayer.movementInput.field_192832_b;
        float side = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.prevRotationYaw + (Minecraft.getMinecraft().thePlayer.rotationYaw - Minecraft.getMinecraft().thePlayer.prevRotationYaw) * Minecraft.getMinecraft().func_184121_ak();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }
}


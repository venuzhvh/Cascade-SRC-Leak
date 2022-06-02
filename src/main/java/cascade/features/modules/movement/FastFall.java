/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.features.modules.Module;
import cascade.features.modules.player.Freecam;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.PlayerUtil;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class FastFall
extends Module {
    Setting<Double> speed = this.register(new Setting<Double>("Speed", 3.0, 0.1, 10.0));
    Setting<Double> height = this.register(new Setting<Double>("Height", 10.0, 0.1, 90.0));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    List<Block> incelBlocks = Arrays.asList(Blocks.bed, Blocks.field_180399_cE);

    public FastFall() {
        super("FastFall", Module.Category.MOVEMENT, "Fast fall");
    }

    @Override
    public void onUpdate() {
        if (FastFall.fullNullCheck() || this.shouldReturn()) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        RayTraceResult trace = FastFall.mc.theWorld.rayTraceBlocks(FastFall.mc.thePlayer.func_174791_d(), new Vec3d(FastFall.mc.thePlayer.posX, FastFall.mc.thePlayer.posY - this.height.getValue(), FastFall.mc.thePlayer.posZ), false, false, false);
        if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK && FastFall.mc.theWorld.func_180495_p(new BlockPos(FastFall.mc.thePlayer.posX, FastFall.mc.thePlayer.posY - 0.1, FastFall.mc.thePlayer.posZ)).func_177230_c() != this.incelBlocks) {
            FastFall.mc.thePlayer.motionY = -this.speed.getValue().doubleValue();
        }
    }

    boolean shouldReturn() {
        return FastFall.mc.thePlayer.func_184613_cA() || PlayerUtil.isClipping() || EntityUtil.isInLiquid() || FastFall.mc.thePlayer.isOnLadder() || FastFall.mc.thePlayer.capabilities.isFlying || FastFall.mc.thePlayer.motionY > 0.0 || FastFall.mc.gameSettings.keyBindJump.getIsKeyPressed() || FastFall.mc.thePlayer.isEntityInsideOpaqueBlock() || FastFall.mc.thePlayer.noClip || !FastFall.mc.thePlayer.onGround || Cascade.moduleManager.isModuleEnabled("PacketFly") || Freecam.getInstance().isEnabled() || Cascade.moduleManager.isModuleEnabled("Phase") || Cascade.moduleManager.isModuleEnabled("LongJump") || Cascade.moduleManager.isModuleEnabled("Strafe");
    }
}


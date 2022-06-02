/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.CollisionEvent;
import cascade.event.events.LiquidJumpEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.modules.player.Freecam;
import cascade.util.entity.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus
extends Module {
    private static Jesus INSTANCE;
    boolean jumping;

    public Jesus() {
        super("Jesus", Module.Category.MOVEMENT, "sus(LOL)");
        INSTANCE = this;
    }

    public static Jesus getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Jesus();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        this.jumping = false;
    }

    @SubscribeEvent
    public void updateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (Jesus.fullNullCheck() || this.isDisabled() || Freecam.getInstance().isEnabled()) {
            return;
        }
        this.doTrampoline();
    }

    @Override
    public String getDisplayInfo() {
        return "Trampoline";
    }

    void doTrampoline() {
        boolean inLiquid;
        if (Jesus.mc.thePlayer.isSneaking() || Jesus.mc.thePlayer.isOnLadder()) {
            return;
        }
        int minY = MathHelper.floor_double((double)(Jesus.mc.thePlayer.func_174813_aQ().minY - 0.2));
        boolean bl = inLiquid = Jesus.checkIfBlockInBB(BlockLiquid.class, minY) != null;
        if (inLiquid && !Jesus.mc.thePlayer.isSneaking()) {
            Jesus.mc.thePlayer.onGround = false;
        }
        Block block = Jesus.mc.theWorld.func_180495_p(new BlockPos((int)Math.floor(Jesus.mc.thePlayer.posX), (int)Math.floor(Jesus.mc.thePlayer.posY), (int)Math.floor(Jesus.mc.thePlayer.posZ))).func_177230_c();
        if (this.jumping && !Jesus.mc.thePlayer.capabilities.isFlying && !Jesus.mc.thePlayer.isInWater()) {
            if (Jesus.mc.thePlayer.motionY < -0.3 || Jesus.mc.thePlayer.onGround || Jesus.mc.thePlayer.isOnLadder()) {
                this.jumping = false;
                return;
            }
            Jesus.mc.thePlayer.motionY = Jesus.mc.thePlayer.motionY / (double)0.98f + 0.08;
            Jesus.mc.thePlayer.motionY -= 0.03120000000005;
        }
        if (Jesus.mc.thePlayer.isInWater() || Jesus.mc.thePlayer.func_180799_ab()) {
            Jesus.mc.thePlayer.motionY = 0.1;
        }
        if (!Jesus.mc.thePlayer.func_180799_ab() && !Jesus.mc.thePlayer.isInWater() && block instanceof BlockLiquid && Jesus.mc.thePlayer.motionY < 0.2) {
            Jesus.mc.thePlayer.motionY = 0.5;
            this.jumping = true;
        }
    }

    @SubscribeEvent
    public void onCollision(CollisionEvent e) {
        if (!Jesus.fullNullCheck() && e.getBlock() instanceof BlockLiquid && e.getEntity() == Jesus.mc.thePlayer && (double)e.getPos().func_177956_o() <= Jesus.mc.thePlayer.posY && Jesus.checkIfBlockInBB(BlockLiquid.class, MathHelper.floor_double((double)(Jesus.mc.thePlayer.func_174813_aQ().minY + 0.01))) != null && Jesus.checkIfBlockInBB(BlockLiquid.class, MathHelper.floor_double((double)(Jesus.mc.thePlayer.func_174813_aQ().minY - 0.02))) != null && Jesus.mc.thePlayer.fallDistance < 3.0f && !Jesus.mc.thePlayer.isSneaking()) {
            e.setBB(Block.field_185505_j);
        }
    }

    @SubscribeEvent
    public void onLiquidJump(LiquidJumpEvent e) {
        if (!Jesus.fullNullCheck() && EntityUtil.isInLiquid() && (Jesus.mc.thePlayer.motionY == 0.1 || Jesus.mc.thePlayer.motionY == 0.5)) {
            e.setCanceled(true);
        }
    }

    public static IBlockState checkIfBlockInBB(Class<? extends Block> blockClass, int minY) {
        for (int iX = MathHelper.floor_double((double)Jesus.mc.thePlayer.func_174813_aQ().minX); iX < MathHelper.ceiling_double_int((double)Jesus.mc.thePlayer.func_174813_aQ().maxX); ++iX) {
            for (int iZ = MathHelper.floor_double((double)Jesus.mc.thePlayer.func_174813_aQ().minZ); iZ < MathHelper.ceiling_double_int((double)Jesus.mc.thePlayer.func_174813_aQ().maxZ); ++iZ) {
                IBlockState state = Jesus.mc.theWorld.func_180495_p(new BlockPos(iX, minY, iZ));
                if (!blockClass.isInstance(state.func_177230_c())) continue;
                return state;
            }
        }
        return null;
    }

    public static boolean isInLiquid() {
        if (Jesus.mc.thePlayer.fallDistance >= 3.0f) {
            return false;
        }
        boolean inLiquid = false;
        AxisAlignedBB bb = Jesus.mc.thePlayer.func_184187_bx() != null ? Jesus.mc.thePlayer.func_184187_bx().func_174813_aQ() : Jesus.mc.thePlayer.func_174813_aQ();
        int y = (int)bb.minY;
        for (int x = MathHelper.floor_double((double)bb.minX); x < MathHelper.floor_double((double)bb.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double((double)bb.minZ); z < MathHelper.floor_double((double)bb.maxZ) + 1; ++z) {
                Block block = Jesus.mc.theWorld.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                if (block instanceof BlockAir) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                inLiquid = true;
            }
        }
        return inLiquid;
    }

    public static boolean isOnLiquid() {
        if (Jesus.mc.thePlayer.fallDistance >= 3.0f) {
            return false;
        }
        AxisAlignedBB bb = Jesus.mc.thePlayer.func_184187_bx() != null ? Jesus.mc.thePlayer.func_184187_bx().func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).offset(0.0, (double)-0.05f, 0.0) : Jesus.mc.thePlayer.func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).offset(0.0, (double)-0.05f, 0.0);
        boolean onLiquid = false;
        int y = (int)bb.minY;
        for (int x = MathHelper.floor_double((double)bb.minX); x < MathHelper.floor_double((double)(bb.maxX + 1.0)); ++x) {
            for (int z = MathHelper.floor_double((double)bb.minZ); z < MathHelper.floor_double((double)(bb.maxZ + 1.0)); ++z) {
                Block block = Jesus.mc.theWorld.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                if (block == Blocks.air) continue;
                if (!(block instanceof BlockLiquid)) {
                    return false;
                }
                onLiquid = true;
            }
        }
        return onLiquid;
    }
}


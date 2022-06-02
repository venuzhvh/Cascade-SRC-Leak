/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package cascade.mixin.mixins;

import cascade.Cascade;
import cascade.event.events.CollisionEvent;
import cascade.features.modules.movement.Jesus;
import cascade.features.modules.player.Freecam;
import cascade.features.modules.visual.Wallhack;
import cascade.util.Util;
import cascade.util.player.PlayerUtil;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Block.class})
public class MixinBlock {
    @Shadow
    protected static void func_185492_a(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, AxisAlignedBB blockBox) {
        throw new IllegalStateException("MixinBlock.addCollisionBoxToList has not been shadowed");
    }

    @Inject(method={"shouldSideBeRendered"}, at={@At(value="HEAD")}, cancellable=true)
    public void shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side, CallbackInfoReturnable<Boolean> info) {
        if (Wallhack.INSTANCE.isEnabled()) {
            info.setReturnValue(true);
        }
    }

    @Inject(method={"getRenderLayer"}, at={@At(value="HEAD")}, cancellable=true)
    public void getRenderLayer(CallbackInfoReturnable<BlockRenderLayer> info) {
        if (Wallhack.INSTANCE.isEnabled() && !Wallhack.blocks.contains(this)) {
            info.setReturnValue(BlockRenderLayer.TRANSLUCENT);
        }
    }

    @Inject(method={"getLightValue"}, at={@At(value="HEAD")}, cancellable=true)
    public void getLightValue(CallbackInfoReturnable<Integer> info) {
        if (Wallhack.INSTANCE.isEnabled()) {
            info.setReturnValue(Wallhack.INSTANCE.light.getValue());
        }
    }

    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"}, at={@At(value="HEAD")}, cancellable=true)
    public void addCollisionBoxToListHook(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState, CallbackInfo info) {
        try {
            if (Freecam.getInstance().isDisabled() && Jesus.getInstance().isOn() && Util.mc.thePlayer != null && state != null && state.func_177230_c() instanceof BlockLiquid && !(entityIn instanceof EntityBoat) && !Util.mc.thePlayer.isSneaking() && Util.mc.thePlayer.fallDistance < 3.0f && !PlayerUtil.isAboveLiquid() && PlayerUtil.checkForLiquid(false) || PlayerUtil.checkForLiquid(false) && Util.mc.thePlayer.func_184187_bx() != null && Util.mc.thePlayer.func_184187_bx().fallDistance < 3.0f && PlayerUtil.isAboveBlock(pos)) {
                info.cancel();
            }
        }
        catch (Exception ex) {
            Cascade.LOGGER.info("Caught an exception from MixinBlock[Jesus]");
            ex.printStackTrace();
        }
    }

    @Deprecated
    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;Z)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void addCollisionBoxToListHook_Pre(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, Entity entity, boolean isActualState, CallbackInfo info) {
        CollisionEvent event;
        if (!Cascade.moduleManager.isModuleEnabled("Jesus")) {
            return;
        }
        Block block = (Block)Block.class.cast(this);
        AxisAlignedBB bb = block.func_180646_a(state, (IBlockAccess)world, pos);
        if (bb != (event = new CollisionEvent(pos, bb, entity, block)).getBB()) {
            bb = event.getBB();
        }
        if (bb != null && entityBox.intersectsWith(bb)) {
            cBoxes.add(bb);
        }
        MixinBlock.func_185492_a(pos, entityBox, cBoxes, bb);
        info.cancel();
    }

    @Inject(method={"addCollisionBoxToList(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/util/math/AxisAlignedBB;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private static void addCollisionBoxToListHook(BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> cBoxes, AxisAlignedBB blockBox, CallbackInfo info) {
        if (blockBox != Block.field_185506_k && Cascade.moduleManager.isModuleEnabled("Jesus")) {
            CollisionEvent event;
            AxisAlignedBB bb = blockBox.func_186670_a(pos);
            if (bb != (event = new CollisionEvent(pos, bb, null, Util.mc.theWorld != null ? Util.mc.theWorld.func_180495_p(pos).func_177230_c() : null)).getBB()) {
                bb = event.getBB();
            }
            if (bb != null && entityBox.intersectsWith(bb)) {
                cBoxes.add(bb);
            }
            info.cancel();
        }
    }
}


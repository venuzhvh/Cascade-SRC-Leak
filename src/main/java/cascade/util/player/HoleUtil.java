/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package cascade.util.player;

import cascade.util.Util;
import cascade.util.player.BlockUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleUtil
implements Util {
    public static BlockPos[] holeOffsets = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    private static final Vec3i[] OFFSETS_2x2 = new Vec3i[]{new Vec3i(0, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 1)};
    private static final Block[] NO_BLAST = new Block[]{Blocks.bedrock, Blocks.obsidian, Blocks.anvil, Blocks.ender_chest};

    public static boolean isHole(BlockPos pos) {
        boolean isHole = false;
        int amount = 0;
        for (BlockPos p : holeOffsets) {
            if (HoleUtil.mc.theWorld.func_180495_p(pos.func_177971_a((Vec3i)p)).func_185904_a().isReplaceable()) continue;
            ++amount;
        }
        if (amount == 5) {
            isHole = true;
        }
        return isHole;
    }

    public static boolean isObbyHole(BlockPos pos) {
        boolean isHole = true;
        int bedrock = 0;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtil.mc.theWorld.func_180495_p(pos.func_177971_a((Vec3i)off)).func_177230_c();
            if (!HoleUtil.isSafeBlock(pos.func_177971_a((Vec3i)off))) {
                isHole = false;
                continue;
            }
            if (b != Blocks.obsidian && b != Blocks.ender_chest && b != Blocks.anvil) continue;
            ++bedrock;
        }
        if (HoleUtil.mc.theWorld.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c() != Blocks.air || HoleUtil.mc.theWorld.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c() != Blocks.air) {
            isHole = false;
        }
        if (bedrock < 1) {
            isHole = false;
        }
        return isHole;
    }

    public static boolean isBedrockHoles(BlockPos pos) {
        boolean isHole = true;
        for (BlockPos off : holeOffsets) {
            Block b = HoleUtil.mc.theWorld.func_180495_p(pos.func_177971_a((Vec3i)off)).func_177230_c();
            if (b == Blocks.bedrock) continue;
            isHole = false;
        }
        if (HoleUtil.mc.theWorld.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c() != Blocks.air || HoleUtil.mc.theWorld.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c() != Blocks.air) {
            isHole = false;
        }
        return isHole;
    }

    public static Hole isDoubleHole(BlockPos pos) {
        if (HoleUtil.checkOffset(pos, 1, 0)) {
            return new Hole(false, true, pos, pos.func_177982_a(1, 0, 0));
        }
        if (HoleUtil.checkOffset(pos, 0, 1)) {
            return new Hole(false, true, pos, pos.func_177982_a(0, 0, 1));
        }
        return null;
    }

    public static boolean checkOffset(BlockPos pos, int offX, int offZ) {
        return HoleUtil.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.air && HoleUtil.mc.theWorld.func_180495_p(pos.func_177982_a(offX, 0, offZ)).func_177230_c() == Blocks.air && HoleUtil.isSafeBlock(pos.func_177982_a(0, -1, 0)) && HoleUtil.isSafeBlock(pos.func_177982_a(offX, -1, offZ)) && HoleUtil.isSafeBlock(pos.func_177982_a(offX * 2, 0, offZ * 2)) && HoleUtil.isSafeBlock(pos.func_177982_a(-offX, 0, -offZ)) && HoleUtil.isSafeBlock(pos.func_177982_a(offZ, 0, offX)) && HoleUtil.isSafeBlock(pos.func_177982_a(-offZ, 0, -offX)) && HoleUtil.isSafeBlock(pos.func_177982_a(offX, 0, offZ).func_177982_a(offZ, 0, offX)) && HoleUtil.isSafeBlock(pos.func_177982_a(offX, 0, offZ).func_177982_a(-offZ, 0, -offX));
    }

    static boolean isSafeBlock(BlockPos pos) {
        return HoleUtil.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.obsidian || HoleUtil.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.bedrock || HoleUtil.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.ender_chest;
    }

    public static List<Hole> getHoles(double range, BlockPos playerPos, boolean doubles) {
        ArrayList<Hole> holes = new ArrayList<Hole>();
        List<BlockPos> circle = HoleUtil.getSphere(range, playerPos, true, false);
        for (BlockPos pos : circle) {
            Hole dh;
            if (HoleUtil.mc.theWorld.func_180495_p(pos).func_177230_c() != Blocks.air) continue;
            if (HoleUtil.isObbyHole(pos)) {
                holes.add(new Hole(false, false, pos));
                continue;
            }
            if (HoleUtil.isBedrockHoles(pos)) {
                holes.add(new Hole(true, false, pos));
                continue;
            }
            if (!doubles || (dh = HoleUtil.isDoubleHole(pos)) == null || HoleUtil.mc.theWorld.func_180495_p(dh.pos1.func_177982_a(0, 1, 0)).func_177230_c() != Blocks.air && HoleUtil.mc.theWorld.func_180495_p(dh.pos2.func_177982_a(0, 1, 0)).func_177230_c() != Blocks.air) continue;
            holes.add(dh);
        }
        return holes;
    }

    public static List<BlockPos> getSphere(double range, BlockPos pos, boolean sphere, boolean hollow) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.func_177958_n();
        int cy = pos.func_177956_o();
        int cz = pos.func_177952_p();
        int x = cx - (int)range;
        while ((double)x <= (double)cx + range) {
            int z = cz - (int)range;
            while ((double)z <= (double)cz + range) {
                int y = sphere ? cy - (int)range : cy;
                while (true) {
                    double d2;
                    double d = y;
                    double d3 = d2 = sphere ? (double)cy + range : (double)cy + range;
                    if (!(d < d2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < range * range) || hollow && dist < (range - 1.0) * (range - 1.0))) {
                        BlockPos l = new BlockPos(x, y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    public static boolean[] isHole(BlockPos pos, boolean above) {
        boolean[] result = new boolean[]{false, true};
        if (!BlockUtil.isAir(pos) || !BlockUtil.isAir(pos.func_177984_a()) || above && !BlockUtil.isAir(pos.func_177981_b(2))) {
            return result;
        }
        return HoleUtil.is1x1(pos, result);
    }

    public static boolean[] is1x1(BlockPos pos) {
        return HoleUtil.is1x1(pos, new boolean[]{false, true});
    }

    public static boolean[] is1x1(BlockPos pos, boolean[] result) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset;
            IBlockState state;
            if (facing == EnumFacing.UP || (state = HoleUtil.mc.theWorld.func_180495_p(offset = pos.func_177972_a(facing))).func_177230_c() == Blocks.bedrock) continue;
            if (Arrays.stream(NO_BLAST).noneMatch(b -> b == state.func_177230_c())) {
                return result;
            }
            result[1] = false;
        }
        result[0] = true;
        return result;
    }

    public static boolean is2x1(BlockPos pos) {
        return HoleUtil.is2x1(pos, true);
    }

    public static boolean is2x1(BlockPos pos, boolean upper) {
        if (upper) {
            if (!BlockUtil.isAir(pos)) {
                return false;
            }
            if (!BlockUtil.isAir(pos.func_177984_a())) {
                return false;
            }
            if (BlockUtil.isAir(pos.func_177977_b())) {
                return false;
            }
        }
        int airBlocks = 0;
        for (EnumFacing facing : EnumFacing.field_176754_o) {
            BlockPos offset = pos.func_177972_a(facing);
            if (BlockUtil.isAir(offset)) {
                if (!BlockUtil.isAir(offset.func_177984_a())) {
                    return false;
                }
                if (BlockUtil.isAir(offset.func_177977_b())) {
                    return false;
                }
                for (EnumFacing offsetFacing : EnumFacing.field_176754_o) {
                    if (offsetFacing == facing.func_176734_d()) continue;
                    IBlockState state = HoleUtil.mc.theWorld.func_180495_p(offset.func_177972_a(offsetFacing));
                    if (!Arrays.stream(NO_BLAST).noneMatch(b -> b == state.func_177230_c())) continue;
                    return false;
                }
                ++airBlocks;
            }
            if (airBlocks <= 0) continue;
            return false;
        }
        return airBlocks == 0;
    }

    public static boolean is2x2Partial(BlockPos pos) {
        HashSet<BlockPos> positions = new HashSet<BlockPos>();
        for (Vec3i vec : OFFSETS_2x2) {
            positions.add(pos.func_177971_a(vec));
        }
        boolean airBlock = false;
        for (BlockPos holePos : positions) {
            if (BlockUtil.isAir(holePos) && BlockUtil.isAir(holePos.func_177984_a()) && !BlockUtil.isAir(holePos.func_177977_b())) {
                if (BlockUtil.isAir(holePos.func_177981_b(2))) {
                    airBlock = true;
                }
                for (EnumFacing facing : EnumFacing.field_176754_o) {
                    BlockPos offset = holePos.func_177972_a(facing);
                    if (positions.contains(offset)) continue;
                    IBlockState state = HoleUtil.mc.theWorld.func_180495_p(offset);
                    if (!Arrays.stream(NO_BLAST).noneMatch(b -> b == state.func_177230_c())) continue;
                    return false;
                }
                continue;
            }
            return false;
        }
        return airBlock;
    }

    public static boolean is2x2(BlockPos pos) {
        return HoleUtil.is2x2(pos, true);
    }

    public static boolean is2x2(BlockPos pos, boolean upper) {
        if (upper && !BlockUtil.isAir(pos)) {
            return false;
        }
        if (HoleUtil.is2x2Partial(pos)) {
            return true;
        }
        BlockPos l = pos.func_177982_a(-1, 0, 0);
        boolean airL = BlockUtil.isAir(l);
        if (airL && HoleUtil.is2x2Partial(l)) {
            return true;
        }
        BlockPos r = pos.func_177982_a(0, 0, -1);
        boolean airR = BlockUtil.isAir(r);
        if (airR && HoleUtil.is2x2Partial(r)) {
            return true;
        }
        return (airL || airR) && HoleUtil.is2x2Partial(pos.func_177982_a(-1, 0, -1));
    }

    public static class Hole {
        public boolean bedrock;
        public boolean doubleHole;
        public BlockPos pos1;
        public BlockPos pos2;

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1, BlockPos pos2) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public Hole(boolean bedrock, boolean doubleHole, BlockPos pos1) {
            this.bedrock = bedrock;
            this.doubleHole = doubleHole;
            this.pos1 = pos1;
        }
    }
}


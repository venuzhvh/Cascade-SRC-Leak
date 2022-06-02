/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.modules.player.Mine;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.AttackUtil;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.player.RotationUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Trap
extends Module {
    Setting<Integer> bpt = this.register(new Setting<Integer>("BPT", 8, 1, 25));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(4.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    Setting<Integer> predictBpt = this.register(new Setting<Object>("PredictBPT", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(8), v -> this.predict.getValue()));
    Setting<Cage> cage = this.register(new Setting<Cage>("Cage", Cage.Trap));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    Setting<Integer> toggle = this.register(new Setting<Integer>("AutoDisable", 0, 0, 250));
    Setting<Float> maxTargetSpeed = this.register(new Setting<Float>("MaxTargetSpeed", Float.valueOf(20.5f), Float.valueOf(0.1f), Float.valueOf(50.0f)));
    Set<BlockPos> placeList = new HashSet<BlockPos>();
    Timer toggleTimer = new Timer();
    boolean isSneaking = false;
    EntityPlayer closestTarget;
    Timer timer = new Timer();
    int offsetStep = 0;
    int packets;
    int ticks;
    static Vec3d[] TRAP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
    static Vec3d[] TRAPTOP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(0.0, 4.0, 0.0)};
    static Vec3d[] TRAPFULLROOF = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0)};
    static Vec3d[] TRAPFULLROOFTOP = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0), new Vec3d(0.0, 4.0, 0.0)};
    static Vec3d[] CRYSTALEXA = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
    static Vec3d[] CRYSTAL = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 0.0)};
    static Vec3d[] CRYSTALFULLROOF = new Vec3d[]{new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(1.0, 1.0, 1.0), new Vec3d(-1.0, 1.0, 1.0), new Vec3d(1.0, 1.0, -1.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, 1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(-1.0, 2.0, 1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(0.0, 3.0, -1.0), new Vec3d(1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 1.0), new Vec3d(-1.0, 3.0, 0.0), new Vec3d(0.0, 3.0, 0.0)};
    static List<Block> blackList = Arrays.asList(Blocks.ender_chest, Blocks.chest, Blocks.trapped_chest, Blocks.crafting_table, Blocks.anvil, Blocks.brewing_stand, Blocks.hopper, Blocks.dropper, Blocks.dispenser);
    static List<Block> shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);

    public Trap() {
        super("Trap", Module.Category.COMBAT, "Traps targets");
    }

    @Override
    public void onDisable() {
        if (Trap.fullNullCheck()) {
            return;
        }
        this.placeList.clear();
        this.toggleTimer.reset();
        this.isSneaking = false;
        this.closestTarget = null;
        this.timer.reset();
        this.offsetStep = 0;
        this.packets = 0;
        this.ticks = 0;
        if (this.isSneaking) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)Trap.mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (Trap.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.predict.getValue().booleanValue() && e.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange p = (SPacketBlockChange)e.getPacket();
            if (p.field_148883_d.func_177230_c() == Blocks.air && this.placeList.contains(p.field_179828_a) && this.predictBpt.getValue() <= this.packets) {
                mc.addScheduledTask(() -> this.doTrap());
                ++this.packets;
            }
        }
    }

    @Override
    public void onUpdate() {
        if (Trap.fullNullCheck()) {
            return;
        }
        ++this.ticks;
        mc.addScheduledTask(() -> this.findClosestTarget());
        if (this.closestTarget == null) {
            return;
        }
        mc.addScheduledTask(() -> this.doTrap());
        if (this.toggle.getValue() != 0 && this.timer.passedMs(this.toggle.getValue().intValue())) {
            this.disable();
            return;
        }
    }

    void doTrap() {
        try {
            ArrayList placeTargets = new ArrayList();
            switch (this.cage.getValue()) {
                case Trap: {
                    Collections.addAll(placeTargets, TRAP);
                    break;
                }
                case TrapTop: {
                    Collections.addAll(placeTargets, TRAPTOP);
                    break;
                }
                case TrapFullRoof: {
                    Collections.addAll(placeTargets, TRAPFULLROOF);
                    break;
                }
                case TrapFullRoofTop: {
                    Collections.addAll(placeTargets, TRAPFULLROOFTOP);
                    break;
                }
                case CrystalExa: {
                    Collections.addAll(placeTargets, CRYSTALEXA);
                    break;
                }
                case Crystal: {
                    Collections.addAll(placeTargets, CRYSTAL);
                    break;
                }
                case CrystalFullRoof: {
                    Collections.addAll(placeTargets, CRYSTALFULLROOF);
                }
            }
            int blocksPlaced = 0;
            while (blocksPlaced < this.bpt.getValue()) {
                if (this.offsetStep >= placeTargets.size()) {
                    this.offsetStep = 0;
                    break;
                }
                BlockPos offsetPos = new BlockPos((Vec3d)placeTargets.get(this.offsetStep));
                BlockPos targetPos = new BlockPos(this.closestTarget.func_174791_d()).func_177977_b().func_177982_a(offsetPos.field_177962_a, offsetPos.field_177960_b, offsetPos.field_177961_c);
                this.placeList.add(targetPos);
                try {
                    if (AttackUtil.isInterceptedByOtherTest(targetPos)) {
                        continue;
                    }
                }
                catch (Exception ex) {
                    Cascade.LOGGER.info("Caught an exception from Trap");
                    ex.printStackTrace();
                }
                if (targetPos == Mine.getInstance().currentPos) continue;
                if (AttackUtil.isInterceptedByCrystal(targetPos)) {
                    if (!this.attack.getValue().booleanValue()) continue;
                    EntityEnderCrystal crystal = null;
                    for (Entity entity : Trap.mc.theWorld.loadedEntityList) {
                        if (entity == null || Trap.mc.thePlayer.getDistanceToEntity(entity) > this.range.getValue().floatValue() || !(entity instanceof EntityEnderCrystal) || entity.isDead) continue;
                        crystal = (EntityEnderCrystal)entity;
                    }
                    if (crystal != null) {
                        if (this.rotate.getValue().booleanValue()) {
                            RotationUtil.faceEntity(crystal);
                        }
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketUseEntity(crystal));
                    }
                }
                if (this.placeBlockInRange(targetPos, this.range.getValue().floatValue())) {
                    ++blocksPlaced;
                }
                ++this.offsetStep;
            }
            if (blocksPlaced > 0 && this.isSneaking) {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)Trap.mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean placeBlockInRange(BlockPos pos, double range) {
        Block block = Trap.mc.theWorld.func_180495_p(pos).func_177230_c();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            this.placeList.remove(pos);
            return false;
        }
        for (Entity entity : Trap.mc.theWorld.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) continue;
            return true;
        }
        EnumFacing side = this.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        BlockPos neighbour = pos.func_177972_a(side);
        EnumFacing opposite = side.func_176734_d();
        if (!this.canBeClicked(neighbour)) {
            return false;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).addVector(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        Block neighbourBlock = Trap.mc.theWorld.func_180495_p(neighbour).func_177230_c();
        if (Trap.mc.thePlayer.func_174791_d().distanceTo(hitVec) > range) {
            return false;
        }
        int ogSlot = Trap.mc.thePlayer.inventory.currentItem;
        int obiSlot = this.findObiInHotbar();
        if (obiSlot == -1) {
            this.disable();
            return true;
        }
        if (this.timer.passedMs(this.delay.getValue().intValue())) {
            InventoryUtil.packetSwap(obiSlot);
            if (!this.isSneaking && blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock)) {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)Trap.mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
                this.isSneaking = true;
            }
            if (this.rotate.getValue().booleanValue()) {
                BlockUtil.faceVectorPacketInstant(hitVec);
            }
            BlockUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite, this.packet.getValue(), false);
            Trap.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
            Trap.mc.rightClickDelayTimer = 4;
            InventoryUtil.packetSwap(ogSlot);
        }
        return true;
    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = Trap.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == ItemStack.field_190927_a || !(stack.getItem() instanceof ItemBlock) || !((block = ((ItemBlock)stack.getItem()).func_179223_d()) instanceof BlockObsidian)) continue;
            slot = i;
            break;
        }
        return slot;
    }

    private void findClosestTarget() {
        List playerList = Trap.mc.theWorld.playerEntities;
        this.closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == Trap.mc.thePlayer || Cascade.friendManager.isFriend(target.getCommandSenderName()) || !EntityUtil.isLiving((Entity)target) || target.getDistanceToEntity((Entity)Trap.mc.thePlayer) > 7.0f || target.getHealth() <= 0.0f || Cascade.speedManager.getPlayerSpeed(target) > (double)this.maxTargetSpeed.getValue().floatValue()) continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (Trap.mc.thePlayer.getDistanceToEntity((Entity)target) >= Trap.mc.thePlayer.getDistanceToEntity((Entity)this.closestTarget)) continue;
            this.closestTarget = target;
        }
    }

    EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.func_177972_a(side);
            if (!Trap.mc.theWorld.func_180495_p(neighbour).func_177230_c().func_176209_a(Trap.mc.theWorld.func_180495_p(neighbour), false) || (blockState = Trap.mc.theWorld.func_180495_p(neighbour)).func_185904_a().isReplaceable()) continue;
            return side;
        }
        return null;
    }

    boolean canBeClicked(BlockPos pos) {
        return Trap.mc.theWorld.func_180495_p(pos).func_177230_c().func_176209_a(Trap.mc.theWorld.func_180495_p(pos), false);
    }

    @Override
    public String getDisplayInfo() {
        return this.ticks + "";
    }

    static enum Cage {
        Trap,
        TrapTop,
        TrapFullRoof,
        TrapFullRoofTop,
        CrystalExa,
        Crystal,
        CrystalFullRoof;

    }
}


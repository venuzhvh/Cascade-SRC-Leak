/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleFiller
extends Module {
    Setting<Integer> bpt = this.register(new Setting<Integer>("BPT", 8, 1, 25));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    Setting<Integer> predictBpt = this.register(new Setting<Object>("PredictBPT", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(8), v -> this.predict.getValue()));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Integer> toggle = this.register(new Setting<Integer>("AutoDisable", 10, 0, 250));
    Map<BlockPos, Integer> retries = new HashMap<BlockPos, Integer>();
    ArrayList<BlockPos> holes = new ArrayList();
    Timer retryTimer = new Timer();
    Timer offTimer = new Timer();
    Timer timer = new Timer();
    int placements = 0;
    int packets;
    int trie;

    public HoleFiller() {
        super("HoleFiller", Module.Category.COMBAT, "Fills holes around you");
    }

    @Override
    public void onEnable() {
        this.offTimer.reset();
        this.trie = 0;
    }

    @Override
    public void onDisable() {
        this.retries.clear();
        this.packets = 0;
    }

    @Override
    public void onUpdate() {
        if (!HoleFiller.fullNullCheck()) {
            mc.addScheduledTask(() -> this.doHoleFill());
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (HoleFiller.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.predict.getValue().booleanValue() && e.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange p = (SPacketBlockChange)e.getPacket();
            if (p.field_148883_d.func_177230_c() == Blocks.air && this.holes.contains(p.field_179828_a) && this.predictBpt.getValue() <= this.packets) {
                mc.addScheduledTask(() -> this.doHoleFill());
                ++this.packets;
            }
        }
    }

    private void doHoleFill() {
        if (this.check()) {
            return;
        }
        this.holes = new ArrayList();
        Iterable blocks = BlockPos.func_177980_a((BlockPos)HoleFiller.mc.thePlayer.func_180425_c().func_177963_a((double)(-this.range.getValue().floatValue()), (double)(-this.range.getValue().floatValue()), (double)(-this.range.getValue().floatValue())), (BlockPos)HoleFiller.mc.thePlayer.func_180425_c().func_177963_a((double)this.range.getValue().floatValue(), (double)this.range.getValue().floatValue(), (double)this.range.getValue().floatValue()));
        for (BlockPos pos : blocks) {
            boolean solidNeighbours;
            if (HoleFiller.mc.theWorld.func_180495_p(pos).func_185904_a().blocksMovement() || HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 1, 0)).func_185904_a().blocksMovement()) continue;
            boolean bl = HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.bedrock | HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(1, 0, 0)).func_177230_c() == Blocks.obsidian && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.bedrock | HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 0, 1)).func_177230_c() == Blocks.obsidian && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.bedrock | HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(-1, 0, 0)).func_177230_c() == Blocks.obsidian && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.bedrock | HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 0, -1)).func_177230_c() == Blocks.obsidian && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 0, 0)).func_185904_a() == Material.air && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 1, 0)).func_185904_a() == Material.air && HoleFiller.mc.theWorld.func_180495_p(pos.func_177982_a(0, 2, 0)).func_185904_a() == Material.air ? true : (solidNeighbours = false);
            if (!solidNeighbours) continue;
            this.holes.add(pos);
        }
        if (!this.holes.isEmpty()) {
            this.holes.forEach(this::placeBlock);
            if (this.toggle.getValue() != 0 && this.offTimer.passedMs(this.toggle.getValue().intValue())) {
                this.disable();
                return;
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        for (Entity entity : HoleFiller.mc.theWorld.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityLivingBase)) continue;
            return;
        }
        if (this.placements < this.bpt.getValue()) {
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && eChestSot == -1) {
                this.disable();
                return;
            }
            int originalSlot = HoleFiller.mc.thePlayer.inventory.currentItem;
            InventoryUtil.packetSwap(obbySlot != -1 ? obbySlot : eChestSot);
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), HoleFiller.mc.thePlayer.isSneaking(), true);
            InventoryUtil.packetSwap(originalSlot);
            this.timer.reset();
            ++this.placements;
        }
    }

    private boolean check() {
        if (HoleFiller.fullNullCheck()) {
            return true;
        }
        this.placements = 0;
        if (this.retryTimer.passedMs(250L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        return !this.timer.passedMs((long)((double)this.delay.getValue().intValue() * 10.0));
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockEnderChest
 *  net.minecraft.block.BlockObsidian
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.server.SPacketBlockChange
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
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
import cascade.util.player.MovementUtil;
import cascade.util.player.PlayerUtil;
import cascade.util.player.RotationUtil;
import java.util.ArrayList;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround
extends Module {
    Setting<Integer> bpt = this.register(new Setting<Integer>("BPT", 8, 1, 8));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    Setting<Integer> predictBpt = this.register(new Setting<Object>("PredictBPT", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(8), v -> this.predict.getValue()));
    Setting<Center> center = this.register(new Setting<Center>("Center", Center.None));
    Setting<Boolean> centerY = this.register(new Setting<Object>("CenterY", Boolean.valueOf(false), v -> this.center.getValue() == Center.Instant));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    Setting<Boolean> extend = this.register(new Setting<Boolean>("Extend", true));
    Vec3d CenterPos = Vec3d.field_186680_a;
    Timer timer = new Timer();
    int placements = 0;
    int packets = 0;
    double startY;

    public Surround() {
        super("Surround", Module.Category.COMBAT, "Surrounds you with blocks");
    }

    @Override
    public void onEnable() {
        if (Surround.fullNullCheck()) {
            return;
        }
        this.startY = Surround.mc.thePlayer.posY;
        this.CenterPos = EntityUtil.getCenter(Surround.mc.thePlayer.posX, Surround.mc.thePlayer.posY, Surround.mc.thePlayer.posZ);
        if (!(EntityUtil.isPlayerSafe((EntityPlayer)Surround.mc.thePlayer) || PlayerUtil.isChestBelow() || EntityUtil.isInLiquid() || Surround.mc.thePlayer.noClip)) {
            switch (this.center.getValue()) {
                case Instant: {
                    MovementUtil.setMotion(0.0, 0.0, 0.0);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : Surround.mc.thePlayer.posY, this.CenterPos.zCoord, true));
                    Surround.mc.thePlayer.setPosition(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : Surround.mc.thePlayer.posY, this.CenterPos.zCoord);
                    break;
                }
                case Motion: {
                    MovementUtil.setMotion((this.CenterPos.xCoord - Surround.mc.thePlayer.posX) / 2.0, Surround.mc.thePlayer.motionY, (this.CenterPos.zCoord - Surround.mc.thePlayer.posZ) / 2.0);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.packets = 0;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (Surround.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.predict.getValue().booleanValue() && e.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange p = (SPacketBlockChange)e.getPacket();
            if (p.field_148883_d.func_177230_c() == Blocks.air && Surround.mc.thePlayer.getDistance((double)p.field_179828_a.field_177962_a, (double)p.field_179828_a.field_177960_b, (double)p.field_179828_a.field_177961_c) < 1.75 && this.predictBpt.getValue() <= this.packets) {
                mc.addScheduledTask(() -> this.doFeetPlace());
                ++this.packets;
            }
        }
        if (!(!(e.getPacket() instanceof SPacketPlayerPosLook) || EntityUtil.isPlayerSafe((EntityPlayer)Surround.mc.thePlayer) || PlayerUtil.isChestBelow() || EntityUtil.isInLiquid() || Surround.mc.thePlayer.noClip)) {
            switch (this.center.getValue()) {
                case Instant: {
                    MovementUtil.setMotion(0.0, 0.0, 0.0);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : Surround.mc.thePlayer.posY, this.CenterPos.zCoord, true));
                    Surround.mc.thePlayer.setPosition(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : Surround.mc.thePlayer.posY, this.CenterPos.zCoord);
                    break;
                }
                case Motion: {
                    MovementUtil.setMotion((this.CenterPos.xCoord - Surround.mc.thePlayer.posX) / 2.0, Surround.mc.thePlayer.motionY, (this.CenterPos.zCoord - Surround.mc.thePlayer.posZ) / 2.0);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!Surround.fullNullCheck()) {
            mc.addScheduledTask(() -> this.doFeetPlace());
        }
    }

    void doFeetPlace() {
        if (this.check()) {
            return;
        }
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        if (PlayerUtil.isChestBelow()) {
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(1.0, 0.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(-1.0, 0.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, 1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, -1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(1.0, 1.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(-1.0, 1.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 1.0, 1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 1.0, -1.0));
        } else {
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(1.0, -1.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(-1.0, -1.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, 1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, -1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(1.0, 0.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(-1.0, 0.0, 0.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, 1.0));
            offsets.add(Surround.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, -1.0));
        }
        ArrayList<BlockPos> blockPosList = new ArrayList<BlockPos>();
        for (Vec3d vec3d : offsets) {
            BlockPos pos = new BlockPos(vec3d);
            if (!Surround.mc.theWorld.func_180495_p(pos).func_185904_a().isReplaceable()) continue;
            blockPosList.add(pos);
        }
        if (blockPosList.isEmpty()) {
            return;
        }
        for (BlockPos blockPos : blockPosList) {
            if (this.placements > this.bpt.getValue()) {
                return;
            }
            if (blockPos == Mine.getInstance().currentPos) continue;
            if (this.extend.getValue().booleanValue()) {
                try {
                    for (Entity en : Surround.mc.theWorld.loadedEntityList) {
                        if (!(en instanceof EntityPlayer) || !Surround.mc.thePlayer.boundingBox.intersectsWith(en.func_174813_aQ())) continue;
                        offsets.add(en.func_174791_d().addVector(1.0, 0.0, 0.0));
                        offsets.add(en.func_174791_d().addVector(0.0, 0.0, 1.0));
                        offsets.add(en.func_174791_d().addVector(-1.0, 0.0, 0.0));
                        offsets.add(en.func_174791_d().addVector(0.0, 0.0, -1.0));
                    }
                }
                catch (Exception ex) {
                    Cascade.LOGGER.info("Caught an exception from Surround");
                    ex.printStackTrace();
                }
            }
            if (AttackUtil.isInterceptedByOther(blockPos)) continue;
            if (AttackUtil.isInterceptedByCrystal(blockPos)) {
                if (!this.attack.getValue().booleanValue()) continue;
                EntityEnderCrystal crystal = null;
                for (Entity entity : Surround.mc.theWorld.loadedEntityList) {
                    if (entity == null || (double)Surround.mc.thePlayer.getDistanceToEntity(entity) > 2.4 || !(entity instanceof EntityEnderCrystal) || entity.isDead) continue;
                    crystal = (EntityEnderCrystal)entity;
                }
                if (crystal != null) {
                    if (this.rotate.getValue().booleanValue()) {
                        RotationUtil.faceEntity(crystal);
                    }
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketUseEntity(crystal));
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                }
            }
            this.placeBlock(blockPos);
            ++this.placements;
        }
    }

    boolean check() {
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && ecSlot == -1) {
            this.disable();
            return true;
        }
        this.placements = 0;
        if (this.isDisabled()) {
            return true;
        }
        if (Surround.mc.thePlayer.posY > this.startY) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    void placeBlock(BlockPos pos) {
        int ogSlot = Surround.mc.thePlayer.inventory.currentItem;
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && ecSlot == -1) {
            this.disable();
            return;
        }
        BlockUtil.placeBlock(pos, this.packet.getValue(), this.rotate.getValue(), true);
        InventoryUtil.packetSwap(ogSlot);
    }

    static enum Center {
        None,
        Instant,
        Motion;

    }
}


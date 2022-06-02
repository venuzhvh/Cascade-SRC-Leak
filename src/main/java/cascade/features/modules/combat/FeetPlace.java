/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
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
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.AttackUtil;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.player.MovementUtil;
import cascade.util.player.PlayerUtil;
import cascade.util.player.RotationUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeetPlace
extends Module {
    public static boolean isPlacing;
    Setting<Integer> bpt = this.register(new Setting<Integer>("BPT", 8, 1, 8));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    Setting<Integer> predictBpt = this.register(new Setting<Object>("PredictBPT", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(8), v -> this.predict.getValue()));
    Setting<Center> center = this.register(new Setting<Center>("Center", Center.None));
    Setting<Boolean> centerY = this.register(new Setting<Object>("CenterY", Boolean.valueOf(false), v -> this.center.getValue() == Center.Instant));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    List<BlockPos> ghostPlace = new ArrayList<BlockPos>();
    List<Vec3d> offsets = new ArrayList<Vec3d>();
    Vec3d CenterPos = Vec3d.field_186680_a;
    Timer timer = new Timer();
    boolean didPlace = false;
    boolean isSneaking;
    int placements = 0;
    BlockPos renderPos;
    BlockPos startPos;
    int packets = 0;
    double startY;
    int isSafe;

    public FeetPlace() {
        super("FeetPlace", Module.Category.COMBAT, "Surrounds you with blocks");
    }

    @Override
    public void onToggle() {
        this.renderPos = null;
    }

    @Override
    public void onEnable() {
        if (FeetPlace.fullNullCheck()) {
            return;
        }
        this.startY = FeetPlace.mc.thePlayer.posY;
        this.startPos = EntityUtil.getRoundedBlockPos((Entity)FeetPlace.mc.thePlayer);
        this.CenterPos = EntityUtil.getCenter(FeetPlace.mc.thePlayer.posX, FeetPlace.mc.thePlayer.posY, FeetPlace.mc.thePlayer.posZ);
        if (!(EntityUtil.isPlayerSafe((EntityPlayer)FeetPlace.mc.thePlayer) || PlayerUtil.isChestBelow() || EntityUtil.isInLiquid() || FeetPlace.mc.thePlayer.noClip)) {
            switch (this.center.getValue()) {
                case Instant: {
                    MovementUtil.setMotion(0.0, 0.0, 0.0);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : FeetPlace.mc.thePlayer.posY, this.CenterPos.zCoord, true));
                    FeetPlace.mc.thePlayer.setPosition(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : FeetPlace.mc.thePlayer.posY, this.CenterPos.zCoord);
                    break;
                }
                case Motion: {
                    MovementUtil.setMotion((this.CenterPos.xCoord - FeetPlace.mc.thePlayer.posX) / 2.0, FeetPlace.mc.thePlayer.motionY, (this.CenterPos.zCoord - FeetPlace.mc.thePlayer.posZ) / 2.0);
                }
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        if (FeetPlace.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.predict.getValue().booleanValue() && e.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange p = (SPacketBlockChange)e.getPacket();
            if (p.field_148883_d.func_177230_c() == Blocks.air && FeetPlace.mc.thePlayer.getDistance((double)p.field_179828_a.field_177962_a, (double)p.field_179828_a.field_177960_b, (double)p.field_179828_a.field_177961_c) < 1.75 && this.predictBpt.getValue() <= this.packets) {
                mc.addScheduledTask(() -> this.doFeetPlace());
                ++this.packets;
            }
        }
        if (!(!(e.getPacket() instanceof SPacketPlayerPosLook) || EntityUtil.isPlayerSafe((EntityPlayer)FeetPlace.mc.thePlayer) || PlayerUtil.isChestBelow() || EntityUtil.isInLiquid() || FeetPlace.mc.thePlayer.noClip)) {
            switch (this.center.getValue()) {
                case Instant: {
                    MovementUtil.setMotion(0.0, 0.0, 0.0);
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : FeetPlace.mc.thePlayer.posY, this.CenterPos.zCoord, true));
                    FeetPlace.mc.thePlayer.setPosition(this.CenterPos.xCoord, this.centerY.getValue() != false ? this.CenterPos.yCoord : FeetPlace.mc.thePlayer.posY, this.CenterPos.zCoord);
                    break;
                }
                case Motion: {
                    MovementUtil.setMotion((this.CenterPos.xCoord - FeetPlace.mc.thePlayer.posX) / 2.0, FeetPlace.mc.thePlayer.motionY, (this.CenterPos.zCoord - FeetPlace.mc.thePlayer.posZ) / 2.0);
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!FeetPlace.fullNullCheck()) {
            mc.addScheduledTask(() -> this.doFeetPlace());
        }
    }

    @Override
    public void onDisable() {
        if (!FeetPlace.fullNullCheck()) {
            isPlacing = false;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            this.packets = 0;
        }
    }

    @Override
    public String getDisplayInfo() {
        switch (this.isSafe) {
            case 0: {
                return ChatFormatting.RED + "Unsafe";
            }
            case 1: {
                return ChatFormatting.YELLOW + "Safe";
            }
        }
        return ChatFormatting.GREEN + "Safe";
    }

    private void doFeetPlace() {
        if (this.check()) {
            return;
        }
        this.renderPos = null;
        this.ghostPlace = new ArrayList<BlockPos>();
        this.offsets = new ArrayList<Vec3d>();
        if (PlayerUtil.isChestBelow()) {
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(1.0, 0.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(-1.0, 0.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, 1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, -1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(1.0, 1.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(-1.0, 1.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 1.0, 1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 1.0, -1.0));
        } else {
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(1.0, -1.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(-1.0, -1.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, 1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, -1.0, -1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(1.0, 0.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(-1.0, 0.0, 0.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, 1.0));
            this.offsets.add(FeetPlace.mc.thePlayer.func_174791_d().addVector(0.0, 0.0, -1.0));
        }
        ArrayList<BlockPos> blockPosList = new ArrayList<BlockPos>();
        for (Vec3d vec3d : this.offsets) {
            BlockPos pos = new BlockPos(vec3d);
            if (!FeetPlace.mc.theWorld.func_180495_p(pos).func_185904_a().isReplaceable()) continue;
            blockPosList.add(pos);
        }
        if (blockPosList.isEmpty()) {
            return;
        }
        for (BlockPos blockPos : blockPosList) {
            if (this.placements > this.bpt.getValue()) {
                return;
            }
            if (AttackUtil.isInterceptedByOther(blockPos)) continue;
            if (AttackUtil.isInterceptedByCrystal(blockPos)) {
                if (!this.attack.getValue().booleanValue()) continue;
                EntityEnderCrystal crystal = null;
                for (Entity entity : FeetPlace.mc.theWorld.loadedEntityList) {
                    if (entity == null || (double)FeetPlace.mc.thePlayer.getDistanceToEntity(entity) > 2.4 || !(entity instanceof EntityEnderCrystal) || entity.isDead) continue;
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
            this.renderPos = blockPos;
            this.placeBlock(this.renderPos);
            ++this.placements;
        }
    }

    private boolean check() {
        if (FeetPlace.nullCheck()) {
            return true;
        }
        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
        if (obbySlot == -1 && ecSlot == -1) {
            this.disable();
            return true;
        }
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        if (this.isDisabled()) {
            return true;
        }
        this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
        if (FeetPlace.mc.thePlayer.posY > this.startY) {
            this.disable();
            return true;
        }
        return !this.timer.passedMs(this.delay.getValue().intValue());
    }

    void placeBlock(BlockPos pos) {
        try {
            int ogSlot = FeetPlace.mc.thePlayer.inventory.currentItem;
            int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            int ecSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
            if (obbySlot == -1 && ecSlot == -1) {
                this.disable();
                return;
            }
            isPlacing = true;
            this.isSneaking = BlockUtil.placeBlock(pos, this.packet.getValue(), this.rotate.getValue(), true);
            InventoryUtil.packetSwap(ogSlot);
            this.didPlace = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static enum Center {
        None,
        Instant,
        Motion;

    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.combat;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.AttackUtil;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.player.PlayerUtil;
import cascade.util.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public class SelfFill
extends Module {
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    Setting<Block> prefer = this.register(new Setting<Block>("Prefer", Block.EChest));
    Setting<Double> offset = this.register(new Setting<Double>("Offset", 1.0, -9.0, 9.0));
    BlockPos startPos = null;

    public SelfFill() {
        super("SelfFill", Module.Category.COMBAT, "Lags u into a block");
    }

    @Override
    public void onEnable() {
        if (SelfFill.fullNullCheck()) {
            return;
        }
        if (!SelfFill.mc.thePlayer.onGround) {
            return;
        }
        this.startPos = new BlockPos(SelfFill.mc.thePlayer.posX, SelfFill.mc.thePlayer.posY, SelfFill.mc.thePlayer.posZ);
        if (SelfFill.mc.theWorld.checkBlockCollision(SelfFill.mc.thePlayer.boundingBox.addCoord(0.0, 1.0, 0.0)) || !SelfFill.mc.theWorld.func_180495_p((BlockPos)this.startPos).func_177230_c().blockMaterial.isReplaceable()) {
            return;
        }
    }

    @Override
    public void onDisable() {
        if (SelfFill.fullNullCheck()) {
            return;
        }
        this.startPos = null;
    }

    @Override
    public void onUpdate() {
        if (SelfFill.fullNullCheck()) {
            return;
        }
        int oglSlot = SelfFill.mc.thePlayer.inventory.currentItem;
        int ecSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((net.minecraft.block.Block)Blocks.ender_chest));
        int obbySlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock((net.minecraft.block.Block)Blocks.obsidian));
        if (ecSlot == -1 && obbySlot == -1) {
            this.disable();
            return;
        }
        if (this.shouldReturn()) {
            return;
        }
        if (AttackUtil.isInterceptedByCrystal(this.startPos)) {
            if (!this.attack.getValue().booleanValue()) {
                return;
            }
            EntityEnderCrystal crystal = null;
            for (Entity entity : SelfFill.mc.theWorld.loadedEntityList) {
                if (entity == null || (double)SelfFill.mc.thePlayer.getDistanceToEntity(entity) > 1.75 || !(entity instanceof EntityEnderCrystal) || entity.isDead) continue;
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
        if (this.prefer.getValue() == Block.EChest) {
            if (ecSlot != -1) {
                InventoryUtil.packetSwap(ecSlot);
            } else if (obbySlot != -1) {
                InventoryUtil.packetSwap(obbySlot);
            }
        }
        if (this.prefer.getValue() == Block.Obsidian) {
            if (obbySlot != -1) {
                InventoryUtil.packetSwap(obbySlot);
            } else if (ecSlot != -1) {
                InventoryUtil.packetSwap(ecSlot);
            }
        }
        EntityUtil.startSneaking();
        PlayerUtil.packetJump(false);
        BlockUtil.placeBlock(this.startPos, EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), false, true);
        mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(SelfFill.mc.thePlayer.posX, SelfFill.mc.thePlayer.posY + this.offset.getValue(), SelfFill.mc.thePlayer.posZ, SelfFill.mc.thePlayer.onGround));
        InventoryUtil.packetSwap(oglSlot);
        EntityUtil.stopSneaking(false);
    }

    public boolean shouldReturn() {
        return PlayerUtil.isClipping() || this.startPos.func_177956_o() > 255 || !SelfFill.mc.thePlayer.onGround;
    }

    static enum Block {
        EChest,
        Obsidian;

    }
}


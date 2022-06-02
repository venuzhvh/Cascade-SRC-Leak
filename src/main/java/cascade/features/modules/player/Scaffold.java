/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.player.MovementUtil;
import cascade.util.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold
extends Module {
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Center> center = this.register(new Setting<Center>("Center", Center.None));
    Timer timer = new Timer();

    public Scaffold() {
        super("Scaffold", Module.Category.PLAYER, "Places blocks under ur feet");
    }

    @Override
    public void onEnable() {
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        BlockPos playerBlock;
        if (this.isDisabled() || e.getStage() == 0) {
            return;
        }
        if (!Scaffold.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
            this.timer.reset();
        }
        if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).func_177982_a(0, -1, 0))) {
            if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -2, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.UP);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 0))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, -1))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                this.place(playerBlock.func_177982_a(0, -1, 0), EnumFacing.NORTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.NORTH);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.EAST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(-1, -1, 0))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.WEST);
                }
                this.place(playerBlock.func_177982_a(-1, -1, 1), EnumFacing.SOUTH);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.SOUTH);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.WEST);
            } else if (BlockUtil.isValidBlock(playerBlock.func_177982_a(1, -1, 1))) {
                if (BlockUtil.isValidBlock(playerBlock.func_177982_a(0, -1, 1))) {
                    this.place(playerBlock.func_177982_a(0, -1, 1), EnumFacing.EAST);
                }
                this.place(playerBlock.func_177982_a(1, -1, 1), EnumFacing.NORTH);
            }
        }
    }

    void place(BlockPos posI, EnumFacing face) {
        Block block;
        BlockPos pos = posI;
        if (face == EnumFacing.UP) {
            pos = pos.func_177982_a(0, -1, 0);
        } else if (face == EnumFacing.NORTH) {
            pos = pos.func_177982_a(0, 0, 1);
        } else if (face == EnumFacing.SOUTH) {
            pos = pos.func_177982_a(0, 0, -1);
        } else if (face == EnumFacing.EAST) {
            pos = pos.func_177982_a(-1, 0, 0);
        } else if (face == EnumFacing.WEST) {
            pos = pos.func_177982_a(1, 0, 0);
        }
        int oldSlot = Scaffold.mc.thePlayer.inventory.currentItem;
        int newSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = Scaffold.mc.thePlayer.inventory.getStackInSlot(i);
            if (InventoryUtil.isNull(stack) || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem((Item)stack.getItem()).func_176223_P().func_185913_b()) continue;
            newSlot = i;
            break;
        }
        if (newSlot == -1) {
            return;
        }
        boolean crouched = false;
        if (!Scaffold.mc.thePlayer.isSneaking() && BlockUtil.blackList.contains(block = Scaffold.mc.theWorld.func_180495_p(pos).func_177230_c())) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)Scaffold.mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
            crouched = true;
        }
        if (!(Scaffold.mc.thePlayer.func_184614_ca().getItem() instanceof ItemBlock)) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(newSlot));
            Scaffold.mc.thePlayer.inventory.currentItem = newSlot;
            Scaffold.mc.playerController.updateController();
        }
        if (Scaffold.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
            EntityPlayerSP player = Scaffold.mc.thePlayer;
            player.motionX *= 0.3;
            EntityPlayerSP player2 = Scaffold.mc.thePlayer;
            player2.motionZ *= 0.3;
            Scaffold.mc.thePlayer.jump();
            Vec3d CenterPos = EntityUtil.getCenter(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY, Scaffold.mc.thePlayer.posZ);
            if (!(EntityUtil.isPlayerSafe((EntityPlayer)Scaffold.mc.thePlayer) || PlayerUtil.isChestBelow() || EntityUtil.isInLiquid())) {
                switch (this.center.getValue()) {
                    case Instant: {
                        MovementUtil.setMotion(0.0, 0.0, 0.0);
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(CenterPos.xCoord, Scaffold.mc.thePlayer.posY, CenterPos.zCoord, true));
                        Scaffold.mc.thePlayer.setPosition(CenterPos.xCoord, Scaffold.mc.thePlayer.posY, CenterPos.zCoord);
                        break;
                    }
                    case NCP: {
                        MovementUtil.setMotion((CenterPos.xCoord - Scaffold.mc.thePlayer.posX) / 2.0, Scaffold.mc.thePlayer.motionY, (CenterPos.zCoord - Scaffold.mc.thePlayer.posZ) / 2.0);
                    }
                }
            }
            if (this.timer.passedMs(1500L)) {
                Scaffold.mc.thePlayer.motionY = -0.28;
                this.timer.reset();
            }
        }
        if (this.rotate.getValue().booleanValue()) {
            float[] angle = MathUtil.calcAngle(Scaffold.mc.thePlayer.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5f), (double)((float)pos.func_177956_o() - 0.5f), (double)((float)pos.func_177952_p() + 0.5f)));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Rotation(angle[0], (float)MathHelper.func_180184_b((int)((int)angle[1]), (int)360), Scaffold.mc.thePlayer.onGround));
        }
        Scaffold.mc.playerController.func_187099_a(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, pos, face, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
        Scaffold.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
        mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(oldSlot));
        Scaffold.mc.thePlayer.inventory.currentItem = oldSlot;
        Scaffold.mc.playerController.updateController();
        if (crouched) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)Scaffold.mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    static enum Center {
        None,
        Instant,
        NCP;

    }
}


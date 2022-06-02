/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$PositionRotation
 *  net.minecraft.network.play.client.CPacketPlayer$Rotation
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.event.events.PacketEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BowSpam
extends Module {
    Setting<Integer> ticks = this.register(new Setting<Integer>("Ticks", 3, 0, 21));
    Setting<Boolean> cancelRot = this.register(new Setting<Boolean>("CancelRot", false));
    Setting<Boolean> normal = this.register(new Setting<Boolean>("Normal", true));
    Setting<Boolean> bowBomb = this.register(new Setting<Boolean>("BowBomb", false));
    Setting<Boolean> rape = this.register(new Setting<Boolean>("Rape", false));
    Setting<Float> timer = this.register(new Setting<Object>("Timer", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.rape.getValue()));

    public BowSpam() {
        super("BowSpam", Module.Category.COMBAT, "automatically releases bows");
    }

    @Override
    public void onToggle() {
        if (BowSpam.fullNullCheck()) {
            return;
        }
        if (this.rape.getValue().booleanValue()) {
            Cascade.timerManager.reset();
        }
    }

    @Override
    public void onUpdate() {
        if (BowSpam.fullNullCheck()) {
            return;
        }
        if (this.normal.getValue().booleanValue() && BowSpam.mc.thePlayer.func_184614_ca().getItem() instanceof ItemBow && BowSpam.mc.thePlayer.func_184587_cr() && BowSpam.mc.thePlayer.func_184612_cw() >= this.ticks.getValue()) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, BowSpam.mc.thePlayer.func_174811_aO()));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItem(BowSpam.mc.thePlayer.func_184600_cs()));
            BowSpam.mc.thePlayer.func_184597_cx();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (BowSpam.fullNullCheck() || this.isDisabled()) {
            return;
        }
        ItemStack stack = this.getStack();
        if (this.rape.getValue().booleanValue() && BowSpam.mc.thePlayer.onGround && stack != null && !BowSpam.mc.thePlayer.func_184607_cu().func_190926_b() && BowSpam.mc.thePlayer.func_184605_cv() > 0) {
            Cascade.timerManager.set(this.timer.getValue().floatValue());
            if ((float)(stack.getMaxItemUseDuration() - BowSpam.mc.thePlayer.func_184605_cv()) > (float)this.ticks.getValue().intValue() * this.timer.getValue().floatValue()) {
                BowSpam.mc.playerController.onStoppedUsingItem((EntityPlayer)BowSpam.mc.thePlayer);
            }
        }
        if (this.bowBomb.getValue().booleanValue() && stack != null && !BowSpam.mc.thePlayer.func_184607_cu().func_190926_b() && stack.getMaxItemUseDuration() - BowSpam.mc.thePlayer.func_184605_cv() >= this.ticks.getValue()) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.thePlayer.posX, BowSpam.mc.thePlayer.posY - 0.0624, BowSpam.mc.thePlayer.posZ, BowSpam.mc.thePlayer.rotationYaw, BowSpam.mc.thePlayer.rotationPitch, false));
            mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.PositionRotation(BowSpam.mc.thePlayer.posX, BowSpam.mc.thePlayer.posY - 999.0, BowSpam.mc.thePlayer.posZ, BowSpam.mc.thePlayer.rotationYaw, BowSpam.mc.thePlayer.rotationPitch, true));
            BowSpam.mc.playerController.onStoppedUsingItem((EntityPlayer)BowSpam.mc.thePlayer);
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (BowSpam.fullNullCheck() || this.isDisabled()) {
            return;
        }
        ItemStack stack = this.getStack();
        if (this.rape.getValue().booleanValue() && BowSpam.mc.thePlayer.onGround && stack != null && !BowSpam.mc.thePlayer.func_184607_cu().func_190926_b() && BowSpam.mc.thePlayer.func_184605_cv() > 0) {
            event.setX(0.0);
            event.setY(0.0);
            event.setZ(0.0);
            BowSpam.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (BowSpam.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof CPacketPlayer.Rotation && this.cancelRot.getValue().booleanValue()) {
            e.setCanceled(true);
        }
    }

    private ItemStack getStack() {
        ItemStack mainHand = BowSpam.mc.thePlayer.func_184614_ca();
        if (mainHand.getItem() instanceof ItemBow) {
            return mainHand;
        }
        ItemStack offHand = BowSpam.mc.thePlayer.func_184592_cb();
        if (offHand.getItem() instanceof ItemBow) {
            return offHand;
        }
        return null;
    }
}


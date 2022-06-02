/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 */
package cascade.mixin.mixins;

import cascade.event.events.LiquidJumpEvent;
import cascade.features.modules.player.FastEat;
import cascade.mixin.mixins.MixinEntity;
import cascade.util.Util;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityLivingBase.class})
public abstract class MixinEntityLivingBase
extends MixinEntity {
    @Shadow
    protected int field_184628_bn;
    @Shadow
    protected ItemStack field_184627_bm;

    @Redirect(method={"onItemUseFinish"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/EntityLivingBase;resetActiveHand()V"))
    private void resetActiveHandHook(EntityLivingBase base) {
        if (FastEat.getInstance().isEnabled() && base instanceof EntityPlayerSP && !Util.mc.isSingleplayer() && FastEat.getInstance().mode.getValue() == FastEat.Mode.NoDelay && this.field_184627_bm.getItem() instanceof ItemFood) {
            this.field_184628_bn = 0;
            ((EntityPlayerSP)base).sendQueue.addToSendQueue((Packet)new CPacketPlayerTryUseItem(base.func_184600_cs()));
        } else {
            base.func_184602_cy();
        }
    }

    @Inject(method={"handleJumpWater"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleJumpWaterHook(CallbackInfo info) {
        LiquidJumpEvent event = new LiquidJumpEvent((EntityLivingBase)EntityLivingBase.class.cast(this));
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method={"handleJumpLava"}, at={@At(value="HEAD")}, cancellable=true)
    private void handleJumpLavaHook(CallbackInfo info) {
        LiquidJumpEvent event = new LiquidJumpEvent((EntityLivingBase)EntityLivingBase.class.cast(this));
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }
}


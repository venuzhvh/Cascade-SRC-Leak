/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketChatMessage
 *  net.minecraft.network.play.client.CPacketConfirmTeleport
 *  net.minecraft.network.play.client.CPacketKeepAlive
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItem
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketVehicleMove
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraft.network.play.server.SPacketSetPassengers
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.player;

import cascade.event.events.PacketEvent;
import cascade.event.events.PushEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam
extends Module {
    Setting<Float> speed = this.register(new Setting<Float>("Speed", Float.valueOf(0.2f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    Setting<Boolean> view = this.register(new Setting<Boolean>("3D", false));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> legit = this.register(new Setting<Boolean>("Legit", false));
    Setting<Boolean> noSuicide = this.register(new Setting<Boolean>("NoSuicide", true));
    Setting<Float> health = this.register(new Setting<Object>("Health", Float.valueOf(18.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.noSuicide.getValue()));
    AxisAlignedBB oldBoundingBox;
    public static EntityOtherPlayerMP entity;
    static Freecam INSTANCE;
    Vec3d position;
    Entity riding;
    float yaw;
    float pitch;

    public Freecam() {
        super("Freecam", Module.Category.PLAYER, "Lets u look around freely");
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Freecam getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Freecam();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (!Freecam.fullNullCheck()) {
            this.oldBoundingBox = Freecam.mc.thePlayer.func_174813_aQ();
            Freecam.mc.thePlayer.func_174826_a(new AxisAlignedBB(Freecam.mc.thePlayer.posX, Freecam.mc.thePlayer.posY, Freecam.mc.thePlayer.posZ, Freecam.mc.thePlayer.posX, Freecam.mc.thePlayer.posY, Freecam.mc.thePlayer.posZ));
            if (Freecam.mc.thePlayer.func_184187_bx() != null) {
                this.riding = Freecam.mc.thePlayer.func_184187_bx();
                Freecam.mc.thePlayer.func_184210_p();
            }
            entity = new EntityOtherPlayerMP((World)Freecam.mc.theWorld, Freecam.mc.session.getProfile());
            entity.copyLocationAndAnglesFrom((Entity)Freecam.mc.thePlayer);
            Freecam.entity.rotationYaw = Freecam.mc.thePlayer.rotationYaw;
            Freecam.entity.rotationYawHead = Freecam.mc.thePlayer.rotationYawHead;
            Freecam.entity.inventory.copyInventory(Freecam.mc.thePlayer.inventory);
            Freecam.mc.theWorld.addEntityToWorld(726804364, (Entity)entity);
            this.position = Freecam.mc.thePlayer.func_174791_d();
            this.yaw = Freecam.mc.thePlayer.rotationYaw;
            this.pitch = Freecam.mc.thePlayer.rotationPitch;
            Freecam.mc.thePlayer.noClip = true;
        }
    }

    @Override
    public void onDisable() {
        if (!Freecam.fullNullCheck()) {
            Freecam.mc.thePlayer.func_174826_a(this.oldBoundingBox);
            if (this.riding != null) {
                Freecam.mc.thePlayer.func_184205_a(this.riding, true);
            }
            if (entity != null) {
                Freecam.mc.theWorld.removeEntity((Entity)entity);
            }
            if (this.position != null) {
                Freecam.mc.thePlayer.setPosition(this.position.xCoord, this.position.yCoord, this.position.zCoord);
            }
            Freecam.mc.thePlayer.rotationYaw = this.yaw;
            Freecam.mc.thePlayer.rotationPitch = this.pitch;
            Freecam.mc.thePlayer.noClip = false;
        }
    }

    @Override
    public void onUpdate() {
        if (Freecam.fullNullCheck()) {
            return;
        }
        if (this.noSuicide.getValue().booleanValue() && (EntityUtil.getHealth((Entity)Freecam.mc.thePlayer) <= this.health.getValue().floatValue() || EntityUtil.getHealth((Entity)entity) <= this.health.getValue().floatValue())) {
            this.disable();
            return;
        }
        Freecam.mc.thePlayer.noClip = true;
        Freecam.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
        Freecam.mc.thePlayer.jumpMovementFactor = this.speed.getValue().floatValue();
        double[] dir = MathUtil.directionSpeed(this.speed.getValue().floatValue());
        if (MovementUtil.isMoving()) {
            MovementUtil.setMotion(dir[0], Freecam.mc.thePlayer.motionY, dir[1]);
        } else {
            Freecam.mc.thePlayer.motionX = 0.0;
            Freecam.mc.thePlayer.motionZ = 0.0;
        }
        Freecam.mc.thePlayer.setSprinting(false);
        if (this.view.getValue().booleanValue() && !Freecam.mc.gameSettings.keyBindSneak.getIsKeyPressed() && !Freecam.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
            Freecam.mc.thePlayer.motionY = (double)this.speed.getValue().floatValue() * -MathUtil.degToRad(Freecam.mc.thePlayer.rotationPitch) * (double)Freecam.mc.thePlayer.movementInput.field_192832_b;
        }
        if (Freecam.mc.gameSettings.keyBindJump.getIsKeyPressed()) {
            Freecam.mc.thePlayer.motionY += (double)this.speed.getValue().floatValue();
        }
        if (Freecam.mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
            Freecam.mc.thePlayer.motionY -= (double)this.speed.getValue().floatValue();
        }
    }

    @Override
    public void onLogout() {
        if (this.isEnabled()) {
            this.disable();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        if (this.isDisabled()) {
            return;
        }
        if (this.legit.getValue().booleanValue() && entity != null && e.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer)e.getPacket()).x = Freecam.entity.posX;
            ((CPacketPlayer)e.getPacket()).y = Freecam.entity.posY;
            ((CPacketPlayer)e.getPacket()).z = Freecam.entity.posZ;
            return;
        }
        if (this.packet.getValue().booleanValue()) {
            if (e.getPacket() instanceof CPacketPlayer) {
                e.setCanceled(true);
            }
        } else if (!(e.getPacket() instanceof CPacketUseEntity || e.getPacket() instanceof CPacketPlayerTryUseItem || e.getPacket() instanceof CPacketPlayerTryUseItemOnBlock || e.getPacket() instanceof CPacketPlayer || e.getPacket() instanceof CPacketVehicleMove || e.getPacket() instanceof CPacketChatMessage || e.getPacket() instanceof CPacketKeepAlive)) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        Entity riding;
        if (this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketSetPassengers && (riding = Freecam.mc.theWorld.getEntityByID(((SPacketSetPassengers)e.getPacket()).func_186972_b())) != null && riding == this.riding) {
            this.riding = null;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook p = (SPacketPlayerPosLook)e.getPacket();
            if (this.packet.getValue().booleanValue()) {
                if (entity != null) {
                    entity.setPositionAndRotation(p.func_148932_c(), p.func_148928_d(), p.func_148933_e(), p.func_148931_f(), p.func_148930_g());
                }
                this.position = new Vec3d(p.func_148932_c(), p.func_148928_d(), p.func_148933_e());
                Freecam.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketConfirmTeleport(p.func_186965_f()));
                e.setCanceled(true);
            } else {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent e) {
        if (e.getStage() == 1 && this.isEnabled()) {
            e.setCanceled(true);
        }
    }
}


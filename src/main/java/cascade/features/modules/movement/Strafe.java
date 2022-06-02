/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.network.play.server.SPacketPlayerPosLook
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.event.events.PacketEvent;
import cascade.event.events.PushEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ITimer;
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import java.util.Objects;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe
extends Module {
    Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    Setting<Sneaking> sneaking = this.register(new Setting<Sneaking>("Sneaking", Sneaking.Cancel));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));
    static Strafe INSTANCE;
    double distance;
    double lastDist;
    boolean boost;
    double speed;
    int stage;

    public Strafe() {
        super("Strafe", Module.Category.MOVEMENT, "Lets u go fast as fuck");
        INSTANCE = this;
    }

    public static Strafe getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Strafe();
        }
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (Strafe.mc.thePlayer != null) {
            this.speed = MovementUtil.getSpeed();
            this.distance = MovementUtil.getDistance2D();
        }
        this.stage = 4;
        this.lastDist = 0.0;
    }

    @Override
    public void onDisable() {
        if (Strafe.mc.thePlayer == null) {
            return;
        }
        if (this.step.getValue().booleanValue()) {
            Strafe.mc.thePlayer.stepHeight = 0.6f;
        }
        if (this.useTimer.getValue().booleanValue() && ((ITimer)Strafe.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!MovementUtil.isMoving() || this.isDisabled() || Strafe.mc.thePlayer.func_184613_cA()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid() || Strafe.mc.thePlayer.isOnLadder() || Strafe.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            return;
        }
        if (this.sneaking.getValue() == Sneaking.Pause && Strafe.mc.thePlayer.isSneaking()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (this.useTimer.getValue().booleanValue()) {
            Cascade.timerManager.set(1.0888f);
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        if (this.stage == 1 && MovementUtil.isMoving()) {
            this.speed = 1.35 * MovementUtil.getSpeed(0.2873) - 0.01;
        } else if (this.stage == 2 && MovementUtil.isMoving()) {
            if (!EntityUtil.isInLiquid() && !Strafe.mc.thePlayer.isInWeb) {
                double yMotion;
                Strafe.mc.thePlayer.motionY = yMotion = 0.3999 + MovementUtil.getJumpSpeed();
                event.setY(yMotion);
            }
            this.speed *= this.boost ? 1.6835 : 1.395;
        } else if (this.stage == 3) {
            this.speed = this.distance - 0.66 * (this.distance - MovementUtil.getSpeed(0.2873));
            this.boost = !this.boost;
        } else {
            if ((Strafe.mc.theWorld.func_184144_a(null, Strafe.mc.thePlayer.func_174813_aQ().offset(0.0, Strafe.mc.thePlayer.motionY, 0.0)).size() > 0 || Strafe.mc.thePlayer.isCollidedVertically) && this.stage > 0) {
                this.stage = MovementUtil.isMoving() ? 1 : 0;
            }
            this.speed = this.distance - this.distance / 159.0;
        }
        this.speed = Math.min(this.speed, this.getCap());
        this.speed = Math.max(this.speed, MovementUtil.getSpeed(0.2873));
        MovementUtil.strafe(event, this.speed);
        if (MovementUtil.isMoving()) {
            ++this.stage;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (!MovementUtil.isWasdPressed()) {
            MovementUtil.setMotion(0.0, Strafe.mc.thePlayer.motionY, 0.0);
        }
        this.distance = MovementUtil.getDistance2D();
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive e) {
        if (this.isDisabled() || Strafe.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            this.distance = 0.0;
            this.speed = 0.0;
            this.stage = 4;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send e) {
        CPacketEntityAction p;
        if (this.isDisabled() || Strafe.fullNullCheck()) {
            return;
        }
        if (e.getPacket() instanceof CPacketEntityAction && this.sneaking.getValue() == Sneaking.Cancel && (p = (CPacketEntityAction)e.getPacket()).func_180764_b() == CPacketEntityAction.Action.START_SNEAKING) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPush(PushEvent e) {
        if (this.isEnabled()) {
            e.setCanceled(true);
        }
    }

    double getCap() {
        int amplifier;
        double ret = 10.0;
        if (Strafe.mc.thePlayer.isPotionActive(MobEffects.moveSpeed)) {
            amplifier = Objects.requireNonNull(Strafe.mc.thePlayer.getActivePotionEffect(MobEffects.moveSpeed)).getAmplifier();
            ret *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (Strafe.mc.thePlayer.isPotionActive(MobEffects.moveSlowdown)) {
            amplifier = Objects.requireNonNull(Strafe.mc.thePlayer.getActivePotionEffect(MobEffects.moveSlowdown)).getAmplifier();
            ret /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return ret;
    }

    static enum Sneaking {
        None,
        Pause,
        Cancel;

    }
}


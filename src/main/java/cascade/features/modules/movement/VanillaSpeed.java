/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.Cascade;
import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.mixin.mixins.accessor.ITimer;
import cascade.util.player.MovementUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaSpeed
extends Module {
    Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", false));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));
    Setting<Double> speed = this.register(new Setting<Double>("Speed", 5.0, 0.0, 20.0));

    public VanillaSpeed() {
        super("VanillaSpeed", Module.Category.MOVEMENT, "VSpeed");
    }

    @Override
    public void onDisable() {
        if (VanillaSpeed.fullNullCheck()) {
            return;
        }
        if (this.useTimer.getValue().booleanValue() && ((ITimer)VanillaSpeed.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
        if (this.step.getValue().booleanValue()) {
            VanillaSpeed.mc.thePlayer.stepHeight = 0.6f;
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.isDisabled() || VanillaSpeed.fullNullCheck()) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (this.useTimer.getValue().booleanValue()) {
            Cascade.timerManager.set(1.0888f);
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        double[] dir = MovementUtil.strafe(this.speed.getValue() / 10.0);
        e.setX(dir[0]);
        e.setZ(dir[1]);
    }
}


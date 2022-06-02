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
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AirStrafe
extends Module {
    Setting<Boolean> useTimer = this.register(new Setting<Boolean>("UseTimer", true));
    Setting<Boolean> noLag = this.register(new Setting<Boolean>("NoLag", true));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    Setting<Boolean> step = this.register(new Setting<Boolean>("Step", true));
    Setting<Float> height = this.register(new Setting<Object>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f), v -> this.step.getValue()));

    public AirStrafe() {
        super("AirStrafe", Module.Category.MOVEMENT, "lets u strafe in air");
    }

    @Override
    public void onToggle() {
        if (!AirStrafe.fullNullCheck() && this.step.getValue().booleanValue()) {
            AirStrafe.mc.thePlayer.stepHeight = 0.6f;
        }
        if (this.useTimer.getValue().booleanValue() && ((ITimer)AirStrafe.mc.timer).getTickLength() != 50.0f) {
            Cascade.timerManager.reset();
        }
    }

    @SubscribeEvent
    public void onMove(MoveEvent e) {
        if (this.isDisabled() || AirStrafe.mc.thePlayer.func_184613_cA()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("HoleSnap") || Cascade.moduleManager.isModuleEnabled("Freecam") || Cascade.moduleManager.isModuleEnabled("YPort") || Cascade.moduleManager.isModuleEnabled("Strafe")) {
            return;
        }
        if (this.noLag.getValue().booleanValue() && Cascade.packetManager.caughtPlayerPosLook()) {
            return;
        }
        if (this.step.getValue().booleanValue()) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        if (this.useTimer.getValue().booleanValue()) {
            Cascade.timerManager.set(1.0888f);
        }
        MovementUtil.strafe(e, MovementUtil.getSpeed());
    }
}


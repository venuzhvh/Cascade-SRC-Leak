/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer$Position
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.StepEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.player.MovementUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step
extends Module {
    Setting<Mode> mode = this.register(new Setting<Mode>("Mode", Mode.Vanilla));
    Setting<Float> height = this.register(new Setting<Float>("Height", Float.valueOf(2.0f), Float.valueOf(0.1f), Float.valueOf(2.0f)));
    Setting<Boolean> noLiquid = this.register(new Setting<Boolean>("NoLiquid", true));
    Setting<Boolean> entityStep = this.register(new Setting<Boolean>("EntityStep", false));

    public Step() {
        super("Step", Module.Category.MOVEMENT, "Allows you to step up blocks");
    }

    @Override
    public void onDisable() {
        if (Step.mc.thePlayer.func_184218_aH()) {
            Step.mc.thePlayer.func_184187_bx().stepHeight = 1.0f;
        } else {
            Step.mc.thePlayer.stepHeight = 0.6f;
        }
    }

    @Override
    public void onUpdate() {
        if (Step.fullNullCheck()) {
            return;
        }
        if (this.noLiquid.getValue().booleanValue() && EntityUtil.isInLiquid()) {
            return;
        }
        if (this.mode.getValue() == Mode.Vanilla) {
            MovementUtil.step(this.height.getValue().floatValue());
        }
        if (Step.mc.thePlayer.func_184218_aH() && this.entityStep.getValue().booleanValue()) {
            Step.mc.thePlayer.func_184187_bx().stepHeight = 2.0f;
        }
    }

    @SubscribeEvent
    public void onStep(StepEvent e) {
        if (this.isDisabled() || !Step.mc.thePlayer.isCollidedVertically || (double)Step.mc.thePlayer.fallDistance > 0.1 || Step.mc.thePlayer.isOnLadder() || !Step.mc.thePlayer.onGround || this.mode.getValue() == Mode.Vanilla) {
            return;
        }
        double y = 0.0;
        if (e.getStage() == 0) {
            y = e.getBB().minY;
            e.setHeight(this.height.getValue().floatValue());
        } else {
            double height = e.getBB().minY - y;
            if (height > (double)e.getHeight()) {
                double[] offsets = new double[]{0.42, height < 1.0 && height > 0.8 ? 0.753 : 0.75, 1.0, 1.16, 1.23, 1.2};
                if (height >= 2.0) {
                    offsets = new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                }
                for (int i = 0; i < (height > 1.0 ? offsets.length : 2); ++i) {
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayer.Position(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + offsets[i], Step.mc.thePlayer.posZ, true));
                }
            }
        }
    }

    static enum Mode {
        Vanilla,
        NCP;

    }
}


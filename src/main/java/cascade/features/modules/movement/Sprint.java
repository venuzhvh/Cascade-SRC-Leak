/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.movement;

import cascade.event.events.MoveEvent;
import cascade.features.modules.Module;
import cascade.util.entity.EntityUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint
extends Module {
    public Sprint() {
        super("Sprint", Module.Category.MOVEMENT, "Modifies sprinting");
    }

    @SubscribeEvent
    public void onSprint(MoveEvent event) {
        if (Sprint.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (event.getStage() == 1 && !EntityUtil.isMoving()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        if (Sprint.canSprintBetter()) {
            Sprint.mc.thePlayer.setSprinting(true);
        }
    }

    @Override
    public void onDisable() {
        if (Sprint.mc.thePlayer != null) {
            Sprint.mc.thePlayer.setSprinting(false);
        }
    }

    public static boolean canSprintBetter() {
        return !(!Sprint.mc.gameSettings.keyBindForward.getIsKeyPressed() && !Sprint.mc.gameSettings.keyBindBack.getIsKeyPressed() && !Sprint.mc.gameSettings.keyBindLeft.getIsKeyPressed() && !Sprint.mc.gameSettings.keyBindRight.getIsKeyPressed() || Sprint.mc.thePlayer == null || Sprint.mc.thePlayer.isSneaking() || Sprint.mc.thePlayer.isCollidedHorizontally || (float)Sprint.mc.thePlayer.getFoodStats().getFoodLevel() <= 6.0f);
    }
}


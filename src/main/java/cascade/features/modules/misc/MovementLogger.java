/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.Cascade;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.Timer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MovementLogger
extends Module {
    Setting<Boolean> delay = this.register(new Setting<Boolean>("Delay", false));
    Setting<Boolean> pos = this.register(new Setting<Boolean>("Pos", false));
    Setting<Boolean> tickPos = this.register(new Setting<Boolean>("TickPos", false));
    Setting<Boolean> motion = this.register(new Setting<Boolean>("Motion", true));
    Setting<Boolean> calcMotion = this.register(new Setting<Boolean>("CalcMotion", true));
    Setting<Boolean> onGround = this.register(new Setting<Boolean>("OnGround", true));

    public MovementLogger() {
        super("MovementLogger", Module.Category.MISC, "hacke");
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (this.delay.getValue().booleanValue()) {
            Timer timer = new Timer();
            Cascade.LOGGER.info("Counter=" + timer.getTime());
            timer.reset();
        }
        if (this.pos.getValue().booleanValue()) {
            Cascade.LOGGER.info("posX=" + MovementLogger.mc.thePlayer.posX);
            Cascade.LOGGER.info("posY=" + MovementLogger.mc.thePlayer.posY);
            Cascade.LOGGER.info("posZ=" + MovementLogger.mc.thePlayer.posZ);
        }
        if (this.tickPos.getValue().booleanValue()) {
            Cascade.LOGGER.info("tickPosX=" + MovementLogger.mc.thePlayer.lastTickPosZ);
            Cascade.LOGGER.info("tickPosY=" + MovementLogger.mc.thePlayer.lastTickPosY);
            Cascade.LOGGER.info("tickPosZ=" + MovementLogger.mc.thePlayer.lastTickPosZ);
        }
        if (this.motion.getValue().booleanValue()) {
            Cascade.LOGGER.info("motionX=" + MovementLogger.mc.thePlayer.motionX);
            Cascade.LOGGER.info("motionY=" + MovementLogger.mc.thePlayer.motionY);
            Cascade.LOGGER.info("motionZ=" + MovementLogger.mc.thePlayer.motionZ);
        }
        if (this.calcMotion.getValue().booleanValue()) {
            Cascade.LOGGER.info("calcMotionX=" + (MovementLogger.mc.thePlayer.posX - MovementLogger.mc.thePlayer.lastTickPosX));
            Cascade.LOGGER.info("calcMotionY=" + (MovementLogger.mc.thePlayer.posY - MovementLogger.mc.thePlayer.lastTickPosY));
            Cascade.LOGGER.info("calcMotionZ=" + (MovementLogger.mc.thePlayer.posZ - MovementLogger.mc.thePlayer.lastTickPosZ));
        }
        if (this.onGround.getValue().booleanValue()) {
            Cascade.LOGGER.info("onGround=" + MovementLogger.mc.thePlayer.onGround);
        }
    }
}


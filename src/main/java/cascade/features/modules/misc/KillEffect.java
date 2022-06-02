/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.DeathEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KillEffect
extends Module {
    Setting<Boolean> lightning = this.register(new Setting<Boolean>("Lightning", true));
    Setting<Boolean> sound = this.register(new Setting<Object>("Sound", Boolean.valueOf(true), v -> this.lightning.getValue()));
    int ticks;

    public KillEffect() {
        super("KillEffect", Module.Category.MISC, "Renders effects when someone dies");
    }

    @Override
    public void onTick() {
        if (this.ticks < 20) {
            ++this.ticks;
        }
    }

    @SubscribeEvent
    public void onDeath(DeathEvent e) {
        if (KillEffect.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (this.lightning.getValue().booleanValue() && this.ticks == 20) {
            EntityLightningBolt bolt = new EntityLightningBolt((World)KillEffect.mc.theWorld, e.player.posX, e.player.posY, e.player.posZ, false);
            if (this.sound.getValue().booleanValue()) {
                KillEffect.mc.theWorld.func_184156_a(e.player.func_180425_c(), SoundEvents.field_187754_de, SoundCategory.WEATHER, 1.0f, 1.0f, false);
            }
            bolt.setLocationAndAngles(e.player.posX, e.player.posY, e.player.posZ, KillEffect.mc.thePlayer.rotationYaw, KillEffect.mc.thePlayer.rotationPitch);
            KillEffect.mc.theWorld.spawnEntityInWorld((Entity)bolt);
            this.ticks = 0;
        }
    }
}


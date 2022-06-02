/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.CombatUtil;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aura
extends Module {
    public static Entity target;
    Timer timer = new Timer();
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> swordOnly = this.register(new Setting<Boolean>("SwordOnly", true));
    Setting<Boolean> delay = this.register(new Setting<Boolean>("Delay", true));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Float> wallRange = this.register(new Setting<Float>("WallRange", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> tps = this.register(new Setting<Boolean>("TpsSync", true));
    Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    Setting<Boolean> mobs = this.register(new Setting<Boolean>("Mobs", false));
    Setting<Boolean> animals = this.register(new Setting<Boolean>("Animals", false));
    Setting<Boolean> vehicles = this.register(new Setting<Boolean>("Entities", false));
    Setting<Boolean> projectiles = this.register(new Setting<Boolean>("Projectiles", false));
    static Aura INSTANCE;

    public Aura() {
        super("Aura", Module.Category.COMBAT, "Automatically attacks targets");
        INSTANCE = this;
    }

    public static Aura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Aura();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (Aura.fullNullCheck()) {
            return;
        }
        if (!this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent e) {
        if (this.isDisabled()) {
            return;
        }
        if (e.getStage() == 0 && this.rotate.getValue().booleanValue()) {
            this.doKillaura();
        }
    }

    void doKillaura() {
        int wait;
        if (!CombatUtil.holdingWeapon() && this.swordOnly.getValue().booleanValue()) {
            target = null;
            return;
        }
        int n = this.delay.getValue() == false ? 0 : (wait = (int)((float)CombatUtil.getCooldownByWeapon((EntityPlayer)Aura.mc.thePlayer) * (this.tps.getValue() != false ? Cascade.serverManager.getTpsFactor() : 1.0f)));
        if (!this.timer.passedMs(wait)) {
            return;
        }
        target = this.getTarget();
        if (target == null) {
            return;
        }
        if (this.rotate.getValue().booleanValue()) {
            Cascade.rotationManager.lookAtEntity(target);
        }
        CombatUtil.attackEntity(target, this.packet.getValue(), true);
        this.timer.reset();
    }

    Entity getTarget() {
        Entity target = null;
        double distance = this.range.getValue().floatValue();
        double maxHealth = 36.0;
        for (Entity entity : Aura.mc.theWorld.playerEntities) {
            if (!(this.players.getValue() != false && entity instanceof EntityPlayer || this.animals.getValue() != false && EntityUtil.isPassive(entity) || this.mobs.getValue() != false && EntityUtil.isMobAggressive(entity) || this.vehicles.getValue() != false && EntityUtil.isVehicle(entity)) && (!this.projectiles.getValue().booleanValue() || !EntityUtil.isProjectile(entity)) || entity instanceof EntityLivingBase && EntityUtil.isntValid(entity, distance) || !Aura.mc.thePlayer.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && Aura.mc.thePlayer.getDistanceSqToEntity(entity) > MathUtil.square(this.wallRange.getValue().floatValue())) continue;
            if (target == null) {
                target = entity;
                distance = Aura.mc.thePlayer.getDistanceSqToEntity(entity);
                maxHealth = EntityUtil.getHealth(entity);
                continue;
            }
            if (entity instanceof EntityPlayer && CombatUtil.isArmorLow((EntityPlayer)entity, 18)) {
                target = entity;
                break;
            }
            if (Aura.mc.thePlayer.getDistanceSqToEntity(entity) < distance) {
                target = entity;
                distance = Aura.mc.thePlayer.getDistanceSqToEntity(entity);
                maxHealth = EntityUtil.getHealth(entity);
            }
            if (!((double)EntityUtil.getHealth(entity) < maxHealth)) continue;
            target = entity;
            distance = Aura.mc.thePlayer.getDistanceSqToEntity(entity);
            maxHealth = EntityUtil.getHealth(entity);
        }
        return target;
    }
}


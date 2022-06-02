/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.entity.EntityOtherPlayerMP
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.play.server.SPacketDestroyEntities
 *  net.minecraft.network.play.server.SPacketEntityStatus
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.CombatRules
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.misc;

import cascade.event.events.PacketEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FakePlayer
extends Module {
    public Setting<Boolean> inv = this.register(new Setting<Boolean>("Inv", false));
    public Setting<Boolean> pop = this.register(new Setting<Boolean>("Pop", false));
    public Setting<String> plrName = this.register(new Setting<String>("Name", "Subhuman"));
    private EntityOtherPlayerMP fake_player;

    public FakePlayer() {
        super("FakePlayer", Module.Category.MISC, "Spawns in a fake player");
    }

    @Override
    public void onEnable() {
        if (FakePlayer.fullNullCheck()) {
            return;
        }
        this.fake_player = new EntityOtherPlayerMP((World)FakePlayer.mc.theWorld, new GameProfile(UUID.fromString("ee11ee92-8148-47e8-b416-72908a6a2275"), this.plrName.getValue()));
        this.fake_player.copyLocationAndAnglesFrom((Entity)FakePlayer.mc.thePlayer);
        this.fake_player.rotationYawHead = FakePlayer.mc.thePlayer.rotationYawHead;
        if (this.inv.getValue().booleanValue()) {
            this.fake_player.inventory = FakePlayer.mc.thePlayer.inventory;
        }
        this.fake_player.setHealth(36.0f);
        FakePlayer.mc.theWorld.addEntityToWorld(-100, (Entity)this.fake_player);
    }

    @Override
    public void onLogout() {
        if (this.isEnabled()) {
            this.disable();
        }
    }

    @Override
    public void onDisable() {
        if (FakePlayer.fullNullCheck()) {
            return;
        }
        try {
            FakePlayer.mc.theWorld.removeEntity((Entity)this.fake_player);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.pop.getValue().booleanValue() && this.isEnabled() && !FakePlayer.fullNullCheck() && event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
            for (int id : packet.func_149098_c()) {
                Entity entity = FakePlayer.mc.theWorld.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSqToEntity((Entity)this.fake_player) < 144.0)) continue;
                float rawDamage = FakePlayer.calculateDamage(entity.posX, entity.posY, entity.posZ, (Entity)this.fake_player);
                float absorption = this.fake_player.getAbsorptionAmount() - rawDamage;
                boolean hasHealthDmg = absorption < 0.0f;
                float health = this.fake_player.getHealth() + absorption;
                if (hasHealthDmg && health > 0.0f) {
                    this.fake_player.setHealth(health);
                    this.fake_player.setAbsorptionAmount(0.0f);
                } else if (health > 0.0f) {
                    this.fake_player.setAbsorptionAmount(absorption);
                } else {
                    this.fake_player.setHealth(2.0f);
                    this.fake_player.setAbsorptionAmount(8.0f);
                    this.fake_player.addPotionEffect(new PotionEffect(MobEffects.absorption, 5));
                    this.fake_player.addPotionEffect(new PotionEffect(MobEffects.regeneration, 1));
                    try {
                        FakePlayer.mc.thePlayer.sendQueue.handleEntityStatus(new SPacketEntityStatus((Entity)this.fake_player, 35));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                this.fake_player.hurtTime = 5;
            }
        }
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = entity.worldObj.getBlockDensity(vec3d, entity.func_174813_aQ());
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            finald = FakePlayer.getBlastReduction((EntityLivingBase)entity, FakePlayer.getDamageMultiplied(damage), new Explosion((World)FakePlayer.mc.theWorld, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finald;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer)entity;
            DamageSource ds = DamageSource.setExplosionSource((Explosion)explosion);
            damage = CombatRules.func_189427_a((float)damage, (float)ep.getTotalArmorValue(), (float)((float)ep.getEntityAttribute(SharedMonsterAttributes.field_189429_h).getAttributeValue()));
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)ep.func_184193_aE(), (DamageSource)ds);
            }
            catch (Exception exception) {
                // empty catch block
            }
            float f = MathHelper.clamp_float((float)k, (float)0.0f, (float)20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.resistance)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.func_189427_a((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.field_189429_h).getAttributeValue()));
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = FakePlayer.mc.theWorld.func_175659_aa().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }
}


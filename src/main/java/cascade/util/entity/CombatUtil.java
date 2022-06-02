/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemShield
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.util.CombatRules
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package cascade.util.entity;

import cascade.util.Util;
import cascade.util.entity.EntityUtil;
import cascade.util.player.InventoryUtil;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class CombatUtil
implements Util {
    private static final DamageSource EXPLOSION_SOURCE = new DamageSource("explosion").setDifficultyScaled().setExplosion();

    public static boolean holdingWeapon() {
        return InventoryUtil.heldItem(Items.diamond_sword, InventoryUtil.Hand.Main) || InventoryUtil.heldItem(Items.diamond_axe, InventoryUtil.Hand.Main);
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketUseEntity(entity));
        } else {
            CombatUtil.mc.playerController.attackEntity((EntityPlayer)CombatUtil.mc.thePlayer, entity);
        }
        if (swingArm) {
            CombatUtil.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
        }
    }

    public static int getCooldownByWeapon(EntityPlayer player) {
        Item item = player.func_184614_ca().getItem();
        if (item instanceof ItemSword) {
            return 600;
        }
        if (item == Items.diamond_axe) {
            return 1000;
        }
        return 250;
    }

    public static int getItemDamage(ItemStack stack) {
        return stack.getMaxDurability() - stack.getCurrentDurability();
    }

    public static boolean isArmorLow(EntityPlayer player, int durability) {
        for (ItemStack piece : player.inventory.armorInventory) {
            if (piece == null) {
                return true;
            }
            if (CombatUtil.getItemDamage(piece) >= durability) continue;
            return true;
        }
        return false;
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        int size = CombatUtil.mc.theWorld.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)CombatUtil.mc.theWorld.playerEntities.get(i);
            if (EntityUtil.isntValid((Entity)player, range)) continue;
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (!(CombatUtil.mc.thePlayer.getDistanceSqToEntity((Entity)player) < CombatUtil.mc.thePlayer.getDistanceSqToEntity((Entity)currentTarget))) continue;
            currentTarget = player;
        }
        return currentTarget;
    }

    public static float calculate(double posX, double posY, double posZ, EntityLivingBase entity) {
        double v = (1.0 - entity.getDistance(posX, posY, posZ) / 12.0) * (double)CombatUtil.getBlockDensity(new Vec3d(posX, posY, posZ), entity.func_174813_aQ());
        return CombatUtil.getBlastReduction(entity, CombatUtil.getDamageMultiplied((float)((v * v + v) / 2.0 * 85.0 + 1.0)));
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        float j2 = 0.0f;
        float k2 = 0.0f;
        for (float f = 0.0f; f <= 1.0f; f += (float)d0) {
            for (float f2 = 0.0f; f2 <= 1.0f; f2 += (float)d2) {
                for (float f3 = 0.0f; f3 <= 1.0f; f3 += (float)d3) {
                    double d6 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                    double d7 = bb.minY + (bb.maxY - bb.minY) * (double)f2;
                    double d8 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f3;
                    if (CombatUtil.rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec, false, false, false) == null) {
                        j2 += 1.0f;
                    }
                    k2 += 1.0f;
                }
            }
        }
        return j2 / k2;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI) {
        float damage = damageI;
        damage = CombatRules.func_189427_a((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.field_189429_h).getAttributeValue()));
        damage *= 1.0f - MathHelper.clamp_float((float)EnchantmentHelper.getEnchantmentModifierDamage((Iterable)entity.func_184193_aE(), (DamageSource)EXPLOSION_SOURCE), (float)0.0f, (float)20.0f) / 25.0f;
        if (entity.isPotionActive(MobEffects.resistance)) {
            return damage - damage / 4.0f;
        }
        return damage;
    }

    public static float getDamageMultiplied(float damage) {
        int diff = CombatUtil.mc.theWorld.func_175659_aa().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        int i = MathHelper.floor_double((double)vec32.xCoord);
        int j = MathHelper.floor_double((double)vec32.yCoord);
        int k = MathHelper.floor_double((double)vec32.zCoord);
        int l = MathHelper.floor_double((double)vec31.xCoord);
        int i2 = MathHelper.floor_double((double)vec31.yCoord);
        int j2 = MathHelper.floor_double((double)vec31.zCoord);
        BlockPos blockpos = new BlockPos(l, i2, j2);
        IBlockState iblockstate = CombatUtil.mc.theWorld.func_180495_p(blockpos);
        Block block = iblockstate.func_177230_c();
        if ((!ignoreBlockWithoutBoundingBox || iblockstate.func_185890_d((IBlockAccess)CombatUtil.mc.theWorld, blockpos) != Block.field_185506_k) && block.func_176209_a(iblockstate, stopOnLiquid)) {
            return iblockstate.func_185910_a((World)CombatUtil.mc.theWorld, blockpos, vec31, vec32);
        }
        RayTraceResult raytraceresult2 = null;
        int k2 = 200;
        while (k2-- >= 0) {
            EnumFacing enumfacing;
            if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                return null;
            }
            if (l == i && i2 == j && j2 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }
            boolean flag2 = true;
            boolean flag3 = true;
            boolean flag4 = true;
            double d0 = 999.0;
            double d2 = 999.0;
            double d3 = 999.0;
            if (i > l) {
                d0 = (double)l + 1.0;
            } else if (i < l) {
                d0 = (double)l + 0.0;
            } else {
                flag2 = false;
            }
            if (j > i2) {
                d2 = (double)i2 + 1.0;
            } else if (j < i2) {
                d2 = (double)i2 + 0.0;
            } else {
                flag3 = false;
            }
            if (k > j2) {
                d3 = (double)j2 + 1.0;
            } else if (k < j2) {
                d3 = (double)j2 + 0.0;
            } else {
                flag4 = false;
            }
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = 999.0;
            double d7 = vec32.xCoord - vec31.xCoord;
            double d8 = vec32.yCoord - vec31.yCoord;
            double d9 = vec32.zCoord - vec31.zCoord;
            if (flag2) {
                d4 = (d0 - vec31.xCoord) / d7;
            }
            if (flag3) {
                d5 = (d2 - vec31.yCoord) / d8;
            }
            if (flag4) {
                d6 = (d3 - vec31.zCoord) / d9;
            }
            if (d4 == -0.0) {
                d4 = -1.0E-4;
            }
            if (d5 == -0.0) {
                d5 = -1.0E-4;
            }
            if (d6 == -0.0) {
                d6 = -1.0E-4;
            }
            if (d4 < d5 && d4 < d6) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.yCoord + d8 * d4, vec31.zCoord + d9 * d4);
            } else if (d5 < d6) {
                enumfacing = j > i2 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.xCoord + d7 * d5, d2, vec31.zCoord + d9 * d5);
            } else {
                enumfacing = k > j2 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.xCoord + d7 * d6, vec31.yCoord + d8 * d6, d3);
            }
            l = MathHelper.floor_double((double)vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i2 = MathHelper.floor_double((double)vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j2 = MathHelper.floor_double((double)vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i2, j2);
            IBlockState iblockstate2 = CombatUtil.mc.theWorld.func_180495_p(blockpos);
            Block block2 = iblockstate2.func_177230_c();
            if (ignoreBlockWithoutBoundingBox && iblockstate2.func_185904_a() != Material.portal && iblockstate2.func_185890_d((IBlockAccess)CombatUtil.mc.theWorld, blockpos) == Block.field_185506_k) continue;
            if (block2.func_176209_a(iblockstate2, stopOnLiquid) && !(block2 instanceof BlockWeb)) {
                return iblockstate2.func_185910_a((World)CombatUtil.mc.theWorld, blockpos, vec31, vec32);
            }
            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
        }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

    public static float getDamageInPercent(ItemStack stack) {
        return (float)CombatUtil.getItemDamage(stack) / (float)stack.getMaxDurability() * 100.0f;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int)CombatUtil.getDamageInPercent(stack);
    }
}


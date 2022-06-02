/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockAir
 *  net.minecraft.block.BlockDeadBush
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAgeable
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.EnumCreatureType
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.item.EntityMinecart
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.monster.EntityIronGolem
 *  net.minecraft.entity.monster.EntityPigZombie
 *  net.minecraft.entity.passive.EntityAmbientCreature
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.passive.EntityVillager
 *  net.minecraft.entity.passive.EntityWolf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityFireball
 *  net.minecraft.entity.projectile.EntityShulkerBullet
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketEntityAction
 *  net.minecraft.network.play.client.CPacketEntityAction$Action
 *  net.minecraft.potion.Potion
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
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.EmptyChunk
 */
package cascade.util.entity;

import cascade.Cascade;
import cascade.util.Util;
import cascade.util.misc.MathUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;

public class EntityUtil
implements Util {
    public static void swingArm(SwingType swingType) {
        switch (swingType) {
            case MainHand: {
                EntityUtil.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                break;
            }
            case OffHand: {
                EntityUtil.mc.thePlayer.func_184609_a(EnumHand.OFF_HAND);
                break;
            }
            case Packet: {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketAnimation(EntityUtil.mc.thePlayer.func_184614_ca().getItem().equals(Items.field_185158_cP) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
            }
        }
    }

    public static float calculateEntityDamage(EntityEnderCrystal crystal, EntityPlayer player) {
        return EntityUtil.calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, (Entity)player);
    }

    public static float calculatePosDamage(BlockPos position, EntityPlayer player) {
        return EntityUtil.calculatePosDamage((double)position.func_177958_n() + 0.5, (double)position.func_177956_o() + 1.0, (double)position.func_177952_p() + 0.5, (Entity)player);
    }

    public static float calculatePosDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleSize = 12.0f;
        double size = entity.getDistance(posX, posY, posZ) / (double)doubleSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.worldObj.getBlockDensity(vec3d, entity.func_174813_aQ());
        double value = (1.0 - size) * blockDensity;
        float damage = (int)((value * value + value) / 2.0 * 7.0 * (double)doubleSize + 1.0);
        double finalDamage = 1.0;
        if (entity instanceof EntityLivingBase) {
            finalDamage = EntityUtil.getBlastReduction((EntityLivingBase)entity, EntityUtil.getMultipliedDamage(damage), new Explosion((World)EntityUtil.mc.theWorld, null, posX, posY, posZ, 6.0f, false, true));
        }
        return (float)finalDamage;
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        DamageSource ds = DamageSource.setExplosionSource((Explosion)explosion);
        damage = CombatRules.func_189427_a((float)damage, (float)entity.getTotalArmorValue(), (float)((float)entity.getEntityAttribute(SharedMonsterAttributes.field_189429_h).getAttributeValue()));
        int k = EnchantmentHelper.getEnchantmentModifierDamage((Iterable)entity.func_184193_aE(), (DamageSource)ds);
        damage *= 1.0f - MathHelper.clamp_float((float)k, (float)0.0f, (float)20.0f) / 25.0f;
        if (entity.isPotionActive(MobEffects.resistance)) {
            damage -= damage / 4.0f;
        }
        return damage;
    }

    public static float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 >= 0.0 && d1 >= 0.0 && d2 >= 0.0) {
            int j2 = 0;
            int k2 = 0;
            float f = 0.0f;
            while (f <= 1.0f) {
                float f1 = 0.0f;
                while (f1 <= 1.0f) {
                    float f2 = 0.0f;
                    while (f2 <= 1.0f) {
                        if (EntityUtil.rayTraceBlocks(new Vec3d(bb.minX + (bb.maxX - bb.minX) * (double)f + d3, bb.minY + (bb.maxY - bb.minY) * (double)f1, bb.minZ + (bb.maxZ - bb.minZ) * (double)f2 + d4), vec, false, false, false) == null) {
                            ++j2;
                        }
                        ++k2;
                        f2 = (float)((double)f2 + d2);
                    }
                    f1 = (float)((double)f1 + d1);
                }
                f = (float)((double)f + d0);
            }
            return (float)j2 / (float)k2;
        }
        return 0.0f;
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        int i = MathHelper.floor_double((double)vec32.xCoord);
        int j = MathHelper.floor_double((double)vec32.yCoord);
        int k = MathHelper.floor_double((double)vec32.zCoord);
        int l = MathHelper.floor_double((double)vec31.xCoord);
        int i1 = MathHelper.floor_double((double)vec31.yCoord);
        int j1 = MathHelper.floor_double((double)vec31.zCoord);
        BlockPos blockpos = new BlockPos(l, i1, j1);
        IBlockState iblockstate = EntityUtil.mc.theWorld.func_180495_p(blockpos);
        Block block = iblockstate.func_177230_c();
        if ((!ignoreBlockWithoutBoundingBox || iblockstate.func_185890_d((IBlockAccess)EntityUtil.mc.theWorld, blockpos) != Block.field_185506_k) && block.func_176209_a(iblockstate, stopOnLiquid)) {
            return iblockstate.func_185910_a((World)EntityUtil.mc.theWorld, blockpos, vec31, vec32);
        }
        RayTraceResult raytraceresult2 = null;
        int k1 = 200;
        while (k1-- >= 0) {
            EnumFacing enumfacing;
            if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                return null;
            }
            if (l == i && i1 == j && j1 == k) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }
            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0;
            double d1 = 999.0;
            double d2 = 999.0;
            if (i > l) {
                d0 = (double)l + 1.0;
            } else if (i < l) {
                d0 = (double)l + 0.0;
            } else {
                flag2 = false;
            }
            if (j > i1) {
                d1 = (double)i1 + 1.0;
            } else if (j < i1) {
                d1 = (double)i1 + 0.0;
            } else {
                flag = false;
            }
            if (k > j1) {
                d2 = (double)j1 + 1.0;
            } else if (k < j1) {
                d2 = (double)j1 + 0.0;
            } else {
                flag1 = false;
            }
            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = vec32.xCoord - vec31.xCoord;
            double d7 = vec32.yCoord - vec31.yCoord;
            double d8 = vec32.zCoord - vec31.zCoord;
            if (flag2) {
                d3 = (d0 - vec31.xCoord) / d6;
            }
            if (flag) {
                d4 = (d1 - vec31.yCoord) / d7;
            }
            if (flag1) {
                d5 = (d2 - vec31.zCoord) / d8;
            }
            if (d3 == -0.0) {
                d3 = -1.0E-4;
            }
            if (d4 == -0.0) {
                d4 = -1.0E-4;
            }
            if (d5 == -0.0) {
                d5 = -1.0E-4;
            }
            if (d3 < d4 && d3 < d5) {
                enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
            } else {
                enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
            }
            l = MathHelper.floor_double((double)vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            i1 = MathHelper.floor_double((double)vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
            j1 = MathHelper.floor_double((double)vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            blockpos = new BlockPos(l, i1, j1);
            IBlockState iblockstate1 = EntityUtil.mc.theWorld.func_180495_p(blockpos);
            Block block1 = iblockstate1.func_177230_c();
            if (ignoreBlockWithoutBoundingBox && iblockstate1.func_185904_a() != Material.portal && iblockstate1.func_185890_d((IBlockAccess)EntityUtil.mc.theWorld, blockpos) == Block.field_185506_k) continue;
            if (block1.func_176209_a(iblockstate1, stopOnLiquid) && block1 != Blocks.web) {
                return iblockstate1.func_185910_a((World)EntityUtil.mc.theWorld, blockpos, vec31, vec32);
            }
            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
        }
        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

    private static float getMultipliedDamage(float damage) {
        return damage * (EntityUtil.mc.theWorld.func_175659_aa().getDifficultyId() == 0 ? 0.0f : (EntityUtil.mc.theWorld.func_175659_aa().getDifficultyId() == 2 ? 1.0f : (EntityUtil.mc.theWorld.func_175659_aa().getDifficultyId() == 1 ? 0.5f : 1.5f)));
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        int size = EntityUtil.mc.theWorld.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)EntityUtil.mc.theWorld.playerEntities.get(i);
            if (EntityUtil.isntValid((Entity)player, range)) continue;
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (!(EntityUtil.mc.thePlayer.getDistanceSqToEntity((Entity)player) < EntityUtil.mc.thePlayer.getDistanceSqToEntity((Entity)currentTarget))) continue;
            currentTarget = player;
        }
        return currentTarget;
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).func_177963_a(vector.xCoord, vector.yCoord, vector.zCoord);
            Block block = EntityUtil.mc.theWorld.func_180495_p(targetPos).func_177230_c();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static boolean isInHole(Entity entity) {
        return EntityUtil.isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }

    public static boolean isBlockValid(BlockPos blockPos) {
        return EntityUtil.isBedrockHole(blockPos) || EntityUtil.isObbyHole(blockPos) || EntityUtil.isBothHole(blockPos);
    }

    public static boolean isObbyHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        for (BlockPos pos : touchingBlocks) {
            IBlockState touchingState = EntityUtil.mc.theWorld.func_180495_p(pos);
            if (touchingState.func_177230_c() != Blocks.air && touchingState.func_177230_c() == Blocks.obsidian) continue;
            return false;
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        for (BlockPos pos : touchingBlocks) {
            IBlockState touchingState = EntityUtil.mc.theWorld.func_180495_p(pos);
            if (touchingState.func_177230_c() != Blocks.air && touchingState.func_177230_c() == Blocks.bedrock) continue;
            return false;
        }
        return true;
    }

    public static boolean isBothHole(BlockPos blockPos) {
        BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
        for (BlockPos pos : touchingBlocks) {
            IBlockState touchingState = EntityUtil.mc.theWorld.func_180495_p(pos);
            if (touchingState.func_177230_c() != Blocks.air && (touchingState.func_177230_c() == Blocks.bedrock || touchingState.func_177230_c() == Blocks.obsidian)) continue;
            return false;
        }
        return true;
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, (double)(y - 1), 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static Vec3d[] getHeightOffsets(int min, int max) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        for (int i = min; i <= max; ++i) {
            offsets.add(new Vec3d(0.0, (double)i, 0.0));
        }
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return EntityUtil.isLiving(entity) && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !EntityUtil.isAlive(entity);
    }

    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static boolean isMoving() {
        return EntityUtil.mc.thePlayer.field_191988_bg != 0.0f || EntityUtil.mc.thePlayer.moveStrafing != 0.0f;
    }

    public static Map<String, Integer> getTextRadarPlayers() {
        Map<String, Integer> output = new HashMap<String, Integer>();
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder healthSB = new StringBuilder();
        StringBuilder distanceSB = new StringBuilder();
        for (EntityPlayer player : EntityUtil.mc.theWorld.playerEntities) {
            if (player.isInvisible() || player.getCommandSenderName().equals(EntityUtil.mc.thePlayer.getCommandSenderName())) continue;
            int hpRaw = (int)EntityUtil.getHealth((Entity)player);
            String hp = dfHealth.format(hpRaw);
            healthSB.append("\u00c2\u00a7");
            if (hpRaw >= 20) {
                healthSB.append("a");
            } else if (hpRaw >= 10) {
                healthSB.append("e");
            } else if (hpRaw >= 5) {
                healthSB.append("6");
            } else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            int distanceInt = (int)EntityUtil.mc.thePlayer.getDistanceToEntity((Entity)player);
            String distance = dfDistance.format(distanceInt);
            distanceSB.append("\u00c2\u00a7");
            if (distanceInt >= 25) {
                distanceSB.append("a");
            } else if (distanceInt > 10) {
                distanceSB.append("6");
            } else {
                distanceSB.append("c");
            }
            distanceSB.append(distance);
            output.put(healthSB.toString() + " " + (Cascade.friendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RED) + player.getCommandSenderName() + " " + distanceSB.toString() + " \u00c2\u00a7f0", (int)EntityUtil.mc.thePlayer.getDistanceToEntity((Entity)player));
            healthSB.setLength(0);
            distanceSB.setLength(0);
        }
        if (!output.isEmpty()) {
            output = MathUtil.sortByValue(output, false);
        }
        return output;
    }

    public static boolean isPlayerSafe(EntityPlayer target) {
        BlockPos playerPos = EntityUtil.getPlayerPos(target);
        return !(EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177977_b()).func_177230_c() != Blocks.obsidian && EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177977_b()).func_177230_c() != Blocks.bedrock || EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177978_c()).func_177230_c() != Blocks.obsidian && EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177978_c()).func_177230_c() != Blocks.bedrock || EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177974_f()).func_177230_c() != Blocks.obsidian && EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177974_f()).func_177230_c() != Blocks.bedrock || EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177968_d()).func_177230_c() != Blocks.obsidian && EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177968_d()).func_177230_c() != Blocks.bedrock || EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177976_e()).func_177230_c() != Blocks.obsidian && EntityUtil.mc.theWorld.func_180495_p(playerPos.func_177976_e()).func_177230_c() != Blocks.bedrock);
    }

    public static boolean isBorderingChunk(Entity boat, Double x, Double z) {
        return EntityUtil.mc.theWorld.getChunkFromChunkCoords((int)(boat.posX + x) / 16, (int)(boat.posZ + z) / 16) instanceof EmptyChunk;
    }

    public static Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5;
        return new Vec3d(x, y, z);
    }

    public static BlockPos getCenterBP(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5;
        return new BlockPos(x, y, z);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (EntityUtil.mc.thePlayer != null && EntityUtil.mc.thePlayer.isPotionActive(Objects.requireNonNull(Potion.func_188412_a((int)1)))) {
            maxModifier *= 1.0 + 0.2 * (double)(Objects.requireNonNull(EntityUtil.mc.thePlayer.getActivePotionEffect(Objects.requireNonNull(Potion.func_188412_a((int)1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }

    public static boolean isEntityMoving(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return EntityUtil.mc.gameSettings.keyBindForward.getIsKeyPressed() || EntityUtil.mc.gameSettings.keyBindBack.getIsKeyPressed() || EntityUtil.mc.gameSettings.keyBindLeft.getIsKeyPressed() || EntityUtil.mc.gameSettings.keyBindRight.getIsKeyPressed();
        }
        return entity.motionX != 0.0 || entity.motionY != 0.0 || entity.motionZ != 0.0;
    }

    public static boolean isInLiquid() {
        return EntityUtil.mc.thePlayer.isInWater() || EntityUtil.mc.thePlayer.func_180799_ab();
    }

    public static double[] forward(double speed) {
        float forward = EntityUtil.mc.thePlayer.movementInput.field_192832_b;
        float side = EntityUtil.mc.thePlayer.movementInput.moveStrafe;
        float yaw = EntityUtil.mc.thePlayer.prevRotationYaw + (EntityUtil.mc.thePlayer.rotationYaw - EntityUtil.mc.thePlayer.prevRotationYaw) * mc.func_184121_ak();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float)(forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float)(forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double)forward * speed * cos + (double)side * speed * sin;
        double posZ = (double)forward * speed * sin - (double)side * speed * cos;
        return new double[]{posX, posZ};
    }

    public static void setSpeed(EntityLivingBase entity, double speed) {
        double[] dir = EntityUtil.forward(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static boolean canEntityFeetBeSeen(Entity entityIn) {
        return EntityUtil.mc.theWorld.rayTraceBlocks(new Vec3d(EntityUtil.mc.thePlayer.posX, EntityUtil.mc.thePlayer.posX + (double)EntityUtil.mc.thePlayer.getEyeHeight(), EntityUtil.mc.thePlayer.posZ), new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ), false, true, false) == null;
    }

    public static boolean isMobAggressive(Entity entity) {
        if (entity instanceof EntityPigZombie) {
            if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
                return true;
            }
        } else {
            if (entity instanceof EntityWolf) {
                return ((EntityWolf)entity).isAngry() && !EntityUtil.mc.thePlayer.equals((Object)((EntityWolf)entity).getOwner());
            }
            if (entity instanceof EntityEnderman) {
                return ((EntityEnderman)entity).isScreaming();
            }
        }
        return EntityUtil.isHostileMob(entity);
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
    }

    public static boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

    public static boolean isFriendlyMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !EntityUtil.isNeutralMob(entity) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || EntityUtil.isNeutralMob(entity) && !EntityUtil.isMobAggressive(entity);
    }

    public static boolean isHostileMob(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !EntityUtil.isNeutralMob(entity);
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof EntityWolf && ((EntityWolf)entity).isAngry()) {
            return false;
        }
        if (entity instanceof EntityAgeable || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid) {
            return true;
        }
        return entity instanceof EntityIronGolem && ((EntityIronGolem)entity).getAITarget() == null;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || EntityUtil.isDead(entity) || entity.equals((Object)EntityUtil.mc.thePlayer) || entity instanceof EntityPlayer && Cascade.friendManager.isFriend(entity.getCommandSenderName()) || EntityUtil.mc.thePlayer.getDistanceSqToEntity(entity) > MathUtil.square(range);
    }

    public static boolean isBurrow(Entity entity) {
        BlockPos blockPos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        return EntityUtil.mc.theWorld.func_180495_p(blockPos).func_177230_c() == Blocks.obsidian || EntityUtil.mc.theWorld.func_180495_p(blockPos).func_177230_c() == Blocks.ender_chest;
    }

    public static boolean isSafe(Entity entity, double height, boolean floor) {
        return EntityUtil.getUnsafeBlocks(entity, height, floor).size() == 0;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, double height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, double height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).func_177963_a(vector.xCoord, vector.yCoord, vector.zCoord);
            Block block = EntityUtil.mc.theWorld.func_180495_p(targetPos).func_177230_c();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static Vec3d[] getOffsets(double y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }

    public static List<Vec3d> getOffsetList(double y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1.0, 0.0));
        }
        return offsets;
    }

    public static boolean isSafe(Entity entity) {
        return EntityUtil.isSafe(entity, 0.0, false);
    }

    public static BlockPos getPlayerPosWithEntity() {
        return new BlockPos(EntityUtil.mc.thePlayer.func_184187_bx() != null ? EntityUtil.mc.thePlayer.func_184187_bx().posX : EntityUtil.mc.thePlayer.posX, EntityUtil.mc.thePlayer.func_184187_bx() != null ? EntityUtil.mc.thePlayer.func_184187_bx().posY : EntityUtil.mc.thePlayer.posY, EntityUtil.mc.thePlayer.func_184187_bx() != null ? EntityUtil.mc.thePlayer.func_184187_bx().posZ : EntityUtil.mc.thePlayer.posZ);
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null) {
            return false;
        }
        double y = entity.posY + 0.01;
        for (int x = MathHelper.floor_double((double)entity.posX); x < MathHelper.ceiling_double_int((double)entity.posX); ++x) {
            for (int z = MathHelper.floor_double((double)entity.posZ); z < MathHelper.ceiling_double_int((double)entity.posZ); ++z) {
                BlockPos pos = new BlockPos(x, (int)y, z);
                if (!(EntityUtil.mc.theWorld.func_180495_p(pos).func_177230_c() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean startSneaking() {
        if (EntityUtil.mc.thePlayer != null) {
            if (EntityUtil.mc.thePlayer.isSneaking()) {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
            } else {
                mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.thePlayer, CPacketEntityAction.Action.START_SNEAKING));
            }
        }
        return false;
    }

    public static boolean stopSneaking(boolean force) {
        if (EntityUtil.mc.thePlayer != null && (EntityUtil.mc.thePlayer.isSneaking() || force)) {
            mc.getNetHandler().addToSendQueue((Packet)new CPacketEntityAction((Entity)EntityUtil.mc.thePlayer, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return false;
    }

    public static boolean isOnLiquid() {
        double y = EntityUtil.mc.thePlayer.posY - 0.03;
        for (int x = MathHelper.floor_double((double)EntityUtil.mc.thePlayer.posX); x < MathHelper.ceiling_double_int((double)EntityUtil.mc.thePlayer.posX); ++x) {
            for (int z = MathHelper.floor_double((double)EntityUtil.mc.thePlayer.posZ); z < MathHelper.ceiling_double_int((double)EntityUtil.mc.thePlayer.posZ); ++z) {
                BlockPos pos = new BlockPos(x, MathHelper.floor_double((double)y), z);
                if (!(EntityUtil.mc.theWorld.func_180495_p(pos).func_177230_c() instanceof BlockLiquid)) continue;
                return true;
            }
        }
        return false;
    }

    public static EntityPlayer getClosestEnemy() {
        return EntityUtil.getClosestEnemy(EntityUtil.mc.theWorld.playerEntities);
    }

    public static EntityPlayer getClosestEnemy(List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(EntityUtil.mc.thePlayer.func_174791_d(), list);
    }

    public static EntityPlayer getClosestEnemy(BlockPos pos, List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p(), list);
    }

    public static EntityPlayer getClosestEnemy(Vec3d vec3d, List<EntityPlayer> list) {
        return EntityUtil.getClosestEnemy(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, list);
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, List<EntityPlayer> players) {
        EntityPlayer closest = null;
        double distance = 3.4028234663852886E38;
        for (EntityPlayer player : players) {
            double dist;
            if (player == null || EntityUtil.isDead((Entity)player) || player.equals((Object)EntityUtil.mc.thePlayer) || Cascade.friendManager.isFriend(player) || !((dist = player.getDistanceSq(x, y, z)) < distance)) continue;
            closest = player;
            distance = dist;
        }
        return closest;
    }

    public static EntityPlayer getClosestEnemy(double x, double y, double z, double maxRange, List<EntityPlayer> enemies, List<EntityPlayer> players) {
        EntityPlayer closestEnemied = EntityUtil.getClosestEnemy(x, y, z, enemies);
        if (closestEnemied != null && closestEnemied.getDistanceSq(x, y, z) < MathUtil.square(maxRange)) {
            return closestEnemied;
        }
        return EntityUtil.getClosestEnemy(x, y, z, players);
    }

    public static boolean isOnChest(Entity entity) {
        BlockPos blockPos = new BlockPos(entity.posX, entity.posY, entity.posZ);
        return EntityUtil.mc.theWorld.func_180495_p(blockPos).func_177230_c().equals(Blocks.obsidian) || EntityUtil.mc.theWorld.func_180495_p(blockPos).func_177230_c().equals(Blocks.ender_chest);
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        try {
            double d3 = y;
            List list1 = EntityUtil.mc.theWorld.func_184144_a(entity, entity.func_174813_aQ().addCoord(x, y, z));
            if (y != 0.0) {
                int l = list1.size();
                for (int k = 0; k < l; ++k) {
                    y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.func_174813_aQ(), y);
                }
            }
            return d3 != y && d3 < 0.0;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static enum SwingType {
        MainHand,
        OffHand,
        Packet;

    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketAnimation
 *  net.minecraft.network.play.client.CPacketHeldItemChange
 *  net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
 *  net.minecraft.network.play.client.CPacketUseEntity
 *  net.minecraft.network.play.client.CPacketUseEntity$Action
 *  net.minecraft.network.play.server.SPacketExplosion
 *  net.minecraft.network.play.server.SPacketSoundEffect
 *  net.minecraft.network.play.server.SPacketSpawnObject
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import cascade.util.render.ColorUtil;
import cascade.util.render.RenderUtil;
import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalAura
extends Module {
    Timer placeTimer = new Timer();
    Timer breakTimer = new Timer();
    Timer predictTimer = new Timer();
    Timer swapTimer = new Timer();
    Setting<Page> page = this.register(new Setting<Page>("Page", Page.Place));
    Setting<Boolean> place = this.register(new Setting<Object>("Place", Boolean.valueOf(true), v -> this.page.getValue() == Page.Place));
    Setting<Float> placeRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    Setting<Float> placeWallRange = this.register(new Setting<Object>("PlaceWallRange", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    Setting<Float> placeDelay = this.register(new Setting<Object>("PlaceDelay", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(250.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    Setting<Float> maxSelfDamage = this.register(new Setting<Object>("MaxSelfDamage", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    Setting<Float> facePlace = this.register(new Setting<Object>("FacePlaceHP", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    Setting<Float> minArmor = this.register(new Setting<Object>("ArmorDamage", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(100.0f), v -> this.page.getValue() == Page.Place));
    Setting<Float> targetRange = this.register(new Setting<Object>("TargetRange", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(16.0f), v -> this.page.getValue() == Page.Place));
    Setting<Boolean> predictMotion = this.register(new Setting<Object>("PredictMotion", Boolean.valueOf(true), v -> this.page.getValue() == Page.Place));
    Setting<Integer> motionTicks = this.register(new Setting<Object>("MotionTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(15), v -> this.predictMotion.getValue() != false && this.page.getValue() == Page.Place));
    Setting<Boolean> explode = this.register(new Setting<Object>("Break", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    Setting<Float> breakDelay = this.register(new Setting<Object>("BreakDelay", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(300.0f), v -> this.page.getValue() == Page.Break));
    Setting<Float> breakRange = this.register(new Setting<Object>("BreakRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Break));
    Setting<Float> breakWallRange = this.register(new Setting<Object>("BreakWallRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Break));
    Setting<Boolean> packetBreak = this.register(new Setting<Object>("PacketBreak", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    Setting<Boolean> predicts = this.register(new Setting<Object>("Predict", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    Setting<Integer> attackFactor = this.register(new Setting<Object>("PredictDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), p -> this.predicts.getValue() != false && this.page.getValue() == Page.Break));
    Setting<Boolean> remove = this.register(new Setting<Object>("Remove", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    Setting<Boolean> confirmation = this.register(new Setting<Object>("Confirm", Boolean.valueOf(false), v -> this.page.getValue() == Page.Break && this.remove.getValue() != false));
    Setting<Integer> ticksExisted = this.register(new Setting<Object>("TicksExisted", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(5), p -> this.predicts.getValue() != false && this.page.getValue() == Page.Break));
    Setting<NoSuicide> noSuicide = this.register(new Setting<Object>("NoSuicide", (Object)NoSuicide.Fast, v -> this.page.getValue() == Page.Misc));
    Setting<Swap> swap = this.register(new Setting<Object>("Swap", (Object)Swap.Off, v -> this.page.getValue() == Page.Misc));
    Setting<Boolean> holePlacement = this.register(new Setting<Object>("HolePlacement", Boolean.valueOf(true), v -> this.page.getValue() == Page.Misc));
    Setting<Boolean> chorusPredict = this.register(new Setting<Object>("ChorusPredict", Boolean.valueOf(true), v -> this.page.getValue() == Page.Misc));
    Setting<Boolean> ignoreUseAmount = this.register(new Setting<Object>("IgnoreUseAmount", Boolean.valueOf(true), v -> this.page.getValue() == Page.Misc));
    Setting<Integer> wasteAmount = this.register(new Setting<Object>("UseAmount", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(5), v -> this.page.getValue() == Page.Misc));
    Setting<SwingMode> swingMode = this.register(new Setting<Object>("Swing", (Object)SwingMode.MainHand, v -> this.page.getValue() == Page.Misc));
    Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    Setting<Boolean> renderDmg = this.register(new Setting<Object>("RenderDmg", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    Setting<HUD> hud = this.register(new Setting<Object>("HUD", (Object)HUD.Target, v -> this.page.getValue() == Page.Render));
    Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.page.getValue() == Page.Render));
    Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    Setting<Boolean> fadeSlower = this.register(new Setting<Object>("FadeSlower", Boolean.valueOf(false), v -> this.page.getValue() == Page.Render));
    ConcurrentHashMap<BlockPos, Integer> renderSpots;
    CalculationThread calculationThread;
    public static CrystalAura INSTANCE;
    EntityLivingBase realTarget;
    EntityEnderCrystal crystal;
    boolean confirmed = true;
    boolean exploded = false;
    EntityLivingBase target;
    double damage = 0.5;
    boolean armorTarget;
    BlockPos calcPos;
    int crystalCount;
    int hotBarSlot;
    boolean armor;
    BlockPos pos;

    public CrystalAura() {
        super("CrystalAura", Module.Category.COMBAT, "close fight");
        INSTANCE = this;
    }

    public static CrystalAura getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrystalAura();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (!CrystalAura.fullNullCheck()) {
            mc.addScheduledTask(() -> this.onCrystal());
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity cPacketUseEntity;
        if (CrystalAura.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (cPacketUseEntity = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && cPacketUseEntity.getEntityFromWorld((World)CrystalAura.mc.theWorld) instanceof EntityEnderCrystal) {
            if (this.remove.getValue().booleanValue()) {
                cPacketUseEntity.getEntityFromWorld((World)CrystalAura.mc.theWorld).setDead();
                CrystalAura.mc.theWorld.removeEntityFromWorld(cPacketUseEntity.entityId);
                if (this.confirmation.getValue().booleanValue()) {
                    this.confirmed = true;
                }
            }
            if (CrystalAura.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
                int crystalSlot;
                int n = crystalSlot = CrystalAura.mc.thePlayer.func_184614_ca().getItem() == Items.field_185158_cP ? CrystalAura.mc.thePlayer.inventory.currentItem : -1;
                if (crystalSlot == -1) {
                    for (int l = 0; l < 9; ++l) {
                        if (CrystalAura.mc.thePlayer.inventory.getStackInSlot(l).getItem() != Items.field_185158_cP) continue;
                        crystalSlot = l;
                        this.hotBarSlot = l;
                        break;
                    }
                }
                if (crystalSlot == -1) {
                    this.pos = null;
                    this.calcPos = null;
                    this.target = null;
                    this.realTarget = null;
                    return;
                }
            }
            if (this.swap.getValue() == Swap.Silent) {
                return;
            }
            if (this.pos != null && CrystalAura.mc.thePlayer.onGround) {
                RayTraceResult result = CrystalAura.mc.theWorld.rayTraceBlocks(new Vec3d(CrystalAura.mc.thePlayer.posX, CrystalAura.mc.thePlayer.posY + (double)CrystalAura.mc.thePlayer.getEyeHeight(), CrystalAura.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                EnumFacing f = result == null || result.field_178784_b == null ? EnumFacing.UP : result.field_178784_b;
                mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f, CrystalAura.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                CrystalAura.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
            }
        }
    }

    @Override
    public void onEnable() {
        this.renderSpots = new ConcurrentHashMap();
        this.placeTimer.reset();
        this.breakTimer.reset();
        this.swapTimer.reset();
        this.hotBarSlot = -1;
        this.damage = 0.5;
        this.pos = null;
        this.calcPos = null;
        this.crystal = null;
        this.target = null;
        this.realTarget = null;
        this.armor = false;
        this.armorTarget = false;
        this.exploded = false;
        this.confirmed = true;
        this.calculationThread = new CalculationThread();
        this.calculationThread.start();
    }

    @Override
    public String getDisplayInfo() {
        if (this.realTarget != null) {
            if (this.hud.getValue() == HUD.Target) {
                return this.realTarget.getCommandSenderName();
            }
            if (this.hud.getValue() == HUD.Damage) {
                return String.format("%.1f", this.damage);
            }
        }
        return null;
    }

    public void onCrystal() {
        if (CrystalAura.fullNullCheck()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("SelfFill")) {
            return;
        }
        this.crystalCount = 0;
        if (!this.ignoreUseAmount.getValue().booleanValue()) {
            for (Entity crystal : CrystalAura.mc.theWorld.loadedEntityList) {
                if (!(crystal instanceof EntityEnderCrystal) || !this.IsValidCrystal(crystal)) continue;
                boolean count = false;
                double damage = this.calculateDamage((double)this.target.func_180425_c().func_177958_n() + 0.5, (double)this.target.func_180425_c().func_177956_o() + 1.0, (double)this.target.func_180425_c().func_177952_p() + 0.5, (Entity)this.target);
                if (damage >= (double)this.minDamage.getValue().floatValue()) {
                    count = true;
                }
                if (!count) continue;
                ++this.crystalCount;
            }
        }
        this.hotBarSlot = -1;
        if (CrystalAura.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
            int crystalSlot;
            int n = crystalSlot = CrystalAura.mc.thePlayer.func_184614_ca().getItem() == Items.field_185158_cP ? CrystalAura.mc.thePlayer.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                for (int l = 0; l < 9; ++l) {
                    if (CrystalAura.mc.thePlayer.inventory.getStackInSlot(l).getItem() != Items.field_185158_cP) continue;
                    crystalSlot = l;
                    this.hotBarSlot = l;
                    break;
                }
            }
            if (crystalSlot == -1) {
                this.pos = null;
                this.calcPos = null;
                this.target = null;
                this.realTarget = null;
                return;
            }
        }
        if (this.target == null) {
            this.target = this.getTarget();
        }
        if (this.target == null) {
            this.crystal = null;
            return;
        }
        if (this.target.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer) > 12.0f) {
            this.crystal = null;
            this.target = null;
            this.realTarget = null;
        }
        this.crystal = CrystalAura.mc.theWorld.loadedEntityList.stream().filter(this::IsValidCrystal).map(p_Entity -> p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(this.target.getDistanceToEntity(p_Entity)))).orElse(null);
        if (this.crystal != null && this.explode.getValue().booleanValue()) {
            if (this.crystal.ticksExisted < this.ticksExisted.getValue()) {
                return;
            }
            if (this.breakTimer.passedMs(this.breakDelay.getValue().longValue())) {
                int swordSlot;
                this.breakTimer.reset();
                int oldSlot = CrystalAura.mc.thePlayer.inventory.currentItem;
                int n = swordSlot = CrystalAura.mc.thePlayer.func_184614_ca().getItem() == Items.diamond_sword ? CrystalAura.mc.thePlayer.inventory.currentItem : -1;
                if (swordSlot == -1) {
                    for (int i = 0; i < 9; ++i) {
                        if (CrystalAura.mc.thePlayer.inventory.getStackInSlot(i).getItem() != Items.diamond_sword) continue;
                        swordSlot = i;
                        break;
                    }
                }
                if (this.swingMode.getValue() == SwingMode.MainHand) {
                    CrystalAura.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                }
                if (this.swingMode.getValue() == SwingMode.OffHand) {
                    CrystalAura.mc.thePlayer.func_184609_a(EnumHand.OFF_HAND);
                }
                if (this.packetBreak.getValue().booleanValue()) {
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketUseEntity((Entity)this.crystal));
                } else {
                    CrystalAura.mc.playerController.attackEntity((EntityPlayer)CrystalAura.mc.thePlayer, (Entity)this.crystal);
                }
                this.exploded = true;
            }
        }
        if (this.placeTimer.passedMs(this.placeDelay.getValue().longValue()) && this.place.getValue().booleanValue()) {
            this.placeTimer.reset();
            if (this.confirmation.getValue().booleanValue() && !this.confirmed) {
                return;
            }
            this.pos = this.calcPos;
            if (this.pos == null) {
                return;
            }
            int crystalSlot = InventoryUtil.getItemSlot(Items.field_185158_cP);
            int oldSlot = CrystalAura.mc.thePlayer.inventory.currentItem;
            if (this.swap.getValue() == Swap.Off && !InventoryUtil.heldItem(Items.field_185158_cP, InventoryUtil.Hand.Both)) {
                this.pos = null;
                this.calcPos = null;
                this.target = null;
                this.realTarget = null;
                return;
            }
            if (!this.ignoreUseAmount.getValue().booleanValue()) {
                int crystalLimit = this.wasteAmount.getValue();
                if (this.crystalCount >= crystalLimit) {
                    return;
                }
                if (this.damage < (double)this.minDamage.getValue().floatValue()) {
                    crystalLimit = 1;
                }
                if (this.crystalCount < crystalLimit && this.pos != null) {
                    if (!this.exploded) {
                        EnumFacing f;
                        RayTraceResult result = CrystalAura.mc.theWorld.rayTraceBlocks(new Vec3d(CrystalAura.mc.thePlayer.posX, CrystalAura.mc.thePlayer.posY + (double)CrystalAura.mc.thePlayer.getEyeHeight(), CrystalAura.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                        EnumFacing enumFacing = f = result == null || result.field_178784_b == null ? EnumFacing.UP : result.field_178784_b;
                        if (this.swap.getValue() == Swap.Silent && !InventoryUtil.heldItem(Items.field_185158_cP, InventoryUtil.Hand.Both) && crystalSlot != -1) {
                            mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(crystalSlot));
                        }
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f, CrystalAura.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                        if (this.swap.getValue() == Swap.Silent && !InventoryUtil.heldItem(Items.field_185158_cP, InventoryUtil.Hand.Both)) {
                            mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(oldSlot));
                        }
                        CrystalAura.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                        this.renderSpots.put(this.pos, this.c.getValue().getAlpha());
                    } else {
                        this.exploded = false;
                    }
                }
            } else if (this.pos != null) {
                if (!this.exploded) {
                    EnumFacing f2;
                    RayTraceResult result2 = CrystalAura.mc.theWorld.rayTraceBlocks(new Vec3d(CrystalAura.mc.thePlayer.posX, CrystalAura.mc.thePlayer.posY + (double)CrystalAura.mc.thePlayer.getEyeHeight(), CrystalAura.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                    EnumFacing enumFacing = f2 = result2 == null || result2.field_178784_b == null ? EnumFacing.UP : result2.field_178784_b;
                    if (this.swap.getValue() == Swap.Silent && !InventoryUtil.heldItem(Items.field_185158_cP, InventoryUtil.Hand.Both) && crystalSlot != -1) {
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(crystalSlot));
                    }
                    mc.getNetHandler().addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f2, CrystalAura.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                    CrystalAura.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                    if (this.swap.getValue() == Swap.Silent && !InventoryUtil.heldItem(Items.field_185158_cP, InventoryUtil.Hand.Both)) {
                        mc.getNetHandler().addToSendQueue((Packet)new CPacketHeldItemChange(oldSlot));
                    }
                    this.renderSpots.put(this.pos, this.c.getValue().getAlpha());
                } else {
                    this.exploded = false;
                }
            }
            this.confirmed = false;
        }
    }

    void doCalculations() {
        if (this.target == null) {
            this.target = this.getTarget();
        }
        if (this.target == null) {
            return;
        }
        this.damage = 0.5;
        for (BlockPos blockPos : this.placePostions(this.placeRange.getValue().floatValue())) {
            double d;
            if (blockPos == null || this.target == null || !CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty() || !(this.target.getDistance((double)blockPos.func_177958_n(), (double)blockPos.func_177956_o(), (double)blockPos.func_177952_p()) <= (double)this.targetRange.getValue().floatValue()) || this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) continue;
            double targetDmg = this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)this.target);
            double localDmg = this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)CrystalAura.mc.thePlayer);
            this.armor = false;
            if (localDmg > (double)this.maxSelfDamage.getValue().floatValue()) continue;
            if ((double)EntityUtil.getHealth((Entity)CrystalAura.mc.thePlayer) - localDmg <= (double)(this.noSuicide.getValue() == NoSuicide.Fast ? 4 : 8)) continue;
            try {
                for (ItemStack is : this.target.func_184193_aE()) {
                    float green = ((float)is.getMaxDurability() - (float)is.getCurrentDurability()) / (float)is.getMaxDurability();
                    float red = 1.0f - green;
                    int dmg = 100 - (int)(red * 100.0f);
                    if ((float)dmg > this.minArmor.getValue().floatValue()) continue;
                    this.armor = true;
                }
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from CrystalAura");
                ex.printStackTrace();
            }
            if (targetDmg < (double)this.minDamage.getValue().floatValue() && this.target.getHealth() + this.target.getAbsorptionAmount() > this.facePlace.getValue().floatValue() && !this.armor) continue;
            double selfDmg = (double)this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)CrystalAura.mc.thePlayer) + 4.0;
            if (d >= (double)(CrystalAura.mc.thePlayer.getHealth() + CrystalAura.mc.thePlayer.getAbsorptionAmount()) && selfDmg >= targetDmg || this.damage > targetDmg) continue;
            this.calcPos = blockPos;
            this.damage = targetDmg;
        }
        if (this.damage == 0.5) {
            this.pos = null;
            this.calcPos = null;
            this.target = null;
            this.realTarget = null;
            return;
        }
        this.realTarget = this.target;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive e) {
        SPacketSoundEffect sPacketSoundEffect;
        SPacketSpawnObject p;
        if (CrystalAura.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketSpawnObject && this.predicts.getValue().booleanValue() && this.predictTimer.passedMs(this.attackFactor.getValue().intValue()) && this.predicts.getValue().booleanValue() && this.explode.getValue().booleanValue() && this.packetBreak.getValue().booleanValue() && this.target != null && (p = (SPacketSpawnObject)e.getPacket()).func_148993_l() == 51) {
            if (!this.isPredicting(p)) {
                return;
            }
            CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = p.func_149001_c();
            predict.action = CPacketUseEntity.Action.ATTACK;
            mc.getNetHandler().addToSendQueue((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.getNetHandler().addToSendQueue((Packet)predict);
        }
        if (e.getPacket() instanceof SPacketSoundEffect) {
            sPacketSoundEffect = (SPacketSoundEffect)e.getPacket();
            try {
                if (sPacketSoundEffect.func_186977_b() == SoundCategory.BLOCKS && sPacketSoundEffect.func_186978_a() == SoundEvents.field_187539_bB) {
                    for (Entity en : CrystalAura.mc.theWorld.loadedEntityList) {
                        if (!(en instanceof EntityEnderCrystal) || !(en.getDistance(sPacketSoundEffect.func_149207_d(), sPacketSoundEffect.func_149211_e(), sPacketSoundEffect.func_149210_f()) <= (double)this.breakRange.getValue().floatValue())) continue;
                        Objects.requireNonNull(CrystalAura.mc.theWorld.getEntityByID(en.getEntityId())).setDead();
                        CrystalAura.mc.theWorld.removeEntityFromWorld(en.entityId);
                        if (!this.confirmation.getValue().booleanValue()) continue;
                        this.confirmed = true;
                    }
                }
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from CrystalAura");
                ex.printStackTrace();
            }
        }
        if (e.getPacket() instanceof SPacketExplosion) {
            try {
                SPacketExplosion sPacketExplosion = (SPacketExplosion)e.getPacket();
                for (Entity en : CrystalAura.mc.theWorld.loadedEntityList) {
                    if (!(en instanceof EntityEnderCrystal) || !(en.getDistance(sPacketExplosion.func_149148_f(), sPacketExplosion.func_149143_g(), sPacketExplosion.func_149145_h()) <= (double)this.breakRange.getValue().floatValue())) continue;
                    CrystalAura.mc.theWorld.getEntityByID(en.getEntityId()).setDead();
                    CrystalAura.mc.theWorld.removeEntityFromWorld(en.entityId);
                    if (!this.confirmation.getValue().booleanValue()) continue;
                    this.confirmed = true;
                }
            }
            catch (Exception ex) {
                Cascade.LOGGER.info("Caught an exception from CrystalAura");
                ex.printStackTrace();
            }
        }
        if (e.getPacket() instanceof SPacketSoundEffect && this.chorusPredict.getValue().booleanValue() && ((sPacketSoundEffect = (SPacketSoundEffect)e.getPacket()).func_186978_a() == SoundEvents.field_187544_ad || sPacketSoundEffect.func_186978_a() == SoundEvents.field_187534_aX) && CrystalAura.mc.thePlayer.getDistance(sPacketSoundEffect.func_149207_d(), sPacketSoundEffect.func_149211_e(), sPacketSoundEffect.func_149210_f()) <= (double)this.targetRange.getValue().floatValue()) {
            this.pos = new BlockPos(sPacketSoundEffect.func_149207_d(), sPacketSoundEffect.func_149211_e(), sPacketSoundEffect.func_149210_f());
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderSpots != null && !this.renderSpots.isEmpty()) {
            for (Map.Entry<BlockPos, Integer> entry : this.renderSpots.entrySet()) {
                BlockPos blockPos = entry.getKey();
                Integer alpha = entry.getValue();
                alpha = this.fadeSlower.getValue() != false ? Integer.valueOf(alpha - 1) : Integer.valueOf(alpha - 2);
                if (alpha <= 0) {
                    this.renderSpots.remove(blockPos);
                    continue;
                }
                this.renderSpots.replace(blockPos, alpha);
                RenderUtil.drawBoxESP(blockPos, ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), alpha), this.outline.getValue(), ClickGui.getInstance().rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.c.getValue().getRed(), this.c.getValue().getGreen(), this.c.getValue().getBlue(), alpha), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), alpha, true);
            }
        }
        if (this.pos != null && this.render.getValue().booleanValue() && this.target != null && this.renderDmg.getValue().booleanValue()) {
            try {
                double renderDamage = this.calculateDamage((double)this.pos.func_177958_n() + 0.5, (double)this.pos.func_177956_o() + 1.0, (double)this.pos.func_177952_p() + 0.5, (Entity)this.target);
                RenderUtil.drawText(this.pos, (Math.floor(renderDamage) == renderDamage ? Integer.valueOf((int)renderDamage) : String.format("%.1f", renderDamage)) + "");
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    boolean isPredicting(SPacketSpawnObject packet) {
        try {
            BlockPos packPos = new BlockPos(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e());
            return CrystalAura.mc.thePlayer.getDistance(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) <= (double)this.breakRange.getValue().floatValue() && (BlockUtil.rayTracePlaceCheck(packPos) || CrystalAura.mc.thePlayer.getDistance(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) <= (double)this.breakWallRange.getValue().floatValue()) && (this.ignoreUseAmount.getValue() != false || !this.target.isDead) && this.target.getHealth() + this.target.getAbsorptionAmount() > 0.0f;
        }
        catch (Exception e) {
            return false;
        }
    }

    boolean IsValidCrystal(Entity p_Entity) {
        try {
            if (p_Entity == null) {
                return false;
            }
            if (!(p_Entity instanceof EntityEnderCrystal)) {
                return false;
            }
            if (this.target == null) {
                return false;
            }
            if (p_Entity.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer) > this.breakRange.getValue().floatValue()) {
                return false;
            }
            if (!CrystalAura.mc.thePlayer.canEntityBeSeen(p_Entity) && p_Entity.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer) > this.breakWallRange.getValue().floatValue()) {
                return false;
            }
            if (p_Entity.isDead) {
                return false;
            }
            if (!this.ignoreUseAmount.getValue().booleanValue() && this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) {
                return false;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return true;
    }

    EntityPlayer getTarget() {
        EntityPlayer closestPlayer = null;
        try {
            for (EntityPlayer entity : CrystalAura.mc.theWorld.playerEntities) {
                if (CrystalAura.mc.thePlayer == null || entity.entityId == -1337420 || CrystalAura.mc.thePlayer.isDead || entity.isDead || entity == CrystalAura.mc.thePlayer || Cascade.friendManager.isFriend(entity.getCommandSenderName()) || entity.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer) > 12.0f) continue;
                this.armorTarget = false;
                for (ItemStack is : entity.func_184193_aE()) {
                    float green;
                    float red;
                    int dmg;
                    if (is.field_190928_g) {
                        this.armorTarget = true;
                    }
                    if ((float)(dmg = 100 - (int)((red = 1.0f - (green = ((float)is.getMaxDurability() - (float)is.getCurrentDurability()) / (float)is.getMaxDurability())) * 100.0f)) > this.minArmor.getValue().floatValue()) continue;
                    this.armorTarget = true;
                }
                if (EntityUtil.isInHole((Entity)entity) && entity.getAbsorptionAmount() + entity.getHealth() > this.facePlace.getValue().floatValue() && !this.armorTarget && this.minDamage.getValue().floatValue() > 2.2f) continue;
                if (closestPlayer == null) {
                    closestPlayer = entity;
                    continue;
                }
                if (closestPlayer.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer) <= entity.getDistanceToEntity((Entity)CrystalAura.mc.thePlayer)) continue;
                closestPlayer = entity;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (closestPlayer != null && this.predictMotion.getValue().booleanValue()) {
            float f = closestPlayer.width / 2.0f;
            float f2 = closestPlayer.height;
            closestPlayer.func_174826_a(new AxisAlignedBB(closestPlayer.posX - (double)f, closestPlayer.posY, closestPlayer.posZ - (double)f, closestPlayer.posX + (double)f, closestPlayer.posY + (double)f2, closestPlayer.posZ + (double)f));
            Entity y = MathUtil.getPredictedPosition(closestPlayer, this.motionTicks.getValue().intValue());
            closestPlayer.func_174826_a(y.func_174813_aQ());
        }
        return closestPlayer;
    }

    NonNullList<BlockPos> placePostions(float placeRange) {
        NonNullList positions = NonNullList.func_191196_a();
        positions.addAll((Collection)MathUtil.getSphere(new BlockPos(Math.floor(CrystalAura.mc.thePlayer.posX), Math.floor(CrystalAura.mc.thePlayer.posY), Math.floor(CrystalAura.mc.thePlayer.posZ)), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> this.canPlaceCrystal((BlockPos)pos, true)).collect(Collectors.toList()));
        return positions;
    }

    boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
        if (!BlockUtil.rayTracePlaceCheck(blockPos) ? CrystalAura.mc.thePlayer.func_174818_b(blockPos) > MathUtil.square(this.placeWallRange.getValue().floatValue()) : CrystalAura.mc.thePlayer.func_174818_b(blockPos) > MathUtil.square(this.placeRange.getValue().floatValue())) {
            return false;
        }
        try {
            if (this.holePlacement.getValue().booleanValue()) {
                if (CrystalAura.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.bedrock && CrystalAura.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.obsidian) {
                    return false;
                }
                if (CrystalAura.mc.theWorld.func_180495_p(boost).func_177230_c() != Blocks.air) {
                    return false;
                }
                if (CrystalAura.mc.theWorld.func_180495_p(boost2).func_177230_c() != Blocks.air) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty();
                }
                for (Entity entity : CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            } else {
                if (CrystalAura.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.bedrock && CrystalAura.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.obsidian) {
                    return false;
                }
                if (CrystalAura.mc.theWorld.func_180495_p(boost).func_177230_c() != Blocks.air) {
                    return false;
                }
                if (CrystalAura.mc.theWorld.func_180495_p(boost2).func_177230_c() != Blocks.air) {
                    return false;
                }
                if (!specialEntityCheck) {
                    return CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
                }
                for (Entity entity : CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
                for (Entity entity : CrystalAura.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                    if (entity instanceof EntityEnderCrystal) continue;
                    return false;
                }
            }
        }
        catch (Exception ex) {
            Cascade.LOGGER.info("Caught an exception from CrystalAura");
            ex.printStackTrace();
        }
        return true;
    }

    float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double)doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = MathUtil.getBlockDensity(vec3d, entity.func_174813_aQ());
        }
        catch (Exception ex) {
            Cascade.LOGGER.info("Caught an exception from CrystalAura");
            ex.printStackTrace();
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * (double)doubleExplosionSize + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            try {
                finald = MathUtil.getBlastReduction((EntityLivingBase)entity, this.getDamageMultiplied(damage), new Explosion((World)CrystalAura.mc.theWorld, null, posX, posY, posZ, 6.0f, false, true));
            }
            catch (Exception ex2) {
                Cascade.LOGGER.info("Caught an exception from CrystalAura");
                ex2.printStackTrace();
            }
        }
        return (float)finald;
    }

    private float getDamageMultiplied(float damage) {
        int diff = CrystalAura.mc.theWorld.func_175659_aa().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static class CalculationThread
    extends Thread {
        @Override
        public void run() {
            while (CrystalAura.getInstance().isEnabled()) {
                try {
                    CrystalAura.getInstance().doCalculations();
                    TimeUnit.MILLISECONDS.sleep(50L);
                }
                catch (Exception exception) {}
            }
        }
    }

    static enum HUD {
        Target,
        Damage;

    }

    static enum SwingMode {
        MainHand,
        OffHand,
        None;

    }

    static enum Swap {
        Off,
        Silent;

    }

    static enum NoSuicide {
        Slow,
        Fast;

    }

    static enum Page {
        Place,
        Break,
        Misc,
        Render;

    }
}


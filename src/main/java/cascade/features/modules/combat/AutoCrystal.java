/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.item.EntityEnderCrystal
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
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
 *  net.minecraft.util.CombatRules
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.event.events.PacketEvent;
import cascade.event.events.Render3DEvent;
import cascade.event.events.UpdateWalkingPlayerEvent;
import cascade.features.modules.Module;
import cascade.features.modules.core.ClickGui;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.MathUtil;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.render.ColorUtil;
import cascade.util.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
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
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoCrystal
extends Module {
    private Timer placeTimer = new Timer();
    private Timer breakTimer = new Timer();
    private Timer predictTimer = new Timer();
    private Timer swapTimer = new Timer();
    public Setting<Page> page = this.register(new Setting<Page>("Page", Page.Place));
    public Setting<Boolean> place = this.register(new Setting<Object>("Place", Boolean.valueOf(true), v -> this.page.getValue() == Page.Place));
    public Setting<Float> placeRange = this.register(new Setting<Object>("PlaceRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    public Setting<Float> placeWallRange = this.register(new Setting<Object>("PlaceWallRange", Float.valueOf(3.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    public Setting<Float> placeDelay = this.register(new Setting<Object>("PlaceDelay", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(300.0f), p -> this.place.getValue() != false && this.page.getValue() == Page.Place));
    public Setting<Float> minDamage = this.register(new Setting<Object>("MinDamage", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Float> maxSelfDamage = this.register(new Setting<Object>("MaxSelfDamage", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Float> facePlace = this.register(new Setting<Object>("FacePlaceHP", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(36.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Float> minArmor = this.register(new Setting<Object>("MinArmor", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(100.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Float> targetRange = this.register(new Setting<Object>("TargetRange", Float.valueOf(4.0f), Float.valueOf(1.0f), Float.valueOf(16.0f), v -> this.page.getValue() == Page.Place));
    public Setting<Boolean> predictMotion = this.register(new Setting<Object>("PredictMotion", Boolean.valueOf(true), v -> this.page.getValue() == Page.Place));
    public Setting<Integer> motionTicks = this.register(new Setting<Object>("MotionTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(15), v -> this.predictMotion.getValue() != false && this.page.getValue() == Page.Place));
    public Setting<Boolean> explode = this.register(new Setting<Object>("Break", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Float> breakDelay = this.register(new Setting<Object>("BreakDelay", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(300.0f), v -> this.page.getValue() == Page.Break));
    public Setting<Float> breakRange = this.register(new Setting<Object>("BreakRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Break));
    public Setting<Float> breakWallRange = this.register(new Setting<Object>("BreakWallRange", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.Break));
    public Setting<Boolean> packetBreak = this.register(new Setting<Object>("PacketBreak", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Boolean> predicts = this.register(new Setting<Object>("Predict", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Integer> attackFactor = this.register(new Setting<Object>("PredictDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), p -> this.predicts.getValue() != false && this.page.getValue() == Page.Break));
    public Setting<Boolean> remove = this.register(new Setting<Object>("Remove", Boolean.valueOf(true), v -> this.page.getValue() == Page.Break));
    public Setting<Integer> ticksExisted = this.register(new Setting<Object>("TicksExisted", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(5), p -> this.predicts.getValue() != false && this.page.getValue() == Page.Break));
    public Setting<Boolean> await;
    public Setting<Boolean> noSuicide = this.register(new Setting<Object>("NoSuicide", Boolean.valueOf(true), v -> this.page.getValue() == Page.Misc));
    public Setting<Float> safetyFactor;
    public Setting<SwapMode> swapType = this.register(new Setting<Object>("Switch", (Object)SwapMode.Off, v -> this.page.getValue() == Page.Misc));
    public Setting<Integer> autoSwitchCooldown = this.register(new Setting<Object>("Cooldown", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(200), p -> this.swapType.getValue() == SwapMode.Normal && this.page.getValue() == Page.Misc));
    public Setting<Boolean> ignoreUseAmount = this.register(new Setting<Object>("IgnoreUseAmount", Boolean.valueOf(true), v -> this.page.getValue() == Page.Misc));
    public Setting<Integer> wasteAmount = this.register(new Setting<Object>("UseAmount", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(5), v -> this.page.getValue() == Page.Misc));
    public Setting<SwingMode> swingMode = this.register(new Setting<Object>("Swing", (Object)SwingMode.MainHand, v -> this.page.getValue() == Page.Misc));
    public Setting<Boolean> render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    public Setting<Boolean> renderDmg = this.register(new Setting<Object>("RenderDmg", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    public Setting<HUD> hud = this.register(new Setting<Object>("HUD", (Object)HUD.Target, v -> this.page.getValue() == Page.Render));
    public Setting<Boolean> box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    public Setting<Float> lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.page.getValue() == Page.Render));
    public Setting<Boolean> outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.page.getValue() == Page.Render));
    public Setting<Boolean> fadeSlower = this.register(new Setting<Object>("FadeSlower", Boolean.valueOf(false), v -> this.page.getValue() == Page.Render));
    private ConcurrentHashMap<BlockPos, Integer> renderSpots;
    EntityEnderCrystal crystal;
    public EntityLivingBase target;
    public BlockPos pos;
    public BlockPos calcPos;
    private int hotBarSlot;
    private boolean armor;
    private boolean armorTarget;
    private int crystalCount;
    double damage = 0.5;
    private EntityLivingBase realTarget;
    private boolean exploded = false;
    private boolean confirmed = true;
    private CalculationThread calculationThread;
    public EnumHand hand;
    public static AutoCrystal INSTANCE;

    public static AutoCrystal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AutoCrystal();
        }
        return INSTANCE;
    }

    public AutoCrystal() {
        super("AutoCrystal", Module.Category.COMBAT, "babbaj moment");
        INSTANCE = this;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = loc.func_177958_n();
        int cy = loc.func_177956_o();
        int cz = loc.func_177952_p();
        int x = cx - (int)r;
        while ((float)x <= (float)cx + r) {
            int z = cz - (int)r;
            while ((float)z <= (float)cz + r) {
                float f2;
                float f;
                int y = sphere ? cy - (int)r : cy;
                while (!((float)y >= (f = (f2 = sphere ? (float)cy + r : (float)(cy + h))))) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double)(r * r) && (!hollow || dist >= (double)((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }

    @Override
    public void onDisable() {
        if (!AutoCrystal.fullNullCheck() && this.shouldNotify()) {
            TextComponentString text = new TextComponentString(Cascade.chatManager.getClientMessage() + " " + ChatFormatting.RED + this.name + " toggled off.");
            AutoCrystal.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent e) {
        if (AutoCrystal.fullNullCheck() || this.isDisabled()) {
            return;
        }
        mc.addScheduledTask(() -> this.onCrystal());
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity cPacketUseEntity;
        if (AutoCrystal.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("SelfFill")) {
            return;
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (cPacketUseEntity = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && cPacketUseEntity.getEntityFromWorld((World)AutoCrystal.mc.theWorld) instanceof EntityEnderCrystal) {
            if (this.remove.getValue().booleanValue()) {
                Objects.requireNonNull(cPacketUseEntity.getEntityFromWorld((World)AutoCrystal.mc.theWorld)).setDead();
                AutoCrystal.mc.theWorld.removeEntityFromWorld(cPacketUseEntity.entityId);
            }
            if (AutoCrystal.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
                int crystalSlot;
                int n = crystalSlot = AutoCrystal.mc.thePlayer.func_184614_ca().getItem() == Items.field_185158_cP ? AutoCrystal.mc.thePlayer.inventory.currentItem : -1;
                if (crystalSlot == -1) {
                    for (int l = 0; l < 9; ++l) {
                        if (AutoCrystal.mc.thePlayer.inventory.getStackInSlot(l).getItem() != Items.field_185158_cP) continue;
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
            if (this.swapType.getValue() == SwapMode.Silent) {
                return;
            }
            if (this.pos != null && AutoCrystal.mc.thePlayer.onGround) {
                RayTraceResult result = AutoCrystal.mc.theWorld.rayTraceBlocks(new Vec3d(AutoCrystal.mc.thePlayer.posX, AutoCrystal.mc.thePlayer.posY + (double)AutoCrystal.mc.thePlayer.getEyeHeight(), AutoCrystal.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                EnumFacing f = result == null || result.field_178784_b == null ? EnumFacing.UP : result.field_178784_b;
                AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f, AutoCrystal.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                AutoCrystal.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
            }
        }
    }

    @Override
    public void onEnable() {
        if (!AutoCrystal.fullNullCheck() && this.shouldNotify()) {
            TextComponentString text = new TextComponentString(Cascade.chatManager.getClientMessage() + " " + ChatFormatting.GREEN + this.name + " toggled on.");
            AutoCrystal.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion((ITextComponent)text, 1);
        }
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
        if (AutoCrystal.fullNullCheck()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("SelfFill")) {
            return;
        }
        this.crystalCount = 0;
        if (!this.ignoreUseAmount.getValue().booleanValue()) {
            for (Entity crystal : AutoCrystal.mc.theWorld.loadedEntityList) {
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
        if (AutoCrystal.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
            int crystalSlot;
            int n = crystalSlot = AutoCrystal.mc.thePlayer.func_184614_ca().getItem() == Items.field_185158_cP ? AutoCrystal.mc.thePlayer.inventory.currentItem : -1;
            if (crystalSlot == -1) {
                for (int l = 0; l < 9; ++l) {
                    if (AutoCrystal.mc.thePlayer.inventory.getStackInSlot(l).getItem() != Items.field_185158_cP) continue;
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
        if (this.target.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer) > 12.0f) {
            this.crystal = null;
            this.target = null;
            this.realTarget = null;
        }
        this.crystal = AutoCrystal.mc.theWorld.loadedEntityList.stream().filter(this::IsValidCrystal).map(p_Entity -> p_Entity).min(Comparator.comparing(p_Entity -> Float.valueOf(this.target.getDistanceToEntity(p_Entity)))).orElse(null);
        if (this.crystal != null && this.explode.getValue().booleanValue()) {
            if (this.crystal.ticksExisted < this.ticksExisted.getValue()) {
                return;
            }
            if (this.breakTimer.passedMs(this.breakDelay.getValue().longValue())) {
                int swordSlot;
                this.breakTimer.reset();
                if (this.swingMode.getValue() == SwingMode.MainHand) {
                    AutoCrystal.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                }
                if (this.swingMode.getValue() == SwingMode.OffHand) {
                    AutoCrystal.mc.thePlayer.func_184609_a(EnumHand.OFF_HAND);
                }
                int oldSlot = AutoCrystal.mc.thePlayer.inventory.currentItem;
                int n = swordSlot = AutoCrystal.mc.thePlayer.func_184614_ca().getItem() == Items.diamond_sword ? AutoCrystal.mc.thePlayer.inventory.currentItem : -1;
                if (swordSlot == -1) {
                    for (int i = 0; i < 9; ++i) {
                        if (AutoCrystal.mc.thePlayer.inventory.getStackInSlot(i).getItem() != Items.diamond_sword) continue;
                        swordSlot = i;
                        break;
                    }
                }
                if (this.packetBreak.getValue().booleanValue()) {
                    AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketUseEntity((Entity)this.crystal));
                } else {
                    AutoCrystal.mc.playerController.attackEntity((EntityPlayer)AutoCrystal.mc.thePlayer, (Entity)this.crystal);
                }
                this.exploded = true;
            }
        }
        if (this.placeTimer.passedMs(this.placeDelay.getValue().longValue()) && this.place.getValue().booleanValue()) {
            this.placeTimer.reset();
            this.pos = this.calcPos;
            int oldSlot = AutoCrystal.mc.thePlayer.inventory.currentItem;
            if (this.pos == null) {
                return;
            }
            if (this.swapType.getValue() == SwapMode.Normal && AutoCrystal.mc.thePlayer.func_184614_ca().getItem() != Items.field_185158_cP && AutoCrystal.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
                AutoCrystal.mc.thePlayer.inventory.currentItem = this.hotBarSlot;
                return;
            }
            if (this.swapType.getValue() == SwapMode.Silent && AutoCrystal.mc.thePlayer.func_184614_ca().getItem() != Items.field_185158_cP) {
                AutoCrystal.mc.thePlayer.inventory.currentItem = this.hotBarSlot;
                AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketHeldItemChange(this.hotBarSlot));
            }
            if (this.swapType.getValue() == SwapMode.Normal) {
                if (!this.swapTimer.passedMs(this.autoSwitchCooldown.getValue().intValue())) {
                    return;
                }
                this.swapTimer.reset();
            }
            if (this.swapType.getValue() != SwapMode.Silent && AutoCrystal.mc.thePlayer.func_184614_ca().getItem() != Items.field_185158_cP && AutoCrystal.mc.thePlayer.func_184592_cb().getItem() != Items.field_185158_cP) {
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
                        RayTraceResult result = AutoCrystal.mc.theWorld.rayTraceBlocks(new Vec3d(AutoCrystal.mc.thePlayer.posX, AutoCrystal.mc.thePlayer.posY + (double)AutoCrystal.mc.thePlayer.getEyeHeight(), AutoCrystal.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                        EnumFacing f = result == null || result.field_178784_b == null ? EnumFacing.UP : result.field_178784_b;
                        AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f, AutoCrystal.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                        AutoCrystal.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                        this.renderSpots.put(this.pos, this.c.getValue().getAlpha());
                    } else {
                        this.exploded = false;
                    }
                }
            } else if (this.pos != null) {
                if (!this.exploded) {
                    RayTraceResult result2 = AutoCrystal.mc.theWorld.rayTraceBlocks(new Vec3d(AutoCrystal.mc.thePlayer.posX, AutoCrystal.mc.thePlayer.posY + (double)AutoCrystal.mc.thePlayer.getEyeHeight(), AutoCrystal.mc.thePlayer.posZ), new Vec3d((double)this.pos.field_177962_a + 0.5, (double)this.pos.field_177960_b + 1.0, (double)this.pos.field_177961_c + 0.5));
                    EnumFacing f2 = result2 == null || result2.field_178784_b == null ? EnumFacing.UP : result2.field_178784_b;
                    AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketPlayerTryUseItemOnBlock(this.pos, f2, AutoCrystal.mc.thePlayer.func_184592_cb().getItem() == Items.field_185158_cP ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (float)this.pos.field_177962_a, (float)this.pos.field_177960_b, (float)this.pos.field_177961_c));
                    AutoCrystal.mc.thePlayer.func_184609_a(EnumHand.MAIN_HAND);
                    this.renderSpots.put(this.pos, this.c.getValue().getAlpha());
                } else {
                    this.exploded = false;
                }
            }
            if (this.swapType.getValue() == SwapMode.Silent) {
                AutoCrystal.mc.thePlayer.inventory.currentItem = oldSlot;
                AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketHeldItemChange(oldSlot));
            }
            this.confirmed = false;
        }
    }

    private void doCalculations() {
        if (this.target == null) {
            this.target = this.getTarget();
        }
        if (this.target == null) {
            return;
        }
        this.damage = 0.5;
        for (BlockPos blockPos : this.placePostions(this.placeRange.getValue().floatValue())) {
            double d;
            if (blockPos == null || this.target == null || !AutoCrystal.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty() || !(this.target.getDistance((double)blockPos.func_177958_n(), (double)blockPos.func_177956_o(), (double)blockPos.func_177952_p()) <= (double)this.targetRange.getValue().floatValue()) || this.target.isDead || this.target.getHealth() + this.target.getAbsorptionAmount() <= 0.0f) continue;
            double targetDmg = this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)this.target);
            double localDmg = this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)AutoCrystal.mc.thePlayer);
            this.armor = false;
            if (localDmg > (double)this.maxSelfDamage.getValue().floatValue()) continue;
            try {
                for (ItemStack is : this.target.func_184193_aE()) {
                    float green = ((float)is.getMaxDurability() - (float)is.getCurrentDurability()) / (float)is.getMaxDurability();
                    float red = 1.0f - green;
                    int dmg = 100 - (int)(red * 100.0f);
                    if ((float)dmg > this.minArmor.getValue().floatValue()) continue;
                    this.armor = true;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (targetDmg < (double)this.minDamage.getValue().floatValue() && this.target.getHealth() + this.target.getAbsorptionAmount() > this.facePlace.getValue().floatValue() && !this.armor) continue;
            double selfDmg = (double)this.calculateDamage((double)blockPos.func_177958_n() + 0.5, (double)blockPos.func_177956_o() + 1.0, (double)blockPos.func_177952_p() + 0.5, (Entity)AutoCrystal.mc.thePlayer) + 4.0;
            if (d >= (double)(AutoCrystal.mc.thePlayer.getHealth() + AutoCrystal.mc.thePlayer.getAbsorptionAmount()) && selfDmg >= targetDmg || this.damage > targetDmg) continue;
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
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (AutoCrystal.fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (Cascade.moduleManager.isModuleEnabled("SelfFill")) {
            return;
        }
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = (SPacketSpawnObject)event.getPacket()).func_148993_l() == 51 && this.predicts.getValue().booleanValue() && this.predictTimer.passedMs(this.attackFactor.getValue().intValue()) && this.predicts.getValue().booleanValue() && this.explode.getValue().booleanValue() && this.packetBreak.getValue().booleanValue() && this.target != null) {
            if (!this.isPredicting(packet)) {
                return;
            }
            CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = packet.func_149001_c();
            predict.action = CPacketUseEntity.Action.ATTACK;
            AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            AutoCrystal.mc.thePlayer.sendQueue.addToSendQueue((Packet)predict);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.isEnabled()) {
            SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)event.getPacket();
            try {
                if (sPacketSoundEffect.func_186977_b() == SoundCategory.BLOCKS && sPacketSoundEffect.func_186978_a() == SoundEvents.field_187539_bB) {
                    for (Entity e : AutoCrystal.mc.theWorld.loadedEntityList) {
                        if (!(e instanceof EntityEnderCrystal) || !(e.getDistance(sPacketSoundEffect.func_149207_d(), sPacketSoundEffect.func_149211_e(), sPacketSoundEffect.func_149210_f()) <= (double)this.breakRange.getValue().floatValue())) continue;
                        Objects.requireNonNull(AutoCrystal.mc.theWorld.getEntityByID(e.getEntityId())).setDead();
                        AutoCrystal.mc.theWorld.removeEntityFromWorld(e.entityId);
                        this.confirmed = true;
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (event.getPacket() instanceof SPacketExplosion) {
            try {
                SPacketExplosion sPacketExplosion = (SPacketExplosion)event.getPacket();
                for (Entity e : AutoCrystal.mc.theWorld.loadedEntityList) {
                    if (!(e instanceof EntityEnderCrystal) || !(e.getDistance(sPacketExplosion.func_149148_f(), sPacketExplosion.func_149143_g(), sPacketExplosion.func_149145_h()) <= (double)this.breakRange.getValue().floatValue())) continue;
                    Objects.requireNonNull(AutoCrystal.mc.theWorld.getEntityByID(e.getEntityId())).setDead();
                    AutoCrystal.mc.theWorld.removeEntityFromWorld(e.entityId);
                    this.confirmed = true;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
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

    private boolean isPredicting(SPacketSpawnObject packet) {
        try {
            BlockPos packPos = new BlockPos(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e());
            return AutoCrystal.mc.thePlayer.getDistance(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) <= (double)this.breakRange.getValue().floatValue() && (BlockUtil.rayTracePlaceCheck(packPos) || AutoCrystal.mc.thePlayer.getDistance(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e()) <= (double)this.breakWallRange.getValue().floatValue()) && (this.ignoreUseAmount.getValue() != false || !this.target.isDead) && this.target.getHealth() + this.target.getAbsorptionAmount() > 0.0f;
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean IsValidCrystal(Entity p_Entity) {
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
            if (p_Entity.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer) > this.breakRange.getValue().floatValue()) {
                return false;
            }
            if (!AutoCrystal.mc.thePlayer.canEntityBeSeen(p_Entity) && p_Entity.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer) > this.breakWallRange.getValue().floatValue()) {
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

    private EntityPlayer getTarget() {
        EntityPlayer closestPlayer = null;
        try {
            for (EntityPlayer entity : AutoCrystal.mc.theWorld.playerEntities) {
                if (AutoCrystal.mc.thePlayer == null || entity.entityId == -1337420 || AutoCrystal.mc.thePlayer.isDead || entity.isDead || entity == AutoCrystal.mc.thePlayer || Cascade.friendManager.isFriend(entity.getCommandSenderName()) || entity.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer) > 12.0f) continue;
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
                if (closestPlayer.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer) <= entity.getDistanceToEntity((Entity)AutoCrystal.mc.thePlayer)) continue;
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
            Entity y = AutoCrystal.getPredictedPosition(closestPlayer, this.motionTicks.getValue().intValue());
            closestPlayer.func_174826_a(y.func_174813_aQ());
        }
        return closestPlayer;
    }

    private NonNullList<BlockPos> placePostions(float placeRange) {
        NonNullList positions = NonNullList.func_191196_a();
        positions.addAll((Collection)AutoCrystal.getSphere(new BlockPos(Math.floor(AutoCrystal.mc.thePlayer.posX), Math.floor(AutoCrystal.mc.thePlayer.posY), Math.floor(AutoCrystal.mc.thePlayer.posZ)), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> this.canPlaceCrystal((BlockPos)pos, true)).collect(Collectors.toList()));
        return positions;
    }

    private boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck) {
        BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
        if (!BlockUtil.rayTracePlaceCheck(blockPos) ? AutoCrystal.mc.thePlayer.func_174818_b(blockPos) > MathUtil.square(this.placeWallRange.getValue().floatValue()) : AutoCrystal.mc.thePlayer.func_174818_b(blockPos) > MathUtil.square(this.placeRange.getValue().floatValue())) {
            return false;
        }
        try {
            if (AutoCrystal.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.bedrock && AutoCrystal.mc.theWorld.func_180495_p(blockPos).func_177230_c() != Blocks.obsidian) {
                return false;
            }
            if (AutoCrystal.mc.theWorld.func_180495_p(boost).func_177230_c() != Blocks.air) {
                return false;
            }
            if (AutoCrystal.mc.theWorld.func_180495_p(boost2).func_177230_c() != Blocks.air) {
                return false;
            }
            if (!specialEntityCheck) {
                return AutoCrystal.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && AutoCrystal.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }
            for (Entity entity : AutoCrystal.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
            for (Entity entity : AutoCrystal.mc.theWorld.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2))) {
                if (entity instanceof EntityEnderCrystal) continue;
                return false;
            }
        }
        catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 12.0f;
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = 0.0;
        try {
            blockDensity = this.getBlockDensity(vec3d, entity.func_174813_aQ());
        }
        catch (Exception exception) {
            // empty catch block
        }
        double v = (1.0 - distancedsize) * blockDensity;
        float damage = (int)((v * v + v) / 2.0 * 7.0 * 12.0 + 1.0);
        double finald = 1.0;
        if (entity instanceof EntityLivingBase) {
            try {
                finald = this.getBlastReduction((EntityLivingBase)entity, this.getDamageMultiplied(damage), new Explosion((World)AutoCrystal.mc.theWorld, (Entity)null, posX, posY, posZ, 6.0f, false, true));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return (float)finald;
    }

    public float getBlockDensity(Vec3d vec, AxisAlignedBB bb) {
        double d0 = 1.0 / ((bb.maxX - bb.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((bb.maxY - bb.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((bb.maxZ - bb.minZ) * 2.0 + 1.0);
        double d4 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d5 = (1.0 - Math.floor(1.0 / d3) * d3) / 2.0;
        if (d0 >= 0.0 && d2 >= 0.0 && d3 >= 0.0) {
            int j2 = 0;
            int k2 = 0;
            for (float f = 0.0f; f <= 1.0f; f += (float)d0) {
                for (float f2 = 0.0f; f2 <= 1.0f; f2 += (float)d2) {
                    for (float f3 = 0.0f; f3 <= 1.0f; f3 += (float)d3) {
                        double d6 = bb.minX + (bb.maxX - bb.minX) * (double)f;
                        double d7 = bb.minY + (bb.maxY - bb.minY) * (double)f2;
                        double d8 = bb.minZ + (bb.maxZ - bb.minZ) * (double)f3;
                        if (AutoCrystal.rayTraceBlocks(new Vec3d(d6 + d4, d7, d8 + d5), vec, false, false, false, true) == null) {
                            ++j2;
                        }
                        ++k2;
                    }
                }
            }
            return (float)j2 / (float)k2;
        }
        return 0.0f;
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreNoBox, boolean returnLastUncollidableBlock, boolean ignoreWebs) {
        if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
            return null;
        }
        if (!(Double.isNaN(vec32.xCoord) || Double.isNaN(vec32.yCoord) || Double.isNaN(vec32.zCoord))) {
            RayTraceResult raytraceresult;
            int x1 = MathHelper.floor_double((double)vec31.xCoord);
            int y1 = MathHelper.floor_double((double)vec31.yCoord);
            int z1 = MathHelper.floor_double((double)vec31.zCoord);
            int x2 = MathHelper.floor_double((double)vec32.xCoord);
            int y2 = MathHelper.floor_double((double)vec32.yCoord);
            int z2 = MathHelper.floor_double((double)vec32.zCoord);
            BlockPos pos = new BlockPos(x1, y1, z1);
            IBlockState state = AutoCrystal.mc.theWorld.func_180495_p(pos);
            Block block = state.func_177230_c();
            if (!(ignoreNoBox && state.func_185890_d((IBlockAccess)AutoCrystal.mc.theWorld, pos) == Block.field_185506_k || !block.func_176209_a(state, stopOnLiquid) || ignoreWebs && block instanceof BlockWeb || (raytraceresult = state.func_185910_a((World)AutoCrystal.mc.theWorld, pos, vec31, vec32)) == null)) {
                return raytraceresult;
            }
            RayTraceResult raytraceresult2 = null;
            int k1 = 200;
            while (k1-- >= 0) {
                EnumFacing enumfacing;
                if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord)) {
                    return null;
                }
                if (x1 == x2 && y1 == y2 && z1 == z2) {
                    return returnLastUncollidableBlock ? raytraceresult2 : null;
                }
                boolean flag2 = true;
                boolean flag3 = true;
                boolean flag4 = true;
                double d0 = 999.0;
                double d2 = 999.0;
                double d3 = 999.0;
                if (x2 > x1) {
                    d0 = (double)x1 + 1.0;
                } else if (x2 < x1) {
                    d0 = (double)x1 + 0.0;
                } else {
                    flag2 = false;
                }
                if (y2 > y1) {
                    d2 = (double)y1 + 1.0;
                } else if (y2 < y1) {
                    d2 = (double)y1 + 0.0;
                } else {
                    flag3 = false;
                }
                if (z2 > z1) {
                    d3 = (double)z1 + 1.0;
                } else if (z2 < z1) {
                    d3 = (double)z1 + 0.0;
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
                    enumfacing = x2 > x1 ? EnumFacing.WEST : EnumFacing.EAST;
                    vec31 = new Vec3d(d0, vec31.yCoord + d8 * d4, vec31.zCoord + d9 * d4);
                } else if (d5 < d6) {
                    enumfacing = y2 > y1 ? EnumFacing.DOWN : EnumFacing.UP;
                    vec31 = new Vec3d(vec31.xCoord + d7 * d5, d2, vec31.zCoord + d9 * d5);
                } else {
                    enumfacing = z2 > z1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    vec31 = new Vec3d(vec31.xCoord + d7 * d6, vec31.yCoord + d8 * d6, d3);
                }
                x1 = MathHelper.floor_double((double)vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                y1 = MathHelper.floor_double((double)vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                z1 = MathHelper.floor_double((double)vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                pos = new BlockPos(x1, y1, z1);
                IBlockState state2 = AutoCrystal.mc.theWorld.func_180495_p(pos);
                Block block2 = state2.func_177230_c();
                if (ignoreNoBox && state2.func_185904_a() != Material.portal && state2.func_185890_d((IBlockAccess)AutoCrystal.mc.theWorld, pos) == Block.field_185506_k) continue;
                if (!(!block2.func_176209_a(state2, stopOnLiquid) || ignoreWebs && block2 instanceof BlockWeb)) {
                    RayTraceResult raytraceresult3 = state2.func_185910_a((World)AutoCrystal.mc.theWorld, pos, vec31, vec32);
                    if (raytraceresult3 == null) continue;
                    return raytraceresult3;
                }
                raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, pos);
            }
            return returnLastUncollidableBlock ? raytraceresult2 : null;
        }
        return null;
    }

    private float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
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

    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0.0) {
            return entity;
        }
        EntityPlayer e = null;
        double mX = entity.posX - entity.lastTickPosX;
        double mY = entity.posY - entity.lastTickPosY;
        double mZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(mX, 2.0) + Math.pow(mZ, 2.0) + Math.pow(mY, 2.0));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        int i = 0;
        while ((double)i < x) {
            if (e == null) {
                if (AutoCrystal.isOnGround(0.0, 0.0, 0.0, entity)) {
                    mY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    mY -= 0.08;
                    mY *= (double)0.98f;
                }
                e = AutoCrystal.placeValue(mX, mY, mZ, (EntityPlayer)entity);
            } else {
                if (AutoCrystal.isOnGround(0.0, 0.0, 0.0, e)) {
                    mY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    mY -= 0.08;
                    mY *= (double)0.98f;
                }
                e = AutoCrystal.placeValue(mX, mY, mZ, e);
            }
            ++i;
        }
        return e;
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        try {
            double d3 = y;
            List list1 = AutoCrystal.mc.theWorld.func_184144_a(entity, entity.func_174813_aQ().addCoord(x, y, z));
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

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List list1 = AutoCrystal.mc.theWorld.func_184144_a((Entity)entity, entity.func_174813_aQ().addCoord(x, y, z));
        if (y != 0.0) {
            int l = list1.size();
            for (int k = 0; k < l; ++k) {
                y = ((AxisAlignedBB)list1.get(k)).calculateYOffset(entity.func_174813_aQ(), y);
            }
            if (y != 0.0) {
                entity.func_174826_a(entity.func_174813_aQ().offset(0.0, y, 0.0));
            }
        }
        if (x != 0.0) {
            int l2 = list1.size();
            for (int j5 = 0; j5 < l2; ++j5) {
                x = AutoCrystal.calculateXOffset(entity.func_174813_aQ(), x, (AxisAlignedBB)list1.get(j5));
            }
            if (x != 0.0) {
                entity.func_174826_a(entity.func_174813_aQ().offset(x, 0.0, 0.0));
            }
        }
        if (z != 0.0) {
            int i6 = list1.size();
            for (int k2 = 0; k2 < i6; ++k2) {
                z = AutoCrystal.calculateZOffset(entity.func_174813_aQ(), z, (AxisAlignedBB)list1.get(k2));
            }
            if (z != 0.0) {
                entity.func_174826_a(entity.func_174813_aQ().offset(0.0, 0.0, z));
            }
        }
        return entity;
    }

    public static double calculateXOffset(AxisAlignedBB other, double OffsetX, AxisAlignedBB this1) {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ) {
            double d2;
            if (OffsetX > 0.0 && other.maxX <= this1.minX) {
                double d1 = this1.minX - 0.3 - other.maxX;
                if (d1 < OffsetX) {
                    OffsetX = d1;
                }
            } else if (OffsetX < 0.0 && other.minX >= this1.maxX && (d2 = this1.maxX + 0.3 - other.minX) > OffsetX) {
                OffsetX = d2;
            }
        }
        return OffsetX;
    }

    public static double calculateZOffset(AxisAlignedBB other, double OffsetZ, AxisAlignedBB this1) {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY) {
            double d2;
            if (OffsetZ > 0.0 && other.maxZ <= this1.minZ) {
                double d1 = this1.minZ - 0.3 - other.maxZ;
                if (d1 < OffsetZ) {
                    OffsetZ = d1;
                }
            } else if (OffsetZ < 0.0 && other.minZ >= this1.maxZ && (d2 = this1.maxZ + 0.3 - other.minZ) > OffsetZ) {
                OffsetZ = d2;
            }
        }
        return OffsetZ;
    }

    private float getDamageMultiplied(float damage) {
        int diff = AutoCrystal.mc.theWorld.func_175659_aa().getDifficultyId();
        return damage * (diff == 0 ? 0.0f : (diff == 2 ? 1.0f : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static class CalculationThread
    extends Thread {
        @Override
        public void run() {
            while (AutoCrystal.getInstance().isEnabled()) {
                try {
                    AutoCrystal.getInstance().doCalculations();
                    TimeUnit.MILLISECONDS.sleep(50L);
                }
                catch (Exception exception) {}
            }
        }
    }

    public static enum SwapMode {
        Off,
        Normal,
        Silent;

    }

    public static enum SwingMode {
        MainHand,
        OffHand,
        None;

    }

    private static enum Page {
        Place,
        Break,
        Misc,
        Render;

    }

    public static enum HUD {
        Target,
        Damage;

    }
}


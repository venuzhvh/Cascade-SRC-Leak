/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.mojang.realmsclient.gui.ChatFormatting
 *  net.minecraft.block.BlockWeb
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.combat;

import cascade.Cascade;
import cascade.features.command.Command;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.entity.EntityUtil;
import cascade.util.misc.Timer;
import cascade.util.player.BlockUtil;
import cascade.util.player.InventoryUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WebAura
extends Module {
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(6.0f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> motionPredict = this.register(new Setting<Boolean>("MotionPredict", true));
    Setting<Boolean> head = this.register(new Setting<Boolean>("Head", true));
    Setting<Boolean> feet = this.register(new Setting<Boolean>("Feet", true));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Timer timer = new Timer();
    List<Vec3d> placeTargets;
    EntityPlayer target;
    boolean isSneaking;

    public WebAura() {
        super("WebAura", Module.Category.COMBAT, "Traps enemies with webs");
    }

    @Override
    public void onEnable() {
        if (WebAura.fullNullCheck()) {
            return;
        }
        this.isSneaking = WebAura.mc.thePlayer.isSneaking();
    }

    @Override
    public void onDisable() {
        if (WebAura.fullNullCheck()) {
            return;
        }
        this.isSneaking = EntityUtil.stopSneaking(false);
    }

    @Override
    public void onToggle() {
        this.timer.reset();
        this.placeTargets = new ArrayList<Vec3d>();
        this.target = null;
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getCommandSenderName();
        }
        return null;
    }

    @Override
    public void onUpdate() {
        if (WebAura.fullNullCheck()) {
            return;
        }
        this.target = null;
        this.getTarget();
        mc.addScheduledTask(() -> this.doWeb());
    }

    void doWeb() {
        if (this.target != null) {
            for (Vec3d pos : this.placeTargets) {
                if (WebAura.mc.theWorld.func_180495_p(new BlockPos(pos)).func_177230_c() != Blocks.air) continue;
                int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
                int ogSlot = WebAura.mc.thePlayer.inventory.currentItem;
                if (webSlot != -1) {
                    if (!this.timer.passedMs(this.delay.getValue().intValue())) continue;
                    InventoryUtil.packetSwap(webSlot);
                    BlockUtil.placeBlock(new BlockPos(pos), EnumHand.MAIN_HAND, this.rotate.getValue(), this.packet.getValue(), this.isSneaking, true);
                    InventoryUtil.packetSwap(ogSlot);
                    EntityUtil.stopSneaking(false);
                    continue;
                }
                Command.sendMessage("Out of webs, disabling " + ChatFormatting.RED + this.name);
                this.disable();
                return;
            }
        }
    }

    void getTarget() {
        this.target = null;
        this.placeTargets = new ArrayList<Vec3d>();
        for (EntityPlayer e : WebAura.mc.theWorld.playerEntities) {
            if (e == null || e.getHealth() > 0.0f || e == WebAura.mc.thePlayer || WebAura.mc.thePlayer.getDistanceToEntity((Entity)e) > this.range.getValue().floatValue() || Cascade.friendManager.isFriend(e.getCommandSenderName())) continue;
            this.target = e;
        }
        if (this.target != null) {
            this.placeTargets = this.getPlacements();
        }
    }

    List<Vec3d> getPlacements() {
        ArrayList<Vec3d> list = new ArrayList<Vec3d>();
        Vec3d baseVec = this.target.func_174791_d();
        if (this.feet.getValue().booleanValue()) {
            if (this.motionPredict.getValue().booleanValue()) {
                list.add(baseVec.addVector(0.0 + this.target.motionX, 0.0, 0.0 + this.target.motionZ));
            }
            list.add(baseVec.addVector(0.0, 0.0, 0.0));
        }
        if (this.head.getValue().booleanValue()) {
            if (this.motionPredict.getValue().booleanValue()) {
                list.add(baseVec.addVector(0.0 + this.target.motionX, 1.0, 0.0 + this.target.motionZ));
            }
            list.add(baseVec.addVector(0.0, 1.0, 0.0));
        }
        return list;
    }
}


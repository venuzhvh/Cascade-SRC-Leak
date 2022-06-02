/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.player.BlockUtil;
import cascade.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleESP
extends Module {
    Setting<Integer> range = this.register(new Setting<Integer>("Range", 7, 1, 10));
    Setting<Boolean> box = this.register(new Setting<Boolean>("Box", true));
    Setting<Color> obbyC = this.register(new Setting<Object>("ObsidianColor", new Color(-1), v -> this.box.getValue()));
    Setting<Color> bedC = this.register(new Setting<Object>("BedrockColor", new Color(-1), v -> this.box.getValue()));
    Setting<Boolean> outline = this.register(new Setting<Boolean>("Outline", true));
    Setting<Color> obbyOutlineC = this.register(new Setting<Object>("ObsidianOutline", new Color(-1), v -> this.outline.getValue()));
    Setting<Color> bedOutlineC = this.register(new Setting<Object>("BedrockOutline", new Color(-1), v -> this.outline.getValue()));
    Setting<Boolean> cross = this.register(new Setting<Boolean>("Cross", false));
    Setting<Color> obbyCrossC = this.register(new Setting<Object>("ObsidianCross", new Color(-1), v -> this.cross.getValue()));
    Setting<Color> bedCrossC = this.register(new Setting<Object>("BedrockCross", new Color(-1), v -> this.cross.getValue()));
    Setting<Float> lw = this.register(new Setting<Object>("LineWidth", Float.valueOf(0.1f), Float.valueOf(0.1f), Float.valueOf(6.0f), v -> this.outline.getValue() != false || this.cross.getValue() != false));
    Setting<Boolean> doubleHoles = this.register(new Setting<Boolean>("DoubleHoles", true));
    Setting<Boolean> inFov = this.register(new Setting<Boolean>("InFov", false));
    Setting<Boolean> renderOwn = this.register(new Setting<Boolean>("RenderOwn", true));
    private static HoleESP INSTANCE;

    public HoleESP() {
        super("HoleESP", Module.Category.VISUAL, "Shows safe spots");
        INSTANCE = this;
    }

    public static HoleESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HoleESP();
        }
        return INSTANCE;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (HoleESP.mc.field_175622_Z != null) {
            Vec3i playerPos = new Vec3i(HoleESP.mc.field_175622_Z.posX, HoleESP.mc.field_175622_Z.posY, HoleESP.mc.field_175622_Z.posZ);
            for (int x = playerPos.func_177958_n() - this.range.getValue(); x < playerPos.func_177958_n() + this.range.getValue(); ++x) {
                for (int z = playerPos.func_177952_p() - this.range.getValue(); z < playerPos.func_177952_p() + this.range.getValue(); ++z) {
                    for (int y = playerPos.func_177956_o() + this.range.getValue(); y > playerPos.func_177956_o() - this.range.getValue(); --y) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if ((HoleESP.mc.theWorld.func_180495_p(pos).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c() != Blocks.air || pos == new BlockPos(HoleESP.mc.thePlayer.posX, HoleESP.mc.thePlayer.posY, HoleESP.mc.thePlayer.posZ)) && !this.renderOwn.getValue().booleanValue() || !BlockUtil.isPosInFov(pos).booleanValue() && this.inFov.getValue().booleanValue()) continue;
                        if (this.doubleHoles.getValue().booleanValue()) {
                            if (HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() == Blocks.air && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177984_a()).func_177230_c() == Blocks.air && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177977_b()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177964_d(2)).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177974_f()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177976_e()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() == Blocks.bedrock) {
                                RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.bedC.getValue().getRed(), this.bedC.getValue().getGreen(), this.bedC.getValue().getBlue(), this.bedC.getValue().getAlpha()), new Color(this.bedOutlineC.getValue().getRed(), this.bedOutlineC.getValue().getGreen(), this.bedOutlineC.getValue().getBlue(), this.bedOutlineC.getValue().getAlpha()), new Color(this.bedCrossC.getValue().getRed(), this.bedCrossC.getValue().getGreen(), this.bedCrossC.getValue().getBlue(), this.bedCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                                RenderUtil.drawHoleESP(pos.func_177978_c(), this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.bedC.getValue().getRed(), this.bedC.getValue().getGreen(), this.bedC.getValue().getBlue(), this.bedC.getValue().getAlpha()), new Color(this.bedOutlineC.getValue().getRed(), this.bedOutlineC.getValue().getGreen(), this.bedOutlineC.getValue().getBlue(), this.bedOutlineC.getValue().getAlpha()), new Color(this.bedCrossC.getValue().getRed(), this.bedCrossC.getValue().getGreen(), this.bedCrossC.getValue().getBlue(), this.bedCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                            } else if (!(HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177984_a()).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177977_b()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177977_b()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177964_d(2)).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177964_d(2)).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177974_f()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177974_f()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177976_e()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c().func_177976_e()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() != Blocks.bedrock || HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() != Blocks.obsidian && HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() != Blocks.bedrock)) {
                                RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.obbyC.getValue().getRed(), this.obbyC.getValue().getGreen(), this.obbyC.getValue().getBlue(), this.obbyC.getValue().getAlpha()), new Color(this.obbyOutlineC.getValue().getRed(), this.obbyOutlineC.getValue().getGreen(), this.obbyOutlineC.getValue().getBlue(), this.obbyOutlineC.getValue().getAlpha()), new Color(this.obbyCrossC.getValue().getRed(), this.obbyCrossC.getValue().getGreen(), this.obbyCrossC.getValue().getBlue(), this.obbyCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                                RenderUtil.drawHoleESP(pos.func_177978_c(), this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.obbyC.getValue().getRed(), this.obbyC.getValue().getGreen(), this.obbyC.getValue().getBlue(), this.obbyC.getValue().getAlpha()), new Color(this.obbyOutlineC.getValue().getRed(), this.obbyOutlineC.getValue().getGreen(), this.obbyOutlineC.getValue().getBlue(), this.obbyOutlineC.getValue().getAlpha()), new Color(this.obbyCrossC.getValue().getRed(), this.obbyCrossC.getValue().getGreen(), this.obbyCrossC.getValue().getBlue(), this.obbyCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                            }
                            if (HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() == Blocks.air && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177984_a()).func_177230_c() == Blocks.air && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177977_b()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177965_g(2)).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177965_g(2).func_177977_b()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177978_c()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177968_d()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() == Blocks.bedrock) {
                                RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.bedC.getValue().getRed(), this.bedC.getValue().getGreen(), this.bedC.getValue().getBlue(), this.bedC.getValue().getAlpha()), new Color(this.bedOutlineC.getValue().getRed(), this.bedOutlineC.getValue().getGreen(), this.bedOutlineC.getValue().getBlue(), this.bedOutlineC.getValue().getAlpha()), new Color(this.bedCrossC.getValue().getRed(), this.bedCrossC.getValue().getGreen(), this.bedCrossC.getValue().getBlue(), this.bedCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                                RenderUtil.drawHoleESP(pos.func_177974_f(), this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.bedC.getValue().getRed(), this.bedC.getValue().getGreen(), this.bedC.getValue().getBlue(), this.bedC.getValue().getAlpha()), new Color(this.bedOutlineC.getValue().getRed(), this.bedOutlineC.getValue().getGreen(), this.bedOutlineC.getValue().getBlue(), this.bedOutlineC.getValue().getAlpha()), new Color(this.bedCrossC.getValue().getRed(), this.bedCrossC.getValue().getGreen(), this.bedCrossC.getValue().getBlue(), this.bedCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                            } else if (!(HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177984_a()).func_177230_c() != Blocks.air || HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177977_b()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177977_b()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177965_g(2)).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177965_g(2)).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177978_c()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177978_c()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177968_d()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f().func_177968_d()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() != Blocks.obsidian || HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() != Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() != Blocks.obsidian)) {
                                RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.obbyC.getValue().getRed(), this.obbyC.getValue().getGreen(), this.obbyC.getValue().getBlue(), this.obbyC.getValue().getAlpha()), new Color(this.obbyOutlineC.getValue().getRed(), this.obbyOutlineC.getValue().getGreen(), this.obbyOutlineC.getValue().getBlue(), this.obbyOutlineC.getValue().getAlpha()), new Color(this.obbyCrossC.getValue().getRed(), this.obbyCrossC.getValue().getGreen(), this.obbyCrossC.getValue().getBlue(), this.obbyCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                                RenderUtil.drawHoleESP(pos.func_177974_f(), this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.obbyC.getValue().getRed(), this.obbyC.getValue().getGreen(), this.obbyC.getValue().getBlue(), this.obbyC.getValue().getAlpha()), new Color(this.obbyOutlineC.getValue().getRed(), this.obbyOutlineC.getValue().getGreen(), this.obbyOutlineC.getValue().getBlue(), this.obbyOutlineC.getValue().getAlpha()), new Color(this.obbyCrossC.getValue().getRed(), this.obbyCrossC.getValue().getGreen(), this.obbyCrossC.getValue().getBlue(), this.obbyCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                            }
                        }
                        if (HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c() == Blocks.bedrock && HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c() == Blocks.bedrock) {
                            RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.bedC.getValue().getRed(), this.bedC.getValue().getGreen(), this.bedC.getValue().getBlue(), this.bedC.getValue().getAlpha()), new Color(this.bedOutlineC.getValue().getRed(), this.bedOutlineC.getValue().getGreen(), this.bedOutlineC.getValue().getBlue(), this.bedOutlineC.getValue().getAlpha()), new Color(this.bedCrossC.getValue().getRed(), this.bedCrossC.getValue().getGreen(), this.bedCrossC.getValue().getBlue(), this.bedCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                            continue;
                        }
                        if (!BlockUtil.isBlockUnSafe(HoleESP.mc.theWorld.func_180495_p(pos.func_177977_b()).func_177230_c()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.theWorld.func_180495_p(pos.func_177974_f()).func_177230_c()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.theWorld.func_180495_p(pos.func_177976_e()).func_177230_c()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.theWorld.func_180495_p(pos.func_177968_d()).func_177230_c()) || !BlockUtil.isBlockUnSafe(HoleESP.mc.theWorld.func_180495_p(pos.func_177978_c()).func_177230_c())) continue;
                        RenderUtil.drawHoleESP(pos, this.box.getValue(), this.outline.getValue(), this.cross.getValue(), new Color(this.obbyC.getValue().getRed(), this.obbyC.getValue().getGreen(), this.obbyC.getValue().getBlue(), this.obbyC.getValue().getAlpha()), new Color(this.obbyOutlineC.getValue().getRed(), this.obbyOutlineC.getValue().getGreen(), this.obbyOutlineC.getValue().getBlue(), this.obbyOutlineC.getValue().getAlpha()), new Color(this.obbyCrossC.getValue().getRed(), this.obbyCrossC.getValue().getGreen(), this.obbyCrossC.getValue().getBlue(), this.obbyCrossC.getValue().getAlpha()), this.lw.getValue().floatValue());
                    }
                }
            }
        }
    }
}


/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 */
package cascade.features.modules.player;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.player.MovementUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Avoid
extends Module {
    Setting<Boolean> fire = this.register(new Setting<Boolean>("Fire", true));
    Setting<Boolean> cactus = this.register(new Setting<Boolean>("Cactus", true));

    public Avoid() {
        super("Avoid", Module.Category.PLAYER, "Avoids certain things");
    }

    @Override
    public void onUpdate() {
        if (Avoid.fullNullCheck()) {
            return;
        }
        BlockPos pos = new BlockPos(Avoid.mc.thePlayer.posX, Avoid.mc.thePlayer.posY, Avoid.mc.thePlayer.posZ);
        if (Avoid.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.fire && this.fire.getValue().booleanValue()) {
            MovementUtil.setMotion(0.0, 0.0, 0.0);
        }
        if (Avoid.mc.theWorld.func_180495_p(pos).func_177230_c() == Blocks.cactus && this.cactus.getValue().booleanValue()) {
            MovementUtil.setMotion(0.0, 0.0, 0.0);
        }
    }
}


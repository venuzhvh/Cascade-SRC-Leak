/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.Vec3d
 */
package cascade.features.modules.visual;

import cascade.event.events.Render3DEvent;
import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import cascade.util.misc.MathUtil;
import cascade.util.shader.shaders.RainbowOutlineShader;
import java.util.Objects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class ShaderChams
extends Module {
    public Setting<ShaderMode> mode = this.register(new Setting<ShaderMode>("Mode", ShaderMode.RainbowOutline));

    public ShaderChams() {
        super("ShaderChams", Module.Category.VISUAL, "Makes shader on cham");
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (ShaderChams.fullNullCheck()) {
            return;
        }
        RainbowOutlineShader framebufferShader = null;
        if (this.mode.getValue().equals((Object)ShaderMode.RainbowOutline)) {
            framebufferShader = RainbowOutlineShader.RAINBOW_OUTLINE_SHADER;
        }
        if (framebufferShader == null) {
            return;
        }
        GlStateManager.func_179128_n((int)5889);
        GlStateManager.func_179094_E();
        GlStateManager.func_179128_n((int)5888);
        GlStateManager.func_179094_E();
        framebufferShader.startDraw(event.getPartialTicks());
        for (Entity entity : ShaderChams.mc.theWorld.loadedEntityList) {
            if (entity == ShaderChams.mc.thePlayer || entity == mc.func_175606_aa() || !(entity instanceof EntityPlayer)) continue;
            Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
            Objects.requireNonNull(mc.func_175598_ae().getEntityRenderObject(entity)).doRender(entity, vector.xCoord, vector.yCoord, vector.zCoord, entity.rotationYaw, event.getPartialTicks());
        }
        framebufferShader.stopDraw();
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179128_n((int)5889);
        GlStateManager.func_179121_F();
        GlStateManager.func_179128_n((int)5888);
        GlStateManager.func_179121_F();
    }

    public static enum ShaderMode {
        RainbowOutline;

    }
}


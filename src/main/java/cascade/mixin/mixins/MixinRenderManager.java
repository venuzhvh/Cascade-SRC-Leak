/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderManager
 */
package cascade.mixin.mixins;

import cascade.util.misc.IRenderManager;
import net.minecraft.client.renderer.entity.RenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={RenderManager.class})
public abstract class MixinRenderManager
implements IRenderManager {
    @Override
    @Accessor(value="renderPosX")
    public abstract double getRenderPosX();

    @Override
    @Accessor(value="renderPosY")
    public abstract double getRenderPosY();

    @Override
    @Accessor(value="renderPosZ")
    public abstract double getRenderPosZ();
}


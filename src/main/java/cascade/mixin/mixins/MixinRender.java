/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.Render
 */
package cascade.mixin.mixins;

import cascade.features.modules.visual.ESP;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Render.class})
public abstract class MixinRender {
    @Inject(method={"doRenderShadowAndFire"}, at={@At(value="HEAD")}, cancellable=true)
    private void doRenderShadowAndFireHook(CallbackInfo info) {
        if (ESP.isRendering) {
            info.cancel();
        }
    }

    @Inject(method={"renderLivingLabel"}, at={@At(value="HEAD")}, cancellable=true)
    private void renderLivingLabelHook(CallbackInfo info) {
        if (ESP.isRendering) {
            info.cancel();
        }
    }
}


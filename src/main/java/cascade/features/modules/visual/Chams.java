/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Chams
extends Module {
    public Setting<Boolean> solid = this.register(new Setting<Boolean>("Solid", true));
    public Setting<Color> solidC = this.register(new Setting<Color>("SolidColor", new Color(-1)));
    public Setting<Boolean> wireframe = this.register(new Setting<Boolean>("Wireframe", true));
    public Setting<Color> wireC = this.register(new Setting<Color>("WireColor", new Color(-1)));
    public Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
    public Setting<Boolean> texture = this.register(new Setting<Boolean>("Texture", false));
    public Setting<Color> textureColor = this.register(new Setting<Color>("TextureColor", new Color(-1)));
    public Setting<Boolean> glint = this.register(new Setting<Boolean>("Glint", false));
    public Setting<Boolean> model = this.register(new Setting<Boolean>("Model", false));
    private static Chams INSTANCE;

    public Chams() {
        super("Chams", Module.Category.VISUAL, "Player chams");
        this.setInstance();
    }

    public static Chams getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new Chams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (Chams.fullNullCheck() || this.isDisabled()) {
            return;
        }
        event.getEntityPlayer().hurtTime = 0;
    }
}


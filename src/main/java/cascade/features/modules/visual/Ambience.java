/*
 * Decompiled with CFR 0.151.
 */
package cascade.features.modules.visual;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;
import java.awt.Color;

public class Ambience
extends Module {
    public Setting<Color> c = this.register(new Setting<Color>("Color", new Color(-1)));
    static Ambience INSTANCE;

    public Ambience() {
        super("Ambience", Module.Category.VISUAL, "Ambience tweaks");
        INSTANCE = this;
    }

    public static Ambience getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ambience();
        }
        return INSTANCE;
    }
}


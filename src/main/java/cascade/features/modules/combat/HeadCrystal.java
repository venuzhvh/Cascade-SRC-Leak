/*
 * Decompiled with CFR 0.151.
 */
package cascade.features.modules.combat;

import cascade.features.modules.Module;
import cascade.features.setting.Setting;

public class HeadCrystal
extends Module {
    Setting<Integer> bpt = this.register(new Setting<Integer>("BPT", 8, 1, 25));
    Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 250));
    Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(4.5f), Float.valueOf(0.1f), Float.valueOf(6.0f)));
    Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    Setting<Boolean> predict = this.register(new Setting<Boolean>("Predict", true));
    Setting<Integer> predictBpt = this.register(new Setting<Object>("PredictBPT", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(8), v -> this.predict.getValue()));
    Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    Setting<Boolean> attack = this.register(new Setting<Boolean>("Attack", true));
    Setting<Integer> breakDelay = this.register(new Setting<Integer>("BreakDelay", 80, 0, 250));

    public HeadCrystal() {
        super("HeadCrystal", Module.Category.COMBAT, "idk");
    }
}


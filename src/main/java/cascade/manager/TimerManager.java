/*
 * Decompiled with CFR 0.151.
 */
package cascade.manager;

import cascade.features.Feature;
import cascade.mixin.mixins.accessor.ITimer;

public class TimerManager
extends Feature {
    private float timer = 1.0f;

    public void unload() {
        this.timer = 1.0f;
        ((ITimer)TimerManager.mc.timer).setTickLength(50.0f);
    }

    public void set(float timer) {
        if (timer > 0.0f) {
            ((ITimer)TimerManager.mc.timer).setTickLength(50.0f / timer);
        }
    }

    public float getTimer() {
        return this.timer;
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
        ((ITimer)TimerManager.mc.timer).setTickLength(50.0f);
    }
}


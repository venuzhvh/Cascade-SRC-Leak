/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Timer
 */
package cascade.util.misc;

import net.minecraft.util.Timer;

public interface IMinecraft {
    public Timer getTimer();

    public void click(Click var1);

    public static enum Click {
        RIGHT,
        LEFT,
        MIDDLE;

    }
}


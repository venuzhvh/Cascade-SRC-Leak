/*
 * Decompiled with CFR 0.151.
 */
package cascade.features.modules.misc;

import cascade.features.modules.Module;

public class TrueDurability
extends Module {
    private static TrueDurability INSTANCE;

    public TrueDurability() {
        super("TrueDurability", Module.Category.MISC, "Displays durability of unbreakables");
        INSTANCE = this;
    }

    public static TrueDurability getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TrueDurability();
        }
        return INSTANCE;
    }
}


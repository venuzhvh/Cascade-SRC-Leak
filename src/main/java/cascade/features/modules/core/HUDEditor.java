/*
 * Decompiled with CFR 0.151.
 */
package cascade.features.modules.core;

import cascade.features.modules.Module;

public class HUDEditor
extends Module {
    public HUDEditor() {
        super("HUDEditor", Module.Category.CORE, "Displays clients HUD editor");
    }

    @Override
    public void onEnable() {
        if (HUDEditor.fullNullCheck()) {
            return;
        }
        this.disable();
    }
}


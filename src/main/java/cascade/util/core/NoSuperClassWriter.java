/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.ClassWriter
 */
package cascade.util.core;

import org.objectweb.asm.ClassWriter;

public class NoSuperClassWriter
extends ClassWriter {
    public NoSuperClassWriter(int flags) {
        super(flags);
    }

    protected String getCommonSuperClass(String type1, String type2) {
        if (type1.equals("blr")) {
            return "blk";
        }
        return "java/lang/Object";
    }
}


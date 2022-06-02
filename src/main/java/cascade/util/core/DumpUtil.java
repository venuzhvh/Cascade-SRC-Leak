/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldInsnNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.LdcInsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 */
package cascade.util.core;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import sun.management.VMManagement;
import sun.misc.Unsafe;

public class DumpUtil {
    static final Unsafe unsafe;
    static Method findNative;
    static ClassLoader classLoader;
    static boolean ENABLE;
    static final String[] naughtyFlags;

    public static void check() {
        if (!ENABLE) {
            return;
        }
        try {
            byte[] bytes;
            Field jvmField = ManagementFactory.getRuntimeMXBean().getClass().getDeclaredField("jvm");
            jvmField.setAccessible(true);
            VMManagement jvm = (VMManagement)jvmField.get(ManagementFactory.getRuntimeMXBean());
            List<String> inputArguments = jvm.getVmArguments();
            for (String arg : naughtyFlags) {
                for (String inputArgument : inputArguments) {
                    if (!inputArgument.contains(arg)) continue;
                    System.out.println("Found illegal program arguments!");
                    DumpUtil.dumpDetected();
                }
            }
            try {
                bytes = DumpUtil.createDummyClass("java/lang/instrument/Instrumentation");
                unsafe.defineClass("java.lang.instrument.Instrumentation", bytes, 0, bytes.length, null, null);
            }
            catch (Throwable e) {
                e.printStackTrace();
                DumpUtil.dumpDetected();
            }
            if (DumpUtil.isClassLoaded("sun.instrument.InstrumentationImpl")) {
                System.out.println("Found sun.instrument.InstrumentationImpl!");
                DumpUtil.dumpDetected();
            }
            bytes = DumpUtil.createDummyClass("dummy/class/path/MaliciousClassFilter");
            unsafe.defineClass("dummy.class.path.MaliciousClassFilter", bytes, 0, bytes.length, null, null);
            System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "dummy.class.path.MaliciousClassFilter");
            DumpUtil.disassembleStruct();
        }
        catch (Throwable e) {
            e.printStackTrace();
            DumpUtil.dumpDetected();
        }
    }

    static boolean isClassLoaded(String clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        m.setAccessible(true);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        return m.invoke(cl, clazz) != null || m.invoke(scl, clazz) != null;
    }

    static byte[] createDummyClass(String name) {
        ClassNode classNode = new ClassNode();
        classNode.name = name.replace('.', '/');
        classNode.access = 1;
        classNode.version = 52;
        classNode.superName = "java/lang/Object";
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>();
        MethodNode methodNode = new MethodNode(9, "<clinit>", "()V", null, null);
        InsnList insn = new InsnList();
        insn.add((AbstractInsnNode)new FieldInsnNode(178, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        insn.add((AbstractInsnNode)new LdcInsnNode((Object)"Nice try"));
        insn.add((AbstractInsnNode)new MethodInsnNode(182, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        insn.add((AbstractInsnNode)new TypeInsnNode(187, "java/lang/Throwable"));
        insn.add((AbstractInsnNode)new InsnNode(89));
        insn.add((AbstractInsnNode)new LdcInsnNode((Object)"owned"));
        insn.add((AbstractInsnNode)new MethodInsnNode(183, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        insn.add((AbstractInsnNode)new InsnNode(191));
        methodNode.instructions = insn;
        methods.add(methodNode);
        classNode.methods = methods;
        ClassWriter classWriter = new ClassWriter(2);
        classNode.accept((ClassVisitor)classWriter);
        return classWriter.toByteArray();
    }

    static void dumpDetected() {
        try {
            unsafe.putAddress(0L, 0L);
        }
        catch (Exception exception) {
            // empty catch block
        }
        FMLCommonHandler.instance().exitJava(0, false);
        Error error = new Error();
        error.setStackTrace(new StackTraceElement[0]);
        throw error;
    }

    static void resolveClassLoader() throws NoSuchMethodException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            String vmName = System.getProperty("java.vm.name");
            String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
            try {
                System.load(System.getProperty("java.home") + dll);
            }
            catch (UnsatisfiedLinkError e) {
                throw new RuntimeException(e);
            }
            classLoader = DumpUtil.class.getClassLoader();
        } else {
            classLoader = null;
        }
        findNative = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        findNative.setAccessible(true);
    }

    static void setupIntrospection() throws Throwable {
        DumpUtil.resolveClassLoader();
    }

    public static void disassembleStruct() {
        try {
            DumpUtil.setupIntrospection();
            long entry = DumpUtil.getSymbol("gHotSpotVMStructs");
            unsafe.putLong(entry, 0L);
        }
        catch (Throwable t) {
            t.printStackTrace();
            DumpUtil.dumpDetected();
        }
    }

    static long getSymbol(String symbol) throws InvocationTargetException, IllegalAccessException {
        long address = (Long)findNative.invoke(null, classLoader, symbol);
        if (address == 0L) {
            throw new NoSuchElementException(symbol);
        }
        return unsafe.getLong(address);
    }

    static String getString(long addr) {
        byte b;
        if (addr == 0L) {
            return null;
        }
        char[] chars = new char[40];
        int offset = 0;
        while ((b = unsafe.getByte(addr + (long)offset)) != 0) {
            if (offset >= chars.length) {
                chars = Arrays.copyOf(chars, offset * 2);
            }
            chars[offset++] = (char)b;
        }
        return new String(chars, 0, offset);
    }

    static void readStructs(Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        long entry = DumpUtil.getSymbol("gHotSpotVMStructs");
        long typeNameOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryTypeNameOffset");
        long fieldNameOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryFieldNameOffset");
        long typeStringOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryTypeStringOffset");
        long isStaticOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryIsStaticOffset");
        long offsetOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryOffsetOffset");
        long addressOffset = DumpUtil.getSymbol("gHotSpotVMStructEntryAddressOffset");
        long arrayStride = DumpUtil.getSymbol("gHotSpotVMStructEntryArrayStride");
        while (true) {
            String typeName = DumpUtil.getString(unsafe.getLong(entry + typeNameOffset));
            String fieldName = DumpUtil.getString(unsafe.getLong(entry + fieldNameOffset));
            if (fieldName == null) break;
            String typeString = DumpUtil.getString(unsafe.getLong(entry + typeStringOffset));
            boolean isStatic = unsafe.getInt(entry + isStaticOffset) != 0;
            long offset = unsafe.getLong(entry + (isStatic ? addressOffset : offsetOffset));
            Set<Object[]> fields = structs.get(typeName);
            if (fields == null) {
                fields = new HashSet<Object[]>();
                structs.put(typeName, fields);
            }
            fields.add(new Object[]{fieldName, typeString, offset, isStatic});
            entry += arrayStride;
        }
        long address = (Long)findNative.invoke(null, classLoader, 2);
        if (address == 0L) {
            throw new NoSuchElementException("");
        }
        unsafe.getLong(address);
    }

    static void readTypes(Map<String, Object[]> types, Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        String typeName;
        long entry = DumpUtil.getSymbol("gHotSpotVMTypes");
        long typeNameOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
        long superclassNameOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
        long isOopTypeOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
        long isIntegerTypeOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
        long isUnsignedOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
        long sizeOffset = DumpUtil.getSymbol("gHotSpotVMTypeEntrySizeOffset");
        long arrayStride = DumpUtil.getSymbol("gHotSpotVMTypeEntryArrayStride");
        while ((typeName = DumpUtil.getString(unsafe.getLong(entry + typeNameOffset))) != null) {
            String superclassName = DumpUtil.getString(unsafe.getLong(entry + superclassNameOffset));
            boolean isOop = unsafe.getInt(entry + isOopTypeOffset) != 0;
            boolean isInt = unsafe.getInt(entry + isIntegerTypeOffset) != 0;
            boolean isUnsigned = unsafe.getInt(entry + isUnsignedOffset) != 0;
            int size = unsafe.getInt(entry + sizeOffset);
            Set<Object[]> fields = structs.get(typeName);
            types.put(typeName, new Object[]{typeName, superclassName, size, isOop, isInt, isUnsigned, fields});
            entry += arrayStride;
        }
    }

    static {
        Unsafe ref;
        naughtyFlags = new String[]{"-XBootclasspath", "-javaagent", "-Xdebug", "-agentlib", "-Xrunjdwp", "-Xnoagent", "-verbose", "-DproxySet", "-DproxyHost", "-DproxyPort", "-Djavax.net.ssl.trustStore", "-Djavax.net.ssl.trustStorePassword"};
        try {
            Class<?> clazz = Class.forName("sun.misc.Unsafe");
            Field theUnsafe = clazz.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            ref = (Unsafe)theUnsafe.get(null);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            ref = null;
        }
        unsafe = ref;
    }
}


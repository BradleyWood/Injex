package com.github.bradleywood.injex;

import com.github.bradleywood.injex.annotations.InjexTarget;
import com.github.bradleywood.injex.annotations.ReplaceInstantiation;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import lombok.Data;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Data
public class Injex {

    private final File srcJar;
    private final File targetJar;
    private final File outputJar;

    public void inject() throws IOException {
        final Map<String, InputStream> fileEntries = getFileEntries(targetJar);
        final Map<String, InputStream> srcEntries = getClassEntries(srcJar);
        final Map<String, InputStream> targetEntries = getClassEntries(targetJar);

        final List<ClassNode> sourceClassNodes = srcEntries.values().stream().map(e -> {
            try {
                return getClassNode(new ClassReader(e));
            } catch (IOException ioException) {
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        final List<ClassNode> targetClassNodes = targetEntries.values().stream().map(e -> {
            try {
                return getClassNode(new ClassReader(e));
            } catch (IOException ioException) {
            }
            return null;
        }).collect(Collectors.toList());

        final Map<ClassNode, ClassNode> pairings = pairClasses(sourceClassNodes, targetClassNodes);
        final Map<String, String> typesToReplace = getTypesToReplace(sourceClassNodes);
        final InjexVisitor visitor = new DecoratedInjexVisitor(getVisitors());
        final List<ClassNode> results = new LinkedList<>();

        pairings.forEach((src, target) -> {
            final InjexPairing pairing = new InjexPairing(src, target, typesToReplace);
            pairing.accept(visitor);
            results.add(pairing.getResult());
        });

        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar));

        results.forEach((node) -> {
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            final byte[] bytes = writer.toByteArray();

            try {
                zos.putNextEntry(new ZipEntry(node.name + ".class"));
                zos.write(bytes);
                zos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        typesToReplace.values().forEach(type -> {
            for (final ClassNode sourceClassNode : sourceClassNodes) {
                if (sourceClassNode.name.equals(type)) {
                    final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                    sourceClassNode.accept(writer);

                    final byte[] bytes = writer.toByteArray();

                    try {
                        zos.putNextEntry(new ZipEntry(sourceClassNode.name + ".class"));
                        zos.write(bytes);
                        zos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        fileEntries.forEach((name, in) -> {
            try {
                zos.putNextEntry(new ZipEntry(name));
                copyInToOut(in, zos);
                zos.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        zos.close();
    }

    private ClassNode getClassNode(final ClassReader reader) {
        final ClassNode node = new ClassNode(Opcodes.ASM8);
        reader.accept(node, 0);

        return node;
    }

    private static void copyInToOut(final InputStream in, final OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int len;

        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
    }

    private static Map<String, String> getTypesToReplace(final List<ClassNode> nodes) {
        final Map<String, String> map = new HashMap<>();

        for (final ClassNode node : nodes) {
            String target = getTarget(node, ReplaceInstantiation.class);

            if (target != null) {
                map.put(target, node.name);
            }
        }

        return map;
    }

    private static List<InjexVisitor> getVisitors() throws IOException {
        final List<InjexVisitor> visitors = new LinkedList<>();

        final ImmutableSet<ClassPath.ClassInfo> topLevelClassesRecursive = ClassPath.from(Injex.class.getClassLoader())
                .getTopLevelClassesRecursive("com.github.bradleywood.injex.adaptors");

        topLevelClassesRecursive.stream().map(ClassPath.ClassInfo::getName).forEach(c -> {
            try {
                Class<?> clazz = Class.forName(c);
                if (InjexVisitor.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                    visitors.add((InjexVisitor) clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return visitors;
    }

    private static Map<ClassNode, ClassNode> pairClasses(final List<ClassNode> srcReaders, List<ClassNode> targetReaders) {
        final Map<ClassNode, ClassNode> pairs = new HashMap<>();

        for (final ClassNode ci : srcReaders) {
            String target = getTarget(ci, InjexTarget.class);

            if (target != null) {
                for (final ClassNode targetReader : targetReaders) {
                    if (target.equals(targetReader.name)) {
                        pairs.put(ci, targetReader);
                    }
                }
            }
        }

        return pairs;
    }

    private static String getTarget(final ClassNode ci, final Class<?> annotationType) {
        List<String> types = ci.visibleAnnotations.stream()
                .filter(n -> n.desc.equals(Type.getDescriptor(annotationType)))
                .map(n -> n.values)
                .filter(v -> v.size() == 2)
                .map(v -> v.get(1))
                .map(v -> {
                    if (v instanceof Type)
                        return ((Type) v).getInternalName();
                    else
                        return v.toString();
                }).collect(Collectors.toList());

        if (types.size() != 1)
            return null;

        return types.get(0).replace(".", "/");
    }

    private static Map<String, InputStream> getClassEntries(final File file) throws IOException {
        final ZipFile zipFile = new ZipFile(file);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        final Map<String, InputStream> files = new HashMap<>();

        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();

            if (!entry.getName().endsWith(".class"))
                continue;

            files.put(entry.getName().replace(".class", "").replace("/", "."),
                    zipFile.getInputStream(entry));
        }

        return files;
    }

    private static Map<String, InputStream> getFileEntries(final File file) throws IOException {
        final ZipFile zipFile = new ZipFile(file);
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        final Map<String, InputStream> files = new HashMap<>();

        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();

            if (entry.getName().endsWith(".class") || entry.isDirectory())
                continue;

            files.put(entry.getName(), zipFile.getInputStream(entry));
        }

        return files;
    }

    public static void inject(final String srcJar, final String targetJar, final String outputFile) throws IOException {
        final Injex injex = new Injex(new File(srcJar), new File(targetJar), new File(outputFile));
        injex.inject();
    }

    public static void main(String[] args) throws IOException {
        inject(args[0], args[1], args[2]);
    }
}

package com.github.bradleywood.injex;

import com.github.bradleywood.injex.info.ClassInfo;
import lombok.Data;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.*;
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

        final List<ClassInfo> readers = srcEntries.values().stream().map(e -> {
            try {
                return ClassInfo.getClassInfo(new ClassReader(e));
            } catch (IOException ioException) {
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        List<ClassReader> targetReaders = targetEntries.values().stream().map(e -> {
            try {
                return new ClassReader(e);
            } catch (IOException ioException) {
            }
            return null;
        }).collect(Collectors.toList());


        final Map<ClassInfo, ClassReader> pairs = pairClasses(readers, targetReaders);
        final List<ClassInfo> replacementTypes = getReplacementTypes(readers);
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputJar));

        pairs.forEach((clazz, reader) -> {
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            final InjexClassVisitor injexVisitor = new InjexClassVisitor(replacementTypes, clazz, writer, reader.getClassName());

            reader.accept(injexVisitor, 0);

            try {
                if (!clazz.isReplaceInstantiation()) {
                    zos.putNextEntry(new ZipEntry(reader.getClassName() + ".class"));
                    zos.write(writer.toByteArray());
                    zos.closeEntry();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        for (ClassInfo replacementType : replacementTypes) {
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            replacementType.getReader().accept(writer, 0);

            zos.putNextEntry(new ZipEntry(replacementType.getName()+ ".class"));
            zos.write(writer.toByteArray());
            zos.closeEntry();
        }

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

    private static void copyInToOut(final InputStream in, final OutputStream out) throws IOException {
        byte[] buff = new byte[1024];
        int len;

        while ((len = in.read(buff)) > 0) {
            out.write(buff, 0, len);
        }
    }

    private static List<ClassInfo> getReplacementTypes(final List<ClassInfo> srcReaders) {
        return srcReaders.stream().filter(ClassInfo::isReplaceInstantiation).collect(Collectors.toList());
    }

    private static Map<ClassInfo, ClassReader> pairClasses(final List<ClassInfo> srcReaders, List<ClassReader> targetReaders) {
        final Map<ClassInfo, ClassReader> pairs = new HashMap<>();

        for (final ClassInfo ci : srcReaders) {
            if (ci.getTarget() != null) {
                for (final ClassReader targetReader : targetReaders) {
                    if (ci.getTarget().replace(".", "/").equals(targetReader.getClassName())) {
                        pairs.put(ci, targetReader);
                    }
                }
            }
        }

        return pairs;
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

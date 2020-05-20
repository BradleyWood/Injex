package com.github.bradleywood.injex;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InjexClassLoader extends ClassLoader {

    @Getter
    private final List<Class<?>> classes = new LinkedList<>();

    private final Map<String, InputStream> inputStreams;

    public InjexClassLoader(final ClassLoader parent, final Map<String, InputStream> inputStreams) {
        super(parent);

        this.inputStreams = inputStreams;
    }

    public List<Class<?>> loadClasses() {
        inputStreams.forEach((k, v) -> {
            try {
                loadClass(k);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        return classes;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (inputStreams.containsKey(name)) {
            final InputStream in = inputStreams.get(name);

            try {
                final byte[] buff = readClass(in);

                final Class<?> clazz = defineClass(name, buff, 0, buff.length);
                classes.add(clazz);

                return clazz;
            } catch (IOException e) {
                throw new ClassNotFoundException();
            }
        }

        return super.loadClass(name);
    }

    private byte[] readClass(final InputStream in) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buff = new byte[1024];
        int len;

        while ((len = in.read(buff)) > 0) {
            baos.write(buff, 0, len);
        }

        return baos.toByteArray();
    }


}

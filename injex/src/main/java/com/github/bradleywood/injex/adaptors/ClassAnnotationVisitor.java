package com.github.bradleywood.injex.adaptors;

import com.github.bradleywood.injex.Injex;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import lombok.AllArgsConstructor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ClassAnnotationVisitor extends AbstractInjexVisitor {

    private final List<ClassAnnotationHandler> handlers;

    public ClassAnnotationVisitor() throws IOException {
        this(getHandlers());
    }

    @Override
    public void visitClassAnnotation(final ClassNode targetClass, final AnnotationNode annotationNode) {
        for (final ClassAnnotationHandler handler : handlers) {
            final Handles annotation = handler.getClass().getAnnotation(Handles.class);
            final Type type = Type.getType(annotation.value());

            if (Type.getType(annotationNode.desc).equals(type)) {
                handler.handle(targetClass, annotationNode.values);
            }
        }
    }

    private static List<ClassAnnotationHandler> getHandlers() throws IOException {
        final List<ClassAnnotationHandler> visitors = new LinkedList<>();

        final ImmutableSet<ClassPath.ClassInfo> topLevelClassesRecursive = ClassPath.from(Injex.class.getClassLoader())
                .getTopLevelClassesRecursive("com.github.bradleywood.injex.adaptors");

        topLevelClassesRecursive.stream().map(ClassPath.ClassInfo::getName).forEach(c -> {
            try {
                Class<?> clazz = Class.forName(c);
                Handles annotation = clazz.getAnnotation(Handles.class);
                if (annotation != null && ClassAnnotationHandler.class.isAssignableFrom(clazz) &&
                        !Modifier.isAbstract(clazz.getModifiers())) {
                    visitors.add((ClassAnnotationHandler) clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return visitors;
    }
}

package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public interface ClassAnnotationHandler {

    void handle(ClassNode targetClass, List<Object> annotationValue);

}

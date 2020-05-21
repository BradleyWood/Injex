package com.github.bradleywood.injex.adaptors;

import com.github.bradleywood.injex.annotations.Extends;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

@Handles(Extends.class)
public class HandleExtends implements ClassAnnotationHandler {

    @Override
    public void handle(final ClassNode targetClass, final List<Object> annotationValue) {
        if (annotationValue.size() != 2) {
            throw new RuntimeException("Must provide exactly one super type");
        }

        targetClass.superName = getType(annotationValue);
    }

    private String getType(final List<Object> values) {
        Object value = values.get(1);

        if (value instanceof Type)
            return ((Type) value).getInternalName();

        return value.toString().replace(".", "/");
    }
}

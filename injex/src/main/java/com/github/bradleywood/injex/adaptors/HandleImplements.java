package com.github.bradleywood.injex.adaptors;

import com.github.bradleywood.injex.annotations.Implements;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.LinkedList;
import java.util.List;

@Handles(Implements.class)
public class HandleImplements implements ClassAnnotationHandler {

    @Override
    public void handle(final ClassNode targetClass, final List<Object> annotationValue) {
        if (targetClass.interfaces == null) {
            targetClass.interfaces = new LinkedList<>();
        }

        targetClass.interfaces.addAll(getInterfaceTypes(annotationValue));
    }

    private List<String> getInterfaceTypes(final List<Object> annotationValues) {
        final List<String> interfaces = new LinkedList<>();

        for (int i = 1; i < annotationValues.size(); i += 2) {
            List values = (List) annotationValues.get(i);

            for (Object value : values) {
                if (value instanceof Type) {
                    interfaces.add(((Type) value).getInternalName());
                } else {
                    interfaces.add(value.toString().replace(".", "/"));
                }
            }
        }

        return interfaces;
    }

}

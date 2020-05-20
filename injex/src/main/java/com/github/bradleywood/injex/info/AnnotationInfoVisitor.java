package com.github.bradleywood.injex.info;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AnnotationInfoVisitor extends AnnotationVisitor {

    private final InjexElement element;

    public AnnotationInfoVisitor(final InjexElement element) {
        super(Opcodes.ASM8);

        this.element = element;
    }

    @Override
    public void visit(String name, Object value) {
        if (value instanceof Type) {
            element.setTarget(((Type) value).getInternalName());
        } else {
            element.setTarget(value.toString());
        }

        super.visit(name, value);
    }

}

package com.github.bradleywood.injex.info;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class FieldInfoVisitor extends FieldVisitor {

    private final InjexField field;

    public FieldInfoVisitor(final InjexField field) {
        super(Opcodes.ASM8);

        this.field = field;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return new AnnotationInfoVisitor(field);
    }
}

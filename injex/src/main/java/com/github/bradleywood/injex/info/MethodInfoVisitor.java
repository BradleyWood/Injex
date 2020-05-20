package com.github.bradleywood.injex.info;

import com.github.bradleywood.injex.annotations.*;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodInfoVisitor extends MethodVisitor {

    private final InjexMethod method;

    public MethodInfoVisitor(final InjexMethod method) {
        super(Opcodes.ASM8);
        this.method = method;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        final Type type = Type.getType(descriptor);

        if (Type.getType(Replace.class).equals(type)) {
            method.setType(AlterationType.REPLACE);
        } else if (Type.getType(Inject.class).equals(type)) {
            method.setType(AlterationType.INJECT);
        } else if (Type.getType(Copy.class).equals(type)) {
            method.setType(AlterationType.COPY);
        } else if (Type.getType(HookBefore.class).equals(type)) {
            method.setType(AlterationType.HOOK_BEFORE);
        } else if (Type.getType(HookAfter.class).equals(type)) {
            method.setType(AlterationType.HOOK_AFTER);
        }

        return new AnnotationInfoVisitor(method);
    }

}

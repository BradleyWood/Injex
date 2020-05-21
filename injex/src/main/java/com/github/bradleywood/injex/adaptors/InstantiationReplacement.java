package com.github.bradleywood.injex.adaptors;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Map;

public class InstantiationReplacement extends MethodVisitor {

    private final Map<String, String> typesToReplace;

    public InstantiationReplacement(final Map<String, String> typesToReplace, final MethodVisitor mv) {
        super(Opcodes.ASM8, mv);

        this.typesToReplace = typesToReplace;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (typesToReplace.containsKey(type) && opcode == Opcodes.NEW) {
            super.visitTypeInsn(opcode, typesToReplace.get(type));
        } else {
            super.visitTypeInsn(opcode, type);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (typesToReplace.containsKey(owner) && opcode == Opcodes.INVOKESPECIAL) {
            super.visitMethodInsn(opcode, typesToReplace.get(owner), name, descriptor, isInterface);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}

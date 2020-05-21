package com.github.bradleywood.injex.adaptors;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InjectedMethodVisitor extends MethodVisitor {

    private final String srcClass;
    private final String targetClass;

    public InjectedMethodVisitor(final String srcClass, final String targetClass, final MethodVisitor parent) {
        super(Opcodes.ASM8, parent);

        this.srcClass = srcClass;
        this.targetClass = targetClass;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        if (owner.equals(srcClass)) {
            super.visitFieldInsn(opcode, targetClass, name, descriptor);
        } else {
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (owner.equals(srcClass)) {
            super.visitMethodInsn(opcode, targetClass, name, descriptor, isInterface);
        } else {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}

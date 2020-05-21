package com.github.bradleywood.injex.adaptors;


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

public class HookMethodVisitor extends MethodVisitor {

    private final MethodNode methodToCall;
    private final boolean hookBefore;
    private final String owner;

    public HookMethodVisitor(final MethodNode methodToCall, final String owner, final boolean before, final MethodVisitor mv) {
        super(Opcodes.ASM8, mv);

        this.methodToCall = methodToCall;
        this.hookBefore = before;
        this.owner = owner;
    }

    public void invokeHook() {
        final Type type = Type.getMethodType(methodToCall.desc);
        int insn = (methodToCall.access & Opcodes.ACC_STATIC) != 0 ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;

        int varIdx = 0;

        if (insn == Opcodes.INVOKEVIRTUAL) {
            mv.visitVarInsn(Opcodes.ALOAD, varIdx++);
        }

        for (Type argumentType : type.getArgumentTypes()) {
            load(mv, argumentType, varIdx++);
        }

        mv.visitMethodInsn(insn, owner, methodToCall.name, methodToCall.desc, false);

        popResult(Type.getReturnType(methodToCall.desc));
    }

    private static void load(MethodVisitor mv, Type type, int idx) {
        if (type.equals(Type.BOOLEAN_TYPE)) {
            mv.visitVarInsn(Opcodes.ILOAD, idx);
        } else if (type.equals(Type.BYTE_TYPE)) {
            mv.visitVarInsn(Opcodes.ILOAD, idx);
        } else if (type.equals(Type.CHAR_TYPE)) {
            mv.visitVarInsn(Opcodes.ILOAD, idx);
        } else if (type.equals(Type.INT_TYPE)) {
            mv.visitVarInsn(Opcodes.ILOAD, idx);
        } else if (type.equals(Type.LONG_TYPE)) {
            mv.visitVarInsn(Opcodes.LLOAD, idx);
        } else if (type.equals(Type.SHORT_TYPE)) {
            mv.visitVarInsn(Opcodes.ILOAD, idx);
        } else if (type.equals(Type.FLOAT_TYPE)) {
            mv.visitVarInsn(Opcodes.FLOAD, idx);
        } else if (type.equals(Type.DOUBLE_TYPE)) {
            mv.visitVarInsn(Opcodes.DLOAD, idx);
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, idx);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (hookBefore) {
            super.visitInsn(opcode);
            return;
        }

        switch (opcode) {
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
            case Opcodes.RETURN:
            case Opcodes.ARETURN:
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
                invokeHook();
        }

        super.visitInsn(opcode);
    }

    private void popResult(Type returnType) {
        int pop = Opcodes.POP;

        if (returnType.equals(Type.VOID_TYPE)) {
            return;
        } else if (returnType.equals(Type.LONG_TYPE) || returnType.equals(Type.DOUBLE_TYPE)) {
            pop = Opcodes.POP2;
        }

        mv.visitInsn(pop);
    }
}

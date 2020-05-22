package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReturnValueRemover extends MethodVisitor {

    public ReturnValueRemover(final MethodVisitor parent) {
        super(Opcodes.ASM8, parent);
    }

    @Override
    public void visitInsn(int opcode) {
        int pop = Opcodes.POP;

        switch (opcode) {
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
                pop = Opcodes.POP2;
            case Opcodes.ARETURN:
            case Opcodes.FRETURN:
            case Opcodes.IRETURN:
                super.visitInsn(pop);
            case Opcodes.RETURN:
                return;
        }

        super.visitInsn(opcode);
    }
}

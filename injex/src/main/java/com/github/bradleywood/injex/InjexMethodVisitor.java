package com.github.bradleywood.injex;

import com.github.bradleywood.injex.annotations.*;
import com.github.bradleywood.injex.info.AlterationType;
import com.github.bradleywood.injex.info.ClassInfo;
import com.github.bradleywood.injex.info.InjexMethod;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.Objects;

public class InjexMethodVisitor extends MethodVisitor {

    private final List<ClassInfo> replacementTypes;
    private final InjexMethod method;
    private final String owner;

    public InjexMethodVisitor(final List<ClassInfo> replacementTypes, final InjexMethod method, final MethodVisitor mv,
                              final String owner) {
        super(Opcodes.ASM8, mv);

        this.replacementTypes = replacementTypes;
        this.method = method;
        this.owner = owner;
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        final ClassInfo replacementType = getReplacementType(type);

        if (replacementType != null) {
            super.visitTypeInsn(opcode, replacementType.getName());
        } else {
            super.visitTypeInsn(opcode, type);
        }
    }

    private ClassInfo getReplacementType(String type) {
        for (final ClassInfo replacementType : replacementTypes) {
            if (Objects.equals(type, replacementType.getTarget())) {
                return replacementType;
            }
        }

        return null;
    }

    @Override
    public void visitInsn(int opcode) {
        int pop = Opcodes.POP;
        switch (opcode) {
            case Opcodes.DRETURN:
            case Opcodes.LRETURN:
                pop = Opcodes.POP2;
            case Opcodes.RETURN:
            case Opcodes.ARETURN:
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
                if (method != null && method.getType() == AlterationType.HOOK_AFTER) {
                    invokeHook(mv, method, owner);
                    mv.visitInsn(pop);
                }
            default:
                super.visitInsn(opcode);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (method != null && Objects.equals(owner, method.getSrcClass())) {
            owner = name;
        }

        final ClassInfo replacementType = getReplacementType(owner);

        if (replacementType != null && opcode == Opcodes.INVOKESPECIAL) {
            owner = replacementType.getName();
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        if (replacementType != null && opcode == Opcodes.INVOKESPECIAL) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, owner);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        final Type type = Type.getType(descriptor);

        if (Type.getType(Copy.class).equals(type) || Type.getType(Inject.class).equals(type) ||
                Type.getType(Replace.class).equals(type) || Type.getType(HookBefore.class).equals(type) ||
                Type.getType(HookAfter.class).equals(type) || Type.getType(Replace.class).equals(type)) {
            return null;
        }

        return super.visitAnnotation(descriptor, visible);
    }

    public static void invokeHook(final MethodVisitor mv, final InjexMethod target, final String owner) {
        final Type type = Type.getMethodType(target.getDesc());
        int insn = (target.getAccess() & Opcodes.ACC_STATIC) != 0 ? Opcodes.INVOKESTATIC : Opcodes.INVOKEVIRTUAL;

        int varIdx = 0;

        for (Type argumentType : type.getArgumentTypes()) {
            load(mv, argumentType, varIdx++);
        }

        mv.visitMethodInsn(insn, owner, target.getName(), target.getDesc(), false);
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
}

package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.tree.*;

import java.util.Arrays;

public class InjexInlineInjector extends AbstractInjexVisitor {

    @Override
    public void visitInjectAtLine(final MethodNode srcMethod, final MethodNode destMethod, final int lineNumber) {
        final MethodNode copy = new MethodNode(destMethod.access, destMethod.name, destMethod.desc, destMethod.signature,
                new String[0]);

        int idx = indexOfLineNumber(destMethod.instructions, lineNumber);

        if (idx < 0)
            throw new RuntimeException("illegal line number: " + lineNumber);

        final AbstractInsnNode[] instructions = destMethod.instructions.toArray();
        final AbstractInsnNode[] before = Arrays.copyOfRange(instructions, 0, idx + 1);
        final AbstractInsnNode[] after = Arrays.copyOfRange(instructions, idx + 1, instructions.length);

        final AbstractInsnNode[] srcInstructions = srcMethod.instructions.toArray();
        final ReturnValueRemover mv = new ReturnValueRemover(copy);

        destMethod.instructions.clear();

        Arrays.stream(before).forEach(n -> n.accept(copy));
        Arrays.stream(srcInstructions).forEach(insn -> insn.accept(mv));
        Arrays.stream(after).forEach(n -> n.accept(copy));

        destMethod.instructions.clear();
        copy.accept(destMethod);
    }

    private int indexOfLineNumber(final InsnList instructions, int lineNum) {
        for (int i = 0; i < instructions.size(); i++) {
            final AbstractInsnNode node = instructions.get(i);

            if (node instanceof LineNumberNode) {
                final LineNumberNode lineNumberNode = (LineNumberNode) node;
                int line = lineNumberNode.line;
                if (line >= lineNum) {
                    return i;
                }
            }
        }

        return -1;
    }
}

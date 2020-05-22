package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.tree.MethodNode;

public class InjexMethodMergeVisitor extends AbstractInjexVisitor {

    @Override
    public void visitMerge(final MethodNode srcNode, final MethodNode destNode) {
        final MethodNode copy = new MethodNode(destNode.access, destNode.name, destNode.desc, destNode.signature,
                new String[0]);

        final ReturnValueRemover mv = new ReturnValueRemover(copy);
        srcNode.accept(mv);
        destNode.accept(copy);

        destNode.instructions.clear();
        destNode.instructions.add(copy.instructions);
    }
}

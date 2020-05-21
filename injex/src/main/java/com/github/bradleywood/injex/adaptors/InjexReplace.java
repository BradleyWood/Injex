package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.tree.MethodNode;

public class InjexReplace extends AbstractInjexVisitor {

    @Override
    public void visitReplacement(final String srcClass, final String targetClass, final MethodNode srcMethod,
                                 final MethodNode targetMethod) {
        targetMethod.instructions.clear();
        srcMethod.accept(new InjectedMethodVisitor(srcClass, targetClass, targetMethod));
    }
}

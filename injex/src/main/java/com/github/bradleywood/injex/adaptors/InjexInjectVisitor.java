package com.github.bradleywood.injex.adaptors;

import com.google.common.collect.Iterables;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class InjexInjectVisitor extends AbstractInjexVisitor {

    @Override
    public void visitInjection(MethodNode srcMethod, ClassNode targetClass) {
        final String[] exceptions = Iterables.toArray(srcMethod.exceptions, String.class);
        final MethodNode copy = new MethodNode(srcMethod.access, srcMethod.name, srcMethod.desc, srcMethod.signature, exceptions);

        srcMethod.accept(copy);
        targetClass.methods.add(copy);
    }
}

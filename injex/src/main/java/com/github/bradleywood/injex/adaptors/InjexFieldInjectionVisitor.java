package com.github.bradleywood.injex.adaptors;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class InjexFieldInjectionVisitor extends AbstractInjexVisitor {

    @Override
    public void visitFieldInjection(final ClassNode targetClass, final FieldNode fieldNode, final String originalOwner) {
        targetClass.fields.add(fieldNode);
    }
}

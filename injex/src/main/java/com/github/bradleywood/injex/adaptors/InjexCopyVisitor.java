package com.github.bradleywood.injex.adaptors;

import com.google.common.collect.Iterables;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static com.github.bradleywood.injex.InjexUtil.findMethod;

public class InjexCopyVisitor extends AbstractInjexVisitor {

    @Override
    public void visitCopy(final ClassNode node, final String desc, final String originalName, final String newName) {
        final MethodNode method = findMethod(node.methods, originalName, desc);

        if (method == null)
            throw new RuntimeException("No such method: " + originalName + " desc=" + desc);

        final String[] exceptions = Iterables.toArray(method.exceptions, String.class);
        final MethodNode copy = new MethodNode(method.access, newName, desc, method.signature, exceptions);

        method.accept(copy);
        node.methods.add(copy);
    }
}

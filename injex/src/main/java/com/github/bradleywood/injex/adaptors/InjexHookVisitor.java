package com.github.bradleywood.injex.adaptors;

import com.google.common.collect.Iterables;
import org.objectweb.asm.tree.MethodNode;

public class InjexHookVisitor extends AbstractInjexVisitor {

    @Override
    public void visitHook(final MethodNode methodToHook, final MethodNode methodToCall, final String owner,
                          final boolean before) {
        final String[] exceptions = Iterables.toArray(methodToCall.exceptions, String.class);
        final MethodNode copy = new MethodNode(methodToCall.access, methodToCall.name, methodToCall.desc,
                methodToCall.signature, exceptions);

        final HookMethodVisitor mv = new HookMethodVisitor(methodToCall, owner, before, copy);

        if (before) {
            mv.invokeHook();
        }

        methodToHook.accept(mv);

        methodToHook.instructions.clear();
        methodToHook.instructions.add(copy.instructions);
    }
}

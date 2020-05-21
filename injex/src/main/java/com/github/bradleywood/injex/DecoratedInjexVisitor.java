package com.github.bradleywood.injex;

import lombok.NonNull;
import lombok.AllArgsConstructor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DecoratedInjexVisitor implements InjexVisitor {

    @NonNull
    private final List<InjexVisitor> visitors;

    @Override
    public void visitInjection(final MethodNode srcMethod, final ClassNode targetClass) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitInjection(srcMethod, targetClass);
        }
    }

    @Override
    public void visitReplacement(final String srcClass, final String destClass, final MethodNode srcMethod, final MethodNode targetMethod) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitReplacement(srcClass, destClass, srcMethod, targetMethod);
        }
    }

    @Override
    public void visitCopy(final ClassNode node, final String desc, final String originalName, final String newName) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitCopy(node, desc, originalName, newName);
        }
    }

    @Override
    public void visitPairing(final ClassNode srcNode, final ClassNode targetNode) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitPairing(srcNode, targetNode);
        }
    }

    @Override
    public void visitInstantiationReplacement(final MethodNode method, final Map<String, String> typesToReplace) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitInstantiationReplacement(method, typesToReplace);
        }
    }

    @Override
    public void visitHook(final MethodNode methodToHook, final MethodNode methodToCall, final String owner, final boolean before) {
        for (final InjexVisitor visitor : visitors) {
            visitor.visitHook(methodToHook, methodToCall, owner, before);
        }
    }
}

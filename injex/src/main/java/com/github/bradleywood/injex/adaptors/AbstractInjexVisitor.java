package com.github.bradleywood.injex.adaptors;

import com.github.bradleywood.injex.InjexVisitor;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

public abstract class AbstractInjexVisitor implements InjexVisitor {

    @Override
    public void visitInjection(MethodNode srcMethod, ClassNode targetClass) {

    }

    @Override
    public void visitReplacement(String srcClass, String targetClass, MethodNode srcMethod, MethodNode targetMethod) {

    }

    @Override
    public void visitCopy(ClassNode node, String desc, String originalName, String newName) {

    }

    @Override
    public void visitPairing(ClassNode srcNode, ClassNode targetNode) {

    }

    @Override
    public void visitInstantiationReplacement(MethodNode method, Map<String, String> typesToReplace) {

    }

    @Override
    public void visitHook(MethodNode methodToHook, MethodNode methodToCall, String owner, boolean before) {

    }

    @Override
    public void visitClassAnnotation(ClassNode targetClass, AnnotationNode annotationNode) {

    }

    @Override
    public void visitFieldInjection(ClassNode targetClass, FieldNode fieldNode, String originalOwner) {

    }

    @Override
    public void visitFieldReplacement(MethodNode methodNode, List<String> fields) {

    }

    @Override
    public void visitInjectAtLine(MethodNode srcMethod, MethodNode destMethod, int lineNumber) {

    }

    @Override
    public void visitMerge(MethodNode srcNode, MethodNode destNode) {

    }
}

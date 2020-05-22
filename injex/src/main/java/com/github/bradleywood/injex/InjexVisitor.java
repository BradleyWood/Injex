package com.github.bradleywood.injex;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

public interface InjexVisitor {

    void visitInjection(MethodNode srcMethod, ClassNode targetClass);

    void visitReplacement(String srcClass, String destClass, MethodNode srcMethod, MethodNode targetMethod);

    void visitCopy(ClassNode node, String desc, String originalName, String newName);

    void visitPairing(ClassNode srcNode, ClassNode targetNode);

    void visitInstantiationReplacement(MethodNode method, Map<String, String> typesToReplace);

    void visitHook(MethodNode methodToHook, MethodNode methodToCall, String owner, boolean before);

    void visitFieldInjection(ClassNode targetClass, FieldNode fieldNode, String originalOwner);

    void visitFieldReplacement(MethodNode methodNode, List<String> fields);

    void visitClassAnnotation(ClassNode targetClass, AnnotationNode annotationNode);

    void visitMerge(MethodNode srcNode, MethodNode destNode);

}

package com.github.bradleywood.injex;

import com.github.bradleywood.injex.annotations.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.bradleywood.injex.InjexUtil.findMethod;

public class InjexPairing {

    private final ClassNode srcNode;
    private final ClassNode targetNode;
    private final Map<String, String> typesToReplace;

    public InjexPairing(final ClassNode srcNode, final ClassNode targetNode, final Map<String, String> typesToReplace) {
        this.srcNode = new ClassNode(Opcodes.ASM8);
        this.targetNode = new ClassNode(Opcodes.ASM8);

        srcNode.accept(this.srcNode);
        targetNode.accept(this.targetNode);

        this.typesToReplace = typesToReplace;
    }

    public ClassNode getResult() {
        return targetNode;
    }

    public void accept(final InjexVisitor visitor) {
        for (final MethodNode method : srcNode.methods) {
            final String copyMethodName = getCopyMethodName(method);

            if (copyMethodName != null) {
                visitor.visitCopy(targetNode, method.desc, copyMethodName, method.name);
            }
        }

        for (final MethodNode method : srcNode.methods) {
            final String replaceMethodName = getReplaceMethodName(method);

            if (replaceMethodName != null) {
                final MethodNode target = findMethod(targetNode.methods, replaceMethodName, method.desc);
                if (target != null) {
                    visitor.visitReplacement(srcNode.name, targetNode.name, method, target);
                } else {
                    throw new RuntimeException("Cannot find method to replace");
                }
            }
        }

        for (final MethodNode method : srcNode.methods) {
            final String hookAfterMethodName = getHookAfterMethods(method);
            final String hookBeforeMethodName = getHookBeforeMethods(method);

            if (hookAfterMethodName != null) {
                final MethodNode methodToHook = findMethod(targetNode.methods, hookAfterMethodName, method.desc);

                if (methodToHook == null)
                    throw new RuntimeException("Hook method: \"" + hookAfterMethodName + "\" not found");

                visitor.visitHook(methodToHook, method, targetNode.name, false);
            }

            if (hookBeforeMethodName != null) {
                final MethodNode methodToHook = findMethod(targetNode.methods, hookBeforeMethodName, method.desc);

                if (methodToHook == null)
                    throw new RuntimeException("Hook method: \"" + hookBeforeMethodName + "\" not found");

                visitor.visitHook(methodToHook, method, targetNode.name, true);
            }

            if (hookAfterMethodName != null || hookBeforeMethodName != null)
                visitor.visitInjection(method, targetNode);
        }

        for (final MethodNode method : srcNode.methods) {
            if (shouldInject(method)) {
                visitor.visitInjection(method, targetNode);
            }
        }

        visitor.visitPairing(srcNode, targetNode);

        for (final MethodNode method : targetNode.methods) {
            visitor.visitInstantiationReplacement(method, typesToReplace);
        }
    }

    private static String getCopyMethodName(final MethodNode node) {
        return getTarget(node, Copy.class);
    }

    private static String getHookBeforeMethods(final MethodNode node) {
        return getTarget(node, HookBefore.class);
    }

    private static String getHookAfterMethods(final MethodNode node) {
        return getTarget(node, HookAfter.class);
    }

    private static String getReplaceMethodName(final MethodNode node) {
        return getTarget(node, Replace.class);
    }

    private static boolean shouldInject(final MethodNode node) {
        if (node.visibleAnnotations == null)
            return false;

        for (AnnotationNode visibleAnnotation : node.visibleAnnotations) {
            if (Type.getType(Inject.class).equals(Type.getType(visibleAnnotation.desc)))
                return true;
        }

        return false;
    }

    private static String getTarget(final MethodNode node, final Class<?> annotationType) {
        if (node.visibleAnnotations == null)
            return null;

        List<String> types = node.visibleAnnotations.stream()
                .filter(n -> n.desc.equals(Type.getDescriptor(annotationType)))
                .map(n -> n.values)
                .filter(v -> v.size() == 2)
                .map(v -> v.get(1))
                .map(v -> {
                    if (v instanceof Type)
                        return ((Type) v).getDescriptor();
                    else
                        return v.toString();
                }).collect(Collectors.toList());

        if (types.size() != 1)
            return null;

        return types.get(0).replace(".", "/");
    }
}

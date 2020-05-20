package com.github.bradleywood.injex;

import com.github.bradleywood.injex.annotations.InjexTarget;
import com.github.bradleywood.injex.info.AlterationType;
import com.github.bradleywood.injex.info.ClassInfo;
import com.github.bradleywood.injex.info.InjexMethod;
import org.objectweb.asm.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.bradleywood.injex.InjexMethodVisitor.invokeHook;

public class InjexClassVisitor extends ClassVisitor {

    private final ClassInfo src;
    private final String name;

    private boolean done = false;

    public InjexClassVisitor(final ClassInfo src, final ClassVisitor classVisitor, final String name) {
        super(Opcodes.ASM8, classVisitor);

        this.src = src;
        this.name = name;
    }

    public InjexClassVisitor(final ClassInfo src, String name) {
        this(src, null, name);
    }

    @Override
    public void visitEnd() {
        if (!done) {
            done = true;
            src.getReader().accept(this, 0);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (Type.getType(InjexTarget.class).equals(Type.getType(descriptor))) {
            return null;
        }

        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

        if (!done) {
            final List<InjexMethod> replacements = getTargets(name, descriptor, AlterationType.REPLACE);

            for (InjexMethod target : getTargets(name, descriptor, AlterationType.COPY)) {
                super.visitMethod(access, target.getName(), descriptor, signature, exceptions);
            }

            if (replacements.size() == 1) {
                return null;
            } else if (replacements.size() > 1) {
                throw new RuntimeException("Cannot replace method more than once");
            }
        } else if (!shouldInject(name, descriptor))
            return null;

        final InjexMethod target = getTarget(name, descriptor);

        final MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (!done && target != null && target.getType() == AlterationType.HOOK_BEFORE) {
            invokeHook(mv, target, this.name);
        }

        return new InjexMethodVisitor(target, mv, this.name);
    }

    private List<InjexMethod> getAlterationsByType(List<InjexMethod> methods, AlterationType alterationType) {
        final List<InjexMethod> result = new LinkedList<>();

        for (InjexMethod method : methods) {
            if (method.getType() == alterationType) {
                result.add(method);
            }
        }

        return result;
    }

    private boolean shouldInject(String name, String desc) {
        return getTargets(name, desc).size() > 0;
    }

    private List<InjexMethod> getTargets(String name, String desc) {
        final List<InjexMethod> methods = new LinkedList<>();

        for (InjexMethod method : src.getMethods()) {
            if (method.getName().equals(name) && method.getDesc().equals(desc))
                methods.add(method);
        }

        return methods;
    }

    private InjexMethod getTarget(String name, String desc) {
        for (InjexMethod method : src.getMethods()) {
            if (name.equals(method.getTarget()) && method.getDesc().equals(desc))
                return method;
        }

        return null;
    }

    private List<InjexMethod> getTargets(final String name, String desc, AlterationType type) {
        return getTargets(name, desc).stream().filter(m -> m.getType() == type).collect(Collectors.toList());
    }
}

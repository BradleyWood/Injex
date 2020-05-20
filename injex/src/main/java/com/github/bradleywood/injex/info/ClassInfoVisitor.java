package com.github.bradleywood.injex.info;


import com.github.bradleywood.injex.annotations.InjexTarget;
import com.github.bradleywood.injex.annotations.ReplaceInstantiation;
import lombok.Getter;
import org.objectweb.asm.*;

import java.util.LinkedList;
import java.util.List;

public class ClassInfoVisitor extends ClassVisitor {

    @Getter
    private final List<InjexMethod> methodList = new LinkedList<>();

    @Getter
    private final List<InjexField> fieldList = new LinkedList<>();

    private final ClassInfo info;

    public ClassInfoVisitor(final ClassInfo info) {
        super(Opcodes.ASM8);

        this.info = info;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        final Type descType = Type.getType(descriptor);

        if (Type.getType(ReplaceInstantiation.class).equals(descType) || Type.getType(InjexTarget.class).equals(descType)) {
            if (info.getTarget() != null)
                throw new RuntimeException("Class can be annotated with either ReplaceInstantiation or InjexTarget");

            if (Type.getType(ReplaceInstantiation.class).equals(descType))
                info.setReplaceInstantiation(true);

            return new AnnotationInfoVisitor(info);
        }

        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        final InjexMethod method = new InjexMethod();

        method.setName(name);
        method.setDesc(descriptor);
        method.setAccess(access);
        methodList.add(method);

        return new MethodInfoVisitor(method);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        final InjexField field = new InjexField();

        field.setName(name);
        field.setDesc(descriptor);
        field.setAccess(access);
        fieldList.add(field);

        return new FieldInfoVisitor(field);
    }
}

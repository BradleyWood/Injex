package com.github.bradleywood.injex.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.objectweb.asm.ClassReader;

import java.lang.annotation.ElementType;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClassInfo extends InjexElement {

    private String name;
    private List<InjexField> fields;
    private List<InjexMethod> methods;
    private ClassReader reader;

    private ClassInfo() {
    }

    public static ClassInfo getClassInfo(final ClassReader reader) {
        final String name = reader.getClassName();

        final ClassInfo info = new ClassInfo();
        info.setName(name);

        final ClassInfoVisitor civ = new ClassInfoVisitor(info);
        reader.accept(civ, 0);

        final List<InjexField> fields = civ.getFieldList();
        final List<InjexMethod> methods = civ.getMethodList();

        for (InjexMethod method : methods) {
            method.setSrcClass(name);
        }

        for (InjexField field : fields) {
            field.setSrcClass(name);
        }

        methods.removeIf(m -> m.getType() == null);

        info.setMethods(methods);
        info.setFields(fields);
        info.setReader(reader);

        if (info.target == null)
            return null;

        return info;
    }
}

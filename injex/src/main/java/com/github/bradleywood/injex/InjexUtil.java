package com.github.bradleywood.injex;

import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class InjexUtil {

    public static MethodNode findMethod(final List<MethodNode> methods, final String name, final String desc) {
        for (final MethodNode method : methods) {
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        }

        return null;
    }

}

package com.github.bradleywood.injex.adaptors;

import com.google.common.collect.Iterables;
import org.objectweb.asm.tree.MethodNode;

import java.util.Map;

public class InjexReplaceInstantiation extends AbstractInjexVisitor {

    @Override
    public void visitInstantiationReplacement(final MethodNode method, final Map<String, String> typesToReplace) {
        final String[] exceptions = Iterables.toArray(method.exceptions, String.class);
        final MethodNode copy = new MethodNode(method.access, method.name, method.desc, method.signature, exceptions);

        final InstantiationReplacement mv = new InstantiationReplacement(typesToReplace, copy);
        method.accept(mv);

        method.instructions.clear();
        method.instructions.add(copy.instructions);
    }
}

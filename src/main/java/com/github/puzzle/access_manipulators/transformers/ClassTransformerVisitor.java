package com.github.puzzle.access_manipulators.transformers;

import com.github.puzzle.access_manipulators.AccessManipulators;
import com.github.puzzle.access_manipulators.pairs.FieldModifierPair;
import com.github.puzzle.access_manipulators.pairs.MethodModifierPair;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClassTransformerVisitor extends ClassVisitor {

    String className;

    protected ClassTransformerVisitor(String className, int api, ClassVisitor cv) {
        super(api, cv);
        this.className = className;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        AccessModifier f = AccessManipulators.classesToModify.get(className);

        if (f != null) {
                int newAccess = f.updateFlags(access);
             cv.visitInnerClass(name, outerName, innerName, newAccess);
            return;
        }
        cv.visitInnerClass(
                name,
                outerName,
                innerName,
                access
        );
    }
    @Override
    public void visitPermittedSubclass(String permittedSubclass) {
        AccessModifier f = AccessManipulators.classesToModify.get(className);

        if(!List.of(f.backupFlags).contains(Opcodes.ACC_FINAL)){
            return;
        }

//        if (access == AccessWidener.ClassAccess.EXTENDABLE || access == AccessWidener.ClassAccess.ACCESSIBLE_EXTENDABLE) {
//            return;
//        }

       cv.visitPermittedSubclass(permittedSubclass);
    }
    @Override
    public FieldVisitor visitField(
            int access, String name, String desc, String signature, Object value) {

        Map<String, FieldModifierPair> pairs = AccessManipulators.fieldsToModify.get(className);
        if (pairs != null) {
            FieldModifierPair pair = pairs.get(name);
            if (pair != null) {
                int newAccess = pair.modifier.updateFlags(access);
                return cv.visitField(newAccess, name, desc, signature, value);
            }
        }
        return cv.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
            final int access,
            final String name,
            final String descriptor,
            final String signature,
            final String[] exceptions) {

        List<MethodModifierPair> pairs = AccessManipulators.methodsToModify.get(className);
        if (pairs != null) {
            for (MethodModifierPair pair : pairs) {
                if (Objects.equals(pair.methodName, name) && Objects.equals(pair.methodDesc, descriptor)) {
                    int newAccess = pair.modifier.updateFlags(access);
                    return cv.visitMethod(newAccess, name, descriptor, signature, exceptions);
                }
            }
        }
        return cv.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
package dev.crmodders.puzzle.access_manipulators.readers.api;

import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import dev.crmodders.puzzle.access_manipulators.pairs.FieldModifierPair;
import dev.crmodders.puzzle.access_manipulators.pairs.MethodModifierPair;
import dev.crmodders.puzzle.access_manipulators.transformers.AccessModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IAccessModifierReader {

    void read(String contents);

    static void registerClassModifier(String className, AccessModifier accessModifier) {
        AccessManipulators.classesToModify.put(className, accessModifier);

        AccessManipulators.affectedClasses.add(className.replaceAll("\\.", "/") + ".class");
    }

    static void registerMethodModifier(String className, AccessModifier accessModifier, String methodName, String methodDesc) {
        registerMethodModifier(new MethodModifierPair(methodName, methodDesc, className, accessModifier));
    }

    static void registerMethodModifier(MethodModifierPair methodModifierPair) {
        List<MethodModifierPair> methodModifierPairs = AccessManipulators.methodsToModify.get(methodModifierPair.className);
        if (methodModifierPairs == null) methodModifierPairs = new ArrayList<>();

        methodModifierPairs.add(methodModifierPair);
        AccessManipulators.methodsToModify.put(methodModifierPair.className, methodModifierPairs);

        AccessManipulators.affectedClasses.add(methodModifierPair.className.replaceAll("\\.", "/") + ".class");
    }

    static void registerFieldModifier(String className, AccessModifier accessModifier, String fieldName) {
        registerFieldModifier(new FieldModifierPair(fieldName, className, accessModifier));
    }

    static void registerFieldModifier(FieldModifierPair fieldModifierPair) {
        Map<String, FieldModifierPair> fieldModifierPairs = AccessManipulators.fieldsToModify.get(fieldModifierPair.className);
        if (fieldModifierPairs == null) fieldModifierPairs = new HashMap<>();

        fieldModifierPairs.put(fieldModifierPair.fieldName, fieldModifierPair);
        AccessManipulators.fieldsToModify.put(fieldModifierPair.className, fieldModifierPairs);

        AccessManipulators.affectedClasses.add(fieldModifierPair.className.replaceAll("\\.", "/") + ".class");
    }

}

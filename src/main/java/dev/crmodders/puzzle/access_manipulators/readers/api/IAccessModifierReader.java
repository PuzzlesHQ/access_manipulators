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
        AccessManipulators.affectedClasses.add(className);
        AccessManipulators.classesToModify.put(className, accessModifier);
    }

    static void registerMethodModifier(String className, AccessModifier accessModifier, String methodName, String methodDesc) {
        registerMethodModifier(new MethodModifierPair(methodName, methodDesc, className, accessModifier));
    }

    static void registerMethodModifier(MethodModifierPair methodModifierPair) {
        if (AccessManipulators.methodsToModify.get(methodModifierPair.className) != null)
            AccessManipulators.methodsToModify.put(methodModifierPair.className, new ArrayList<>());

        List<MethodModifierPair> methodModifierPairs = AccessManipulators.methodsToModify.get(methodModifierPair.className);
        methodModifierPairs.add(methodModifierPair);
        AccessManipulators.methodsToModify.put(methodModifierPair.className, methodModifierPairs);

        AccessManipulators.affectedClasses.add(methodModifierPair.className);
    }

    static void registerFieldModifier(String className, AccessModifier accessModifier, String fieldName) {
        registerFieldModifier(new FieldModifierPair(fieldName, className, accessModifier));
    }

    static void registerFieldModifier(FieldModifierPair fieldModifierPair) {
        if (AccessManipulators.fieldsToModify.get(fieldModifierPair.className) != null)
            AccessManipulators.fieldsToModify.put(fieldModifierPair.className, new HashMap<>());

        Map<String, FieldModifierPair> fieldModifierPairs = AccessManipulators.fieldsToModify.get(fieldModifierPair.className);
        fieldModifierPairs.put(fieldModifierPair.className, fieldModifierPair);
        AccessManipulators.fieldsToModify.put(fieldModifierPair.className, fieldModifierPairs);

        AccessManipulators.affectedClasses.add(fieldModifierPair.className);
    }

}

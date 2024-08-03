package com.github.puzzle.access_manipulators.pairs;

import com.github.puzzle.access_manipulators.transformers.AccessModifier;

public class MethodModifierPair {

    public String methodName;
    public String methodDesc;
    public String className;
    public AccessModifier modifier;

    public MethodModifierPair(
            String methodName,
            String methodDesc,
            String className,
            AccessModifier modifier
    ) {
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.className = className;
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return modifier.name() + " " + className + " " + methodName + " " + methodDesc;
    }
}

package dev.crmodders.puzzle.access_manipulators.pairs;

import dev.crmodders.puzzle.access_manipulators.transformers.ClassModifier;

public class MethodModifierPair {

    public String methodName;
    public String methodDesc;
    public String className;
    public ClassModifier modifier;

    public MethodModifierPair(
            String methodName,
            String methodDesc,
            String className,
            ClassModifier modifier
    ) {
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.className = className;
        this.modifier = modifier;
    }

}

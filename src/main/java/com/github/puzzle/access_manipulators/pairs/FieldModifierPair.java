package com.github.puzzle.access_manipulators.pairs;

import com.github.puzzle.access_manipulators.transformers.AccessModifier;

public class FieldModifierPair {

    public String fieldName;
    public String className;
    public AccessModifier modifier;

    public FieldModifierPair(
            String fieldName,
            String className,
            AccessModifier modifier
    ) {
        this.fieldName = fieldName;
        this.className = className;
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return modifier.name() + " " + className + " " + fieldName;
    }
}

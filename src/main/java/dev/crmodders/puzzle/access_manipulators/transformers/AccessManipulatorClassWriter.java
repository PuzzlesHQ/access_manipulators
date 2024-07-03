package dev.crmodders.puzzle.access_manipulators.transformers;

import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class AccessManipulatorClassWriter {

    ClassReader reader;
    ClassWriter writer;

    String className;

    public AccessManipulatorClassWriter(String className, byte[] bytes) {
        this.className = className;

        reader = new ClassReader(bytes);
        if (AccessManipulators.classesToModify.containsKey(className)) {
            int updatedAccess = AccessManipulators.classesToModify.get(className).updateFlags(reader.getAccess());
            writer = new ClassWriter(reader, updatedAccess);
        } else writer = new ClassWriter(reader, reader.getAccess());
    }

    public byte[] applyManipulations() {
        reader.accept(new ClassTransformerVisitor(className, Opcodes.ASM9, writer), ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }

}

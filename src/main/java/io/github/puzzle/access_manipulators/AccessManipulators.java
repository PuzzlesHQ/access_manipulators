package io.github.puzzle.access_manipulators;

import io.github.puzzle.access_manipulators.pairs.FieldModifierPair;
import io.github.puzzle.access_manipulators.pairs.MethodModifierPair;
import io.github.puzzle.access_manipulators.readers.AccessManipulatorReader;
import io.github.puzzle.access_manipulators.readers.AccessTransformerReader;
import io.github.puzzle.access_manipulators.readers.AccessWidenerReader;
import io.github.puzzle.access_manipulators.readers.api.IAccessModifierReader;
import io.github.puzzle.access_manipulators.transformers.AccessManipulatorClassWriter;
import io.github.puzzle.access_manipulators.transformers.AccessModifier;
import io.github.puzzle.access_manipulators.util.ClassPathUtil;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class AccessManipulators {

    public static List<String> affectedClasses = new ArrayList<>();

    public static Map<String, AccessModifier> classesToModify = new HashMap<>();
    public static Map<String, Map<String, FieldModifierPair>> fieldsToModify = new HashMap<>();
    public static Map<String, List<MethodModifierPair>> methodsToModify = new HashMap<>();

    // FileExtension: Reader
    public static Map<String, IAccessModifierReader> readerMap = new HashMap<>();

    static {
        registerReader(".accesswidener", AccessWidenerReader.class);
        registerReader(".cfg", AccessTransformerReader.class);
        registerReader(".manipulator", AccessManipulatorReader.class);
    }

    public static void registerReader(String extension, Class<? extends IAccessModifierReader> reader) {
        try {
            readerMap.put(extension.replaceAll("\\.", ""), reader.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void registerModifierFile(String path) {
        String fileExt = path.split("\\.")[path.split("\\.").length - 1].toLowerCase();
        IAccessModifierReader reader = readerMap.get(fileExt);
        if (reader == null)
            throw new RuntimeException("Unsupported Access Modifier Extension \"."+fileExt+"\"");

        AtomicReference<InputStream> stream = new AtomicReference<>(AccessTransformerReader.class.getClassLoader().getResourceAsStream(path));
        if (stream.get() != null) {
            try {
                reader.read(new String(stream.get().readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            File file = new File(path);
            if (file.exists()) {
                FileInputStream stream1 = new FileInputStream(file);
                reader.read(new String(stream1.readAllBytes()));
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ClassPathUtil.iterateThroughClasspath(ClassPathUtil.getUrlsOnClasspath(), zipFile -> {
            try {
                stream.set(zipFile.getInputStream(zipFile.getEntry(path)));
                reader.read(new String(stream.get().readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static byte[] transformClass(String name, byte[] bytes) {
        name = name.replaceAll("\\.", "/");
        if (classesToModify.containsKey(name) || fieldsToModify.containsKey(name) || methodsToModify.containsKey(name)) {
            AccessManipulatorClassWriter writer = new AccessManipulatorClassWriter(name, bytes);
            return writer.applyManipulations();
        }
        return bytes;
    }

}

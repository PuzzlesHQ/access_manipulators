package dev.crmodders.puzzle.access_manipulators.readers;

import dev.crmodders.puzzle.access_manipulators.pairs.FieldModifierPair;
import dev.crmodders.puzzle.access_manipulators.pairs.MethodModifierPair;
import dev.crmodders.puzzle.access_manipulators.readers.api.IAccessModifierReader;
import dev.crmodders.puzzle.access_manipulators.AccessManipulators;
import dev.crmodders.puzzle.access_manipulators.transformers.AccessModifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class AccessTransformerReader implements IAccessModifierReader {

    public void read(String contents) {
        try {
            readTransformer(contents.replaceAll("\\(", " ("));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void readTransformer(String contents) throws IOException {
        BufferedReader reader =  new BufferedReader(new StringReader(contents));
        String ln;
        while((ln = reader.readLine())!=null){
            if(ln.isBlank() || ln.isEmpty() || ln.startsWith("#"))
                continue;
            List<String> tokens = Arrays.asList(Pattern.compile("[ \\t]+").split(ln));

            AccessModifier modifier = switch (tokens.get(0)) {
                case "public" -> AccessModifier.PUBLIC;
                case "private" -> AccessModifier.PRIVATE;
                case "protected" -> AccessModifier.PROTECTED;

                case "public+f" -> AccessModifier.PUBLIC_IMMUTABLE;
                case "private+f" -> AccessModifier.PRIVATE_IMMUTABLE;
                case "protected+f" -> AccessModifier.PROTECTED_IMMUTABLE;

                case "public-f" -> AccessModifier.PUBLIC_MUTABLE;
                case "private-f" -> AccessModifier.PRIVATE_MUTABLE;
                case "protected-f" -> AccessModifier.PROTECTED_MUTABLE;
                default -> throw new RuntimeException("Unsupported access: '" + tokens.get(0) + "'");
            };

            switch (tokens.size()) {
                case 2:
                    IAccessModifierReader.registerClassModifier(tokens.get(1).replaceAll("\\.", "/"), modifier);
                    break;
                case 3:
                    IAccessModifierReader.registerFieldModifier(new FieldModifierPair(tokens.get(2), tokens.get(1).replaceAll("\\.", "/"), modifier));
                    break;
                case 4:
                    IAccessModifierReader.registerMethodModifier(new MethodModifierPair(tokens.get(2),tokens.get(3),tokens.get(1).replaceAll("\\.", "/"), modifier));
                    break;
                default:
                    throw new RuntimeException("Unsupported type: '" + tokens.get(1) + "'");
            }
        }
    }

}
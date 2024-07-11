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

public class AccessWidenerReader implements IAccessModifierReader {

    public void read(String contents) {
        try {
            readWidener(contents);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void readWidener(String contents) throws IOException {
        BufferedReader reader =  new BufferedReader(new StringReader(contents));
        String ln;
        while((ln = reader.readLine())!=null){
            if (ln.startsWith("accessWidener "))
                continue;
            if(ln.isBlank() || ln.isEmpty() || ln.startsWith("#"))
                continue;
            List<String> tokens = Arrays.asList(Pattern.compile("[ \\t]+").split(ln));
            AccessModifier modifier;
            var access = tokens.get(0);
            modifier = switch (access) {
                case "accessible" -> AccessModifier.PUBLIC;
                case "extendable" -> AccessModifier.PRIVATE;
                case "mutable" -> AccessModifier.MUTABLE;
                default -> throw new RuntimeException("Unsupported access: '" + tokens.get(0) + "'");
            };
            var type = tokens.get(1);
            switch (type) {
                case "class":
                    if (tokens.size()==3) {
                        IAccessModifierReader.registerClassModifier(tokens.get(2), modifier);
                    }
                    else
                        throw new RuntimeException("Layout is invalid for class AW");
                    break;
                case "field":
                    if (tokens.size()==5) {
                        IAccessModifierReader.registerFieldModifier(new FieldModifierPair(tokens.get(3), tokens.get(2), modifier));
                    }
                    else
                        throw new RuntimeException("Layout is invalid for field AW");
                    break;
                case "method":
                    if(tokens.size()==5){
                        IAccessModifierReader.registerMethodModifier(new MethodModifierPair(tokens.get(3),tokens.get(4),tokens.get(2), modifier));
                    }else {
                        throw new RuntimeException("Layout is invalid for method AW");
                    }
                    break;
                default:
                    throw new RuntimeException("Unsupported type: '" + tokens.get(1) + "'");
            }
        }
    }

}
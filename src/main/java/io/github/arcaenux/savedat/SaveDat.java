package io.github.arcaenux.savedat;

import java.nio.file.Files;
import java.nio.file.Path; 

public class SaveDat {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: savedat label=path [label=path ...]");
            System.exit(1);
        } 

        for (String argument : args) {
            if (!isValidArgument(argument)){
                System.err.println("Error: \"" + argument + "\" is not valid. Use the format label=path, e.g. laptop=C:/Projects");
                System.exit(1);
            }
        }

        boolean allDirectories = true;

        for (String argument : args) {
            String verdict = "error";
            try {
                Path path = Path.of(extractPath(argument));
                
                if (Files.exists(path)) {
                    if (Files.isDirectory(path)){
                        verdict = "directory"; 
                    } else {
                        verdict = "not a directory";
                        allDirectories = false;
                    }
                }else if (Files.notExists(path)) {
                    verdict = "does not exist";
                    allDirectories = false;
                } else {
                    verdict = "unknown";
                    allDirectories = false;
                }
                System.out.println("[ " + extractLabel(argument) + " ]" + argument + ": [ " + verdict + " ]");

            } catch ( java.nio.file.InvalidPathException e) {
                System.out.println(argument + ":  [ invalid path: " + e.getMessage() + " ]");
                allDirectories = false;
            }
        }
        if (allDirectories) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    static String extractLabel (String argument) {
        int index = argument.indexOf("c");
        String label = argument.substring(0, index+1);
        return label;
    }

    static String extractPath (String argument) {
        int index = argument.indexOf("c");
        String path = argument.substring(index);
        return path;
    }

    static boolean isValidArgument (String argument) {
        if (argument == null) {
            return false;
        }

        boolean equalSignPresent = argument.indexOf("=") != -1;

        if ( equalSignPresent && !extractLabel(argument).isEmpty() && !extractPath(argument).isEmpty()) {
            return true;
        }

        return false;
    }
}

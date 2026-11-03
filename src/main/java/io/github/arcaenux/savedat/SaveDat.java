package io.github.arcaenux.savedat;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * SaveDat - A tool to identify files that exist in only one place.
 * <p>
 * SaveDat analyzes the folders you point it at and reports which files would
 * be lost if a storage device disappeared. Files are identified by content,
 * not name, and devices are declared by the user via labels.
 * </p>
 * <p>
 * This is early development - currently only validates that provided paths
 * are directories.
 * </p>
 * 
 * @author Joseph Boafo Afful
 * @version 0.1.0-dev
 * @see <a href="https://github.com/arcaenux/SaveDat">SaveDat Repository</a>
 */
public class SaveDat {
    
    private static final String USAGE_MESSAGE = "Usage: savedat label=path [label=path ...]";
    private static final String INVALID_ARGUMENT_FORMAT = 
        "Error: \"%s\" is not valid. Use the format label=path, e.g. laptop=C:/Projects";
    private static final String INVALID_PATH_MESSAGE = "%s:  [ invalid path: %s ]";
    
    private static final String VERDICT_DIRECTORY = "directory";
    private static final String VERDICT_NOT_DIRECTORY = "not a directory";
    private static final String VERDICT_DOES_NOT_EXIST = "does not exist";
    private static final String VERDICT_UNKNOWN = "unknown";
    private static final String VERDICT_ERROR = "error";
    
    private static final String SEPARATOR_EQUALS = "=";
    private static final String OUTPUT_FORMAT = "[ %s ] %s: [ %s ]";
    
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;
    
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println(USAGE_MESSAGE);
            System.exit(EXIT_FAILURE);
            return;
        }
        
        if (!validateAllArguments(args)) {
            System.exit(EXIT_FAILURE);
            return;
        }
        
        boolean allDirectories = processLocations(args);
        
        if (allDirectories) {
            System.exit(EXIT_SUCCESS);
        } else {
            System.exit(EXIT_FAILURE);
        }
    }
    
    /**
     * Validates all arguments have the correct format.
     * If any argument is invalid, prints an error message and returns false.
     * 
     * @param args the command line arguments
     * @return true if all arguments are valid, false otherwise
     */
    private static boolean validateAllArguments(String[] args) {
        for (String argument : args) {
            if (!isValidArgument(argument)) {
                System.err.println(String.format(INVALID_ARGUMENT_FORMAT, argument));
                return false;
            }
        }
        return true;
    }
    
    /**
     * Processes all location arguments and reports their status.
     * 
     * @param args the command line arguments in label=path format
     * @return true if all locations are directories, false otherwise
     */
    private static boolean processLocations(String[] args) {
        boolean allDirectories = true;
        
        for (String argument : args) {
            String label = extractLabel(argument);
            String pathString = extractPath(argument);
            
            PathStatus status = checkPath(pathString);
            printLocationStatus(label, pathString, status);
            
            if (status != PathStatus.DIRECTORY) {
                allDirectories = false;
            }
        }
        
        return allDirectories;
    }
    
    /**
     * Represents the status of a path.
     */
    private enum PathStatus {
        DIRECTORY,
        NOT_DIRECTORY,
        DOES_NOT_EXIST,
        UNKNOWN,
        INVALID_PATH
    }
    
    /**
     * Checks the status of a path.
     * 
     * @param pathString the path to check
     * @return the status of the path
     */
    private static PathStatus checkPath(String pathString) {
        try {
            Path path = Path.of(pathString);
            
            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    return PathStatus.DIRECTORY;
                } else {
                    return PathStatus.NOT_DIRECTORY;
                }
            } else if (Files.notExists(path)) {
                return PathStatus.DOES_NOT_EXIST;
            } else {
                return PathStatus.UNKNOWN;
            }
        } catch (InvalidPathException e) {
            return PathStatus.INVALID_PATH;
        }
    }
    
    /**
     * Prints the status of a location.
     * 
     * @param label the device label
     * @param pathString the path
     * @param status the path status
     */
    private static void printLocationStatus(String label, String pathString, PathStatus status) {
        if (status == PathStatus.INVALID_PATH) {
            try {
                Path.of(pathString);
            } catch (InvalidPathException e) {
                System.out.println(String.format(INVALID_PATH_MESSAGE, label + "=" + pathString, e.getMessage()));
            }
            return;
        }
        
        String verdict = switch (status) {
            case DIRECTORY -> VERDICT_DIRECTORY;
            case NOT_DIRECTORY -> VERDICT_NOT_DIRECTORY;
            case DOES_NOT_EXIST -> VERDICT_DOES_NOT_EXIST;
            case UNKNOWN -> VERDICT_UNKNOWN;
            case INVALID_PATH -> VERDICT_ERROR;
        };
        
        System.out.println(String.format(OUTPUT_FORMAT, label, pathString, verdict));
    }
    
    /**
     * Extracts the label from an argument in label=path format.
     * 
     * @param argument the argument string
     * @return the label portion
     */
    static String extractLabel(String argument) {
        if (argument == null) {
            return "";
        }
        int index = argument.indexOf(SEPARATOR_EQUALS);
        if (index < 0) {
            return argument;
        }
        return argument.substring(0, index);
    }
    
    /**
     * Extracts the path from an argument in label=path format.
     * 
     * @param argument the argument string
     * @return the path portion
     */
    static String extractPath(String argument) {
        if (argument == null) {
            return "";
        }
        int index = argument.indexOf(SEPARATOR_EQUALS);
        if (index < 0) {
            return "";
        }
        return argument.substring(index + 1);
    }
    
    /**
     * Validates that an argument is in label=path format.
     * Both label and path must be non-empty.
     * 
     * @param argument the argument to validate
     * @return true if the argument is valid, false otherwise
     */
    static boolean isValidArgument(String argument) {
        if (argument == null) {
            return false;
        }
        
        boolean hasEqualsSign = argument.contains(SEPARATOR_EQUALS);
        if (!hasEqualsSign) {
            return false;
        }
        
        String label = extractLabel(argument);
        String path = extractPath(argument);
        
        return !label.isEmpty() && !path.isEmpty();
    }
}

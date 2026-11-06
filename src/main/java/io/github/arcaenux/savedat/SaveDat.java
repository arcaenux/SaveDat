package io.github.arcaenux.savedat;

import java.nio.file.Files;
import java.nio.file.Path; 
import java.util.ArrayList;
import java.util.List;

public class SaveDat {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: savedat label=path [label=path ...]");
            System.exit(1);
        } 

        List<StorageLocation> locations = new ArrayList<>();
        for (String argument: args) {
            try {
                locations.add(StorageLocation.parse(argument));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }
        }
    

        boolean allDirectories = true;


        for (StorageLocation location : locations) {
            Path path = location.path();
            String verdict;

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
            System.out.println("[ " + location.label() + " ] " + path + ": [ " + verdict + " ]");

        }
        if (allDirectories) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}

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
        for (String argument : args) {
            try {
                locations.add(StorageLocation.parse(argument));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(1);
            }
        }

        boolean allGood = true;

        for (StorageLocation location : locations) {
            Path path = location.path();
            String verdict;

            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    verdict = "directory";
                } else {
                    verdict = "not a directory";
                    allGood = false;
                }
            } else if (Files.notExists(path)) {
                verdict = "does not exist";
                allGood = false;
            } else {
                verdict = "unknown";
                allGood = false;
            }

            System.out.println("[ " + location.label() + " ] " + path + ": [ " + verdict + " ]");

            if (verdict.equals("directory")) {
                ScanResult result = FileScanner.scan(location);

                long totalBytes = 0;
                for (ScannedFile file : result.files()) {
                    totalBytes += file.size();
                }

                System.out.println("    files: " + result.files().size() + ", total bytes: " + totalBytes);
                System.out.println("    could not read: " + result.failures().size());
                for (ScanFailure failure : result.failures()) {
                    System.out.println("      " + failure.path() + " (" + failure.reason() + ")");
                }

                if (!result.failures().isEmpty()) {
                    allGood = false;
                }
            }
        }

        if (allGood) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
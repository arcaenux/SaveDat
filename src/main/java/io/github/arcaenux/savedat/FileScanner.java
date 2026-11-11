package io.github.arcaenux.savedat;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class FileScanner {

    public static ScanResult scan(StorageLocation location) {
        List<ScannedFile> files = new ArrayList<>();
        List<ScanFailure> failures = new ArrayList<>();

        try {
            Files.walkFileTree(location.path(), new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (attrs.isRegularFile()) {
                        files.add(new ScannedFile(location.label(), file, attrs.size(), attrs.lastModifiedTime()));
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    failures.add(new ScanFailure(file, exc.getMessage()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            failures.add(new ScanFailure(location.path(), e.getMessage()));
        }

        return new ScanResult(files, failures);
    }
}
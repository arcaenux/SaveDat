package io.github.arcaenux.savedat;

import java.nio.file.Path;

public final class StorageLocation {
    private final String label;
    private final Path path;

    private StorageLocation(String label, Path path) {
        this.label = label;
        this.path = path;
    }

    public static StorageLocation parse(String argument) {
        int index = argument.indexOf('=');

        if ( index == -1) {
             throw new IllegalArgumentException("\"" + argument
                    + "\" is not valid. Use the format label=path, e.g. laptop=C:/Projects");
        }

        String label = argument.substring(0, index);
        String pathText =  argument.substring(index + 1);


        if (label.isEmpty() ||  pathText.isEmpty()) {
            throw new IllegalArgumentException("\"" + argument
                    + "\" is not valid. Use the format label=path, e.g. laptop=C:/Projects");
        }
        return new StorageLocation(label, Path.of(pathText));
    }

    public String label() {
        return label;
    } 

    public Path path() {
        return path;
    }
}

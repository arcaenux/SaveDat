package io.github.arcaenux.savedat;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public record ScannedFile(String deviceLabel, Path path, long size, FileTime lastModified) {}
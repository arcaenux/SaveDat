package io.github.arcaenux.savedat;

import java.nio.file.Path;

public record ScanFailure(Path path, String reason) {}
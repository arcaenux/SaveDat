package io.github.arcaenux.savedat;

import java.util.List;

public record ScanResult(List<ScannedFile> files, List<ScanFailure> failures) {}
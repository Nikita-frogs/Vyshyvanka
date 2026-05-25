package serialization;

public record EmbroideryPattern(
        int schemaVersion,
        int rows,
        int columns,
        String[][] colors
) {}

package serialization;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PatternSerializer {
    private static final int SUPPORTED_SCHEMA_VERSION = 1;

    public void save(EmbroideryPattern pattern, Path path) throws IOException {
        validate(pattern);
        Files.writeString(path, toJson(pattern), StandardCharsets.UTF_8);
    }

    public EmbroideryPattern load(Path path) throws IOException {
        Object parsed = new JsonParser(Files.readString(path, StandardCharsets.UTF_8)).parse();

        if (!(parsed instanceof Map<?, ?> object)) {
            throw new IllegalArgumentException("Pattern JSON must be an object.");
        }

        EmbroideryPattern pattern = new EmbroideryPattern(
                readInt(object, "schemaVersion"),
                readInt(object, "rows"),
                readInt(object, "columns"),
                readColors(object)
        );
        validate(pattern);
        return pattern;
    }

    private String toJson(EmbroideryPattern pattern) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"schemaVersion\": ").append(pattern.schemaVersion()).append(",\n");
        json.append("  \"rows\": ").append(pattern.rows()).append(",\n");
        json.append("  \"columns\": ").append(pattern.columns()).append(",\n");
        json.append("  \"colors\": [\n");

        for (int row = 0; row < pattern.colors().length; row++) {
            json.append("    [");
            for (int column = 0; column < pattern.colors()[row].length; column++) {
                if (column > 0) {
                    json.append(", ");
                }
                json.append('"').append(pattern.colors()[row][column]).append('"');
            }
            json.append("]");
            if (row < pattern.colors().length - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private void validate(EmbroideryPattern pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("Pattern cannot be null.");
        }
        if (pattern.schemaVersion() != SUPPORTED_SCHEMA_VERSION) {
            throw new IllegalArgumentException("Unsupported pattern schema version: " + pattern.schemaVersion());
        }
        if (pattern.rows() <= 0 || pattern.columns() <= 0) {
            throw new IllegalArgumentException("Pattern dimensions must be positive.");
        }
        if (pattern.colors() == null || pattern.colors().length != pattern.rows()) {
            throw new IllegalArgumentException("Pattern row count does not match colors.");
        }

        for (String[] row : pattern.colors()) {
            if (row == null || row.length != pattern.columns()) {
                throw new IllegalArgumentException("Pattern column count does not match colors.");
            }
            for (String color : row) {
                if (color == null || !color.matches("#[0-9A-Fa-f]{6}")) {
                    throw new IllegalArgumentException("Invalid color value: " + color);
                }
            }
        }
    }

    private static int readInt(Map<?, ?> object, String fieldName) {
        Object value = object.get(fieldName);
        if (value instanceof Integer number) {
            return number;
        }
        throw new IllegalArgumentException("Missing or invalid integer field: " + fieldName);
    }

    private static String[][] readColors(Map<?, ?> object) {
        Object value = object.get("colors");
        if (!(value instanceof List<?> rows)) {
            throw new IllegalArgumentException("Missing or invalid colors field.");
        }

        String[][] colors = new String[rows.size()][];
        for (int row = 0; row < rows.size(); row++) {
            Object rowValue = rows.get(row);
            if (!(rowValue instanceof List<?> columns)) {
                throw new IllegalArgumentException("Invalid colors row at index " + row);
            }

            colors[row] = new String[columns.size()];
            for (int column = 0; column < columns.size(); column++) {
                Object color = columns.get(column);
                if (!(color instanceof String text)) {
                    throw new IllegalArgumentException("Invalid color at row " + row + ", column " + column);
                }
                colors[row][column] = text;
            }
        }
        return colors;
    }

    private static class JsonParser {
        private final String json;
        private int index;

        JsonParser(String json) {
            this.json = json;
        }

        Object parse() {
            Object value = readValue();
            skipWhitespace();
            if (index != json.length()) {
                throw error("Unexpected trailing content.");
            }
            return value;
        }

        private Object readValue() {
            skipWhitespace();
            if (index >= json.length()) {
                throw error("Unexpected end of JSON.");
            }

            char current = json.charAt(index);
            if (current == '{') {
                return readObject();
            }
            if (current == '[') {
                return readArray();
            }
            if (current == '"') {
                return readString();
            }
            if (current == '-' || Character.isDigit(current)) {
                return readNumber();
            }
            throw error("Unexpected character: " + current);
        }

        private Map<String, Object> readObject() {
            expect('{');
            Map<String, Object> object = new LinkedHashMap<>();
            skipWhitespace();
            if (peek('}')) {
                index++;
                return object;
            }

            while (true) {
                skipWhitespace();
                String key = readString();
                skipWhitespace();
                expect(':');
                object.put(key, readValue());
                skipWhitespace();

                if (peek('}')) {
                    index++;
                    return object;
                }
                expect(',');
            }
        }

        private List<Object> readArray() {
            expect('[');
            List<Object> array = new ArrayList<>();
            skipWhitespace();
            if (peek(']')) {
                index++;
                return array;
            }

            while (true) {
                array.add(readValue());
                skipWhitespace();

                if (peek(']')) {
                    index++;
                    return array;
                }
                expect(',');
            }
        }

        private String readString() {
            expect('"');
            StringBuilder value = new StringBuilder();

            while (index < json.length()) {
                char current = json.charAt(index++);
                if (current == '"') {
                    return value.toString();
                }
                if (current == '\\') {
                    value.append(readEscape());
                } else {
                    value.append(current);
                }
            }

            throw error("Unterminated string.");
        }

        private char readEscape() {
            if (index >= json.length()) {
                throw error("Unterminated escape sequence.");
            }

            char escaped = json.charAt(index++);
            return switch (escaped) {
                case '"', '\\', '/' -> escaped;
                case 'b' -> '\b';
                case 'f' -> '\f';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                default -> throw error("Unsupported escape sequence: \\" + escaped);
            };
        }

        private int readNumber() {
            int start = index;
            if (peek('-')) {
                index++;
            }

            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }

            return Integer.parseInt(json.substring(start, index));
        }

        private void skipWhitespace() {
            while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
                index++;
            }
        }

        private boolean peek(char expected) {
            return index < json.length() && json.charAt(index) == expected;
        }

        private void expect(char expected) {
            skipWhitespace();
            if (!peek(expected)) {
                throw error("Expected '" + expected + "'.");
            }
            index++;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException(message + " At character " + index + ".");
        }
    }
}

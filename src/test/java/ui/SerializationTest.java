package ui;

import org.junit.jupiter.api.Test;
import serialization.EmbroideryPattern;
import serialization.PatternSerializer;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTest {
    @Test
    public void testCanvasPatternRoundTrip() throws Exception {
        EmbroideryCanvas canvas = new EmbroideryCanvas(2, 2, 14);
        canvas.cells[0][0] = Color.BLACK;
        canvas.cells[0][1] = new Color(190, 30, 45);
        canvas.cells[1][0] = new Color(25, 95, 55);
        canvas.cells[1][1] = new Color(245, 185, 65);

        Path file = Files.createTempFile("vyshyvanka-pattern", ".json");
        PatternSerializer serializer = new PatternSerializer();
        serializer.save(canvas.toPattern(), file);

        EmbroideryPattern loaded = serializer.load(file);
        EmbroideryCanvas restored = new EmbroideryCanvas(loaded.columns(), loaded.rows(), 14);
        restored.loadPattern(loaded);

        assertEquals(canvas.cells.length, restored.cells.length);
        assertEquals(canvas.cells[0].length, restored.cells[0].length);
        assertArrayEquals(canvas.cells[0], restored.cells[0]);
        assertArrayEquals(canvas.cells[1], restored.cells[1]);
    }

    @Test
    public void testSerializerLoadsSavedPattern() throws Exception {
        EmbroideryPattern pattern = new EmbroideryPattern(
                1,
                2,
                3,
                new String[][]{
                        {"#FFFFFF", "#000000", "#BE1E2D"},
                        {"#195F37", "#F5B941", "#FFFFFF"}
                }
        );

        Path file = Files.createTempFile("vyshyvanka-pattern", ".json");
        PatternSerializer serializer = new PatternSerializer();
        serializer.save(pattern, file);

        EmbroideryPattern loaded = serializer.load(file);

        assertEquals(pattern.schemaVersion(), loaded.schemaVersion());
        assertEquals(pattern.rows(), loaded.rows());
        assertEquals(pattern.columns(), loaded.columns());
        assertArrayEquals(pattern.colors()[0], loaded.colors()[0]);
        assertArrayEquals(pattern.colors()[1], loaded.colors()[1]);
    }
}

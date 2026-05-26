package ui;

import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextEmbroideryGeneratorTest {
    @Test
    void generatedPatternUsesAlternatingThreadColors() {
        TextEmbroideryGenerator.GeneratedPattern pattern = TextEmbroideryGenerator.generate(
                "\u041C\u0418\u0420",
                TextEmbroideryGenerator.LayoutVariant.DIAGONAL_THEN_VERTICAL
        );

        assertTrue(count(pattern.cells(), TextEmbroideryGenerator.RED) > 0);
        assertTrue(count(pattern.cells(), TextEmbroideryGenerator.BLACK) > 0);
    }

    @Test
    void reversedVariantChangesTheRenderedCells() {
        TextEmbroideryGenerator.GeneratedPattern normal = TextEmbroideryGenerator.generate(
                "\u041C\u0418\u0420",
                TextEmbroideryGenerator.LayoutVariant.DIAGONAL_THEN_VERTICAL
        );
        TextEmbroideryGenerator.GeneratedPattern reversed = TextEmbroideryGenerator.generate(
                "\u041C\u0418\u0420",
                TextEmbroideryGenerator.LayoutVariant.REVERSED_DIAGONAL_THEN_VERTICAL
        );

        assertNotEquals(signature(normal.cells()), signature(reversed.cells()));
    }

    @Test
    void canvasCanBeReplacedWithGeneratedPattern() {
        EmbroideryCanvas canvas = new EmbroideryCanvas(2, 2, 14);
        TextEmbroideryGenerator.GeneratedPattern pattern = TextEmbroideryGenerator.generate(
                "AB",
                TextEmbroideryGenerator.LayoutVariant.VERTICAL_THEN_DIAGONAL
        );

        canvas.replaceCells(pattern.cells());

        assertEquals(pattern.rows(), canvas.getRows());
        assertEquals(pattern.columns(), canvas.getColumns());
    }

    private static long count(Color[][] cells, Color color) {
        long total = 0;
        for (Color[] row : cells) {
            for (Color cell : row) {
                if (color.equals(cell)) {
                    total++;
                }
            }
        }
        return total;
    }

    private static String signature(Color[][] cells) {
        StringBuilder signature = new StringBuilder();
        for (Color[] row : cells) {
            for (Color cell : row) {
                if (TextEmbroideryGenerator.RED.equals(cell)) {
                    signature.append('R');
                } else if (TextEmbroideryGenerator.BLACK.equals(cell)) {
                    signature.append('B');
                } else {
                    signature.append('.');
                }
            }
            signature.append('\n');
        }
        return signature.toString();
    }
}

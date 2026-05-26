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

    @Test
    void shortWordPatternHasClearCenterAndSurroundingMotifs() {
        TextEmbroideryGenerator.GeneratedPattern pattern = TextEmbroideryGenerator.generate(
                "\u041C\u0418\u0420",
                TextEmbroideryGenerator.LayoutVariant.DIAGONAL_THEN_VERTICAL
        );

        Bounds redBounds = bounds(pattern.cells(), TextEmbroideryGenerator.RED);
        Bounds blackBounds = bounds(pattern.cells(), TextEmbroideryGenerator.BLACK);

        assertTrue(redBounds.width() > 10);
        assertTrue(redBounds.height() > 10);
        assertTrue(blackBounds.minColumn() > redBounds.minColumn());
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

    private static Bounds bounds(Color[][] cells, Color color) {
        int minRow = Integer.MAX_VALUE;
        int maxRow = Integer.MIN_VALUE;
        int minColumn = Integer.MAX_VALUE;
        int maxColumn = Integer.MIN_VALUE;

        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                if (color.equals(cells[row][column])) {
                    minRow = Math.min(minRow, row);
                    maxRow = Math.max(maxRow, row);
                    minColumn = Math.min(minColumn, column);
                    maxColumn = Math.max(maxColumn, column);
                }
            }
        }

        return new Bounds(minRow, maxRow, minColumn, maxColumn);
    }

    private record Bounds(int minRow, int maxRow, int minColumn, int maxColumn) {
        int width() {
            return maxColumn - minColumn + 1;
        }

        int height() {
            return maxRow - minRow + 1;
        }
    }
}

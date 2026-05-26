package ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class TextEmbroideryGenerator {
    static final Color RED = new Color(190, 30, 45);
    static final Color BLACK = Color.BLACK;
    static final Color WHITE = Color.WHITE;

    private static final String UKRAINIAN_ALPHABET =
            " \u0410\u0411\u0412\u0413\u0490\u0414\u0415\u0404\u0416\u0417\u0418\u0406\u0407\u0419\u041A\u041B\u041C\u041D\u041E\u041F\u0420\u0421\u0422\u0423\u0424\u0425\u0426\u0427\u0428\u0429\u042C\u042E\u042F";
    private static final int MOTIF_SIZE = 5;
    private static final int FLOWER_SIZE = 9;
    private static final int STEP = 10;
    private static final int WORD_GAP = 10;
    private static final int PADDING = 8;
    private static final int MIN_GENERATED_SIZE = 35;

    private TextEmbroideryGenerator() {
    }

    static GeneratedPattern generate(String text, LayoutVariant variant) {
        if (text == null || text.isBlank()) {
            return new GeneratedPattern(new Color[][]{{WHITE}});
        }

        List<PlacedMotif> motifs = placeMotifs(text, variant);
        if (motifs.isEmpty()) {
            return new GeneratedPattern(new Color[][]{{WHITE}});
        }

        Bounds bounds = Bounds.from(motifs);
        int rows = Math.max(MIN_GENERATED_SIZE, bounds.height() + PADDING * 2);
        int columns = Math.max(MIN_GENERATED_SIZE, bounds.width() + PADDING * 2);
        Color[][] cells = new Color[rows][columns];
        fill(cells, WHITE);

        int rowCenteringOffset = (rows - bounds.height()) / 2;
        int columnCenteringOffset = (columns - bounds.width()) / 2;
        for (PlacedMotif motif : motifs) {
            boolean[][] shape = motif.flower ? createFlower(motif.letter) : createLetterMotif(motif.letter);
            draw(
                    cells,
                    shape,
                    motif.row - bounds.minRow + rowCenteringOffset,
                    motif.column - bounds.minColumn + columnCenteringOffset,
                    motif.color
            );
        }

        return new GeneratedPattern(cells);
    }

    private static List<PlacedMotif> placeMotifs(String text, LayoutVariant variant) {
        List<PlacedMotif> motifs = new ArrayList<>();
        String normalized = text.toUpperCase(Locale.ROOT);
        int wordStartColumn = 0;
        int letterIndexInWord = 0;

        for (int index = 0; index < normalized.length(); index++) {
            char letter = normalized.charAt(index);
            if (Character.isWhitespace(letter)) {
                if (letterIndexInWord > 0) {
                    wordStartColumn = Bounds.from(motifs).maxColumn + WORD_GAP;
                    letterIndexInWord = 0;
                }
                continue;
            }

            if (!isSupported(letter)) {
                continue;
            }

            int sequence = letterIndexInWord;
            int row = 0;
            int column = wordStartColumn;
            boolean flower = sequence == 0;

            if (!flower) {
                Offset offset = offsetFor(sequence, variant);
                row += flowerCenter() + offset.row - MOTIF_SIZE / 2;
                column += flowerCenter() + offset.column - MOTIF_SIZE / 2;
            }

            Color color = motifs.size() % 2 == 0 ? RED : BLACK;
            motifs.add(new PlacedMotif(letter, row, column, color, flower));
            letterIndexInWord++;
        }

        return motifs;
    }

    private static Offset offsetFor(int sequence, LayoutVariant variant) {
        int index = sequence - 1;
        int ring = index / 8 + 1;
        int direction = index % 8;
        int distance = ring * STEP;
        int[][] offsets = variant.startsWithDiagonal() ? diagonalFirstOffsets(distance) : verticalFirstOffsets(distance);
        int[] offset = offsets[direction];
        return new Offset(offset[0] * variant.diagonalDirection(), offset[1]);
    }

    private static int[][] diagonalFirstOffsets(int distance) {
        return new int[][]{
                {-distance, distance},
                {-distance, 0},
                {distance, distance},
                {0, distance},
                {distance, -distance},
                {distance, 0},
                {-distance, -distance},
                {0, -distance}
        };
    }

    private static int[][] verticalFirstOffsets(int distance) {
        return new int[][]{
                {-distance, 0},
                {-distance, distance},
                {0, distance},
                {distance, distance},
                {distance, 0},
                {distance, -distance},
                {0, -distance},
                {-distance, -distance}
        };
    }

    private static int flowerCenter() {
        return FLOWER_SIZE / 2;
    }

    private static boolean isSupported(char letter) {
        return UKRAINIAN_ALPHABET.indexOf(letter) >= 0
                || letter >= 'A' && letter <= 'Z'
                || letter >= '0' && letter <= '9';
    }

    private static boolean[][] createLetterMotif(char letter) {
        int code = letterCode(letter);
        boolean[][] motif = new boolean[MOTIF_SIZE][MOTIF_SIZE];

        motif[2][2] = true;
        for (int bit = 0; bit < 5; bit++) {
            boolean enabled = ((code >> bit) & 1) == 1;
            motif[bit][2] = true;
            motif[2][bit] = enabled || bit == 2;
            motif[bit][bit] = enabled;
            motif[bit][MOTIF_SIZE - 1 - bit] |= ((code >> (bit + 1)) & 1) == 1;
        }

        return motif;
    }

    private static boolean[][] createFlower(char letter) {
        boolean[][] letterMotif = createLetterMotif(letter);
        boolean[][] flower = new boolean[FLOWER_SIZE][FLOWER_SIZE];

        for (int row = 0; row < MOTIF_SIZE; row++) {
            for (int column = 0; column < MOTIF_SIZE; column++) {
                if (letterMotif[row][column]) {
                    int targetRow = row + 2;
                    int targetColumn = column + 2;
                    flower[targetRow][targetColumn] = true;
                    flower[FLOWER_SIZE - 1 - targetRow][targetColumn] = true;
                    flower[targetRow][FLOWER_SIZE - 1 - targetColumn] = true;
                    flower[FLOWER_SIZE - 1 - targetRow][FLOWER_SIZE - 1 - targetColumn] = true;
                }
            }
        }

        flower[4][4] = true;
        return flower;
    }

    private static int letterCode(char letter) {
        int index = UKRAINIAN_ALPHABET.indexOf(letter);
        if (index >= 0) {
            return index;
        }
        if (letter >= 'A' && letter <= 'Z') {
            return letter - 'A' + 1;
        }
        if (letter >= '0' && letter <= '9') {
            return letter - '0' + 24;
        }
        return 0;
    }

    private static void draw(Color[][] cells, boolean[][] shape, int rowOffset, int columnOffset, Color color) {
        for (int row = 0; row < shape.length; row++) {
            for (int column = 0; column < shape[row].length; column++) {
                if (shape[row][column]) {
                    cells[rowOffset + row][columnOffset + column] = color;
                }
            }
        }
    }

    private static void fill(Color[][] cells, Color color) {
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                cells[row][column] = color;
            }
        }
    }

    enum LayoutVariant {
        DIAGONAL_THEN_VERTICAL("1. Flower, diagonal, vertical", true, 1),
        VERTICAL_THEN_DIAGONAL("2. Flower, vertical, diagonal", false, 1),
        REVERSED_DIAGONAL_THEN_VERTICAL("3. Reversed diagonal first", true, -1),
        REVERSED_VERTICAL_THEN_DIAGONAL("4. Reversed vertical first", false, -1);

        private final String label;
        private final boolean startsWithDiagonal;
        private final int diagonalDirection;

        LayoutVariant(String label, boolean startsWithDiagonal, int diagonalDirection) {
            this.label = label;
            this.startsWithDiagonal = startsWithDiagonal;
            this.diagonalDirection = diagonalDirection;
        }

        boolean startsWithDiagonal() {
            return startsWithDiagonal;
        }

        int diagonalDirection() {
            return diagonalDirection;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    record GeneratedPattern(Color[][] cells) {
        int rows() {
            return cells.length;
        }

        int columns() {
            return cells[0].length;
        }
    }

    private record PlacedMotif(char letter, int row, int column, Color color, boolean flower) {
        int size() {
            return flower ? FLOWER_SIZE : MOTIF_SIZE;
        }
    }

    private record Offset(int row, int column) {
    }

    private record Bounds(int minRow, int maxRow, int minColumn, int maxColumn) {
        static Bounds from(List<PlacedMotif> motifs) {
            int minRow = Integer.MAX_VALUE;
            int maxRow = Integer.MIN_VALUE;
            int minColumn = Integer.MAX_VALUE;
            int maxColumn = Integer.MIN_VALUE;

            for (PlacedMotif motif : motifs) {
                minRow = Math.min(minRow, motif.row);
                maxRow = Math.max(maxRow, motif.row + motif.size() - 1);
                minColumn = Math.min(minColumn, motif.column);
                maxColumn = Math.max(maxColumn, motif.column + motif.size() - 1);
            }

            return new Bounds(minRow, maxRow, minColumn, maxColumn);
        }

        int width() {
            return maxColumn - minColumn + 1;
        }

        int height() {
            return maxRow - minRow + 1;
        }
    }
}

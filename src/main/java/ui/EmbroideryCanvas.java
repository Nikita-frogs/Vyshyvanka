package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

class EmbroideryCanvas extends JPanel {
    final Color[][] cells;
    final int cellSize;
    private Color selectedColor = new Color(190, 30, 45);
    private boolean mirrorXEnabled;
    private boolean mirrorYEnabled;

    EmbroideryCanvas(int columns, int rows, int cellSize) {
        this.cells = new Color[rows][columns];
        this.cellSize = cellSize;
        setPreferredSize(new Dimension(columns * cellSize, rows * cellSize));
        fill(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                paintCell(event);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                paintCell(event);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    private void fill(Color color) {
        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                cells[row][column] = color;
            }
        }
    }

    protected void paintCell(MouseEvent event) {
        int column = event.getX() / cellSize;
        int row = event.getY() / cellSize;

        if (isInside(row, column)) {
            paintCellAt(row, column);

            if (mirrorXEnabled) {
                paintCellAt(mirrorIndex(row, cells.length), column);
            }

            if (mirrorYEnabled) {
                paintCellAt(row, mirrorIndex(column, cells[row].length));
            }

            if (mirrorXEnabled && mirrorYEnabled) {
                paintCellAt(mirrorIndex(row, cells.length), mirrorIndex(column, cells[row].length));
            }

            repaint();
        }
    }

    protected void mirrorX() {
        mirrorXEnabled = true;
    }

    protected void mirrorY() {
        mirrorYEnabled = true;
    }

    protected void mirrorXY() {
        mirrorXEnabled = true;
        mirrorYEnabled = true;
    }

    private void paintCellAt(int row, int column) {
        if (isInside(row, column)) {
            cells[row][column] = selectedColor;
        }
    }

    private boolean isInside(int row, int column) {
        return row >= 0 && row < cells.length && column >= 0 && column < cells[row].length;
    }

    private int mirrorIndex(int index, int size) {
        int axis = (size + 1) / 2;
        return 2 * axis - index;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                int x = column * cellSize;
                int y = row * cellSize;

                graphics.setColor(cells[row][column]);
                graphics.fillRect(x, y, cellSize, cellSize);
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    BufferedImage toImage(boolean includeGrid) {
        int width = cells[0].length * cellSize;
        int height = cells.length * cellSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        for (int row = 0; row < cells.length; row++) {
            for (int column = 0; column < cells[row].length; column++) {
                int x = column * cellSize;
                int y = row * cellSize;

                g.setColor(cells[row][column]);
                g.fillRect(x, y, cellSize, cellSize);

                if (includeGrid) {
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(x, y, cellSize, cellSize);
                }
            }
        }

        g.dispose();
        return image;
    }
}

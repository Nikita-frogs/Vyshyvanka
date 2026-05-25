package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class EmbroideryCanvas extends JPanel {
    final Color[][] cells;
    final int cellSize;
    private Color selectedColor = new Color(190, 30, 45);

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

        if (row >= 0 && row < cells.length && column >= 0 && column < cells[row].length) {
            cells[row][column] = selectedColor;
            repaint();
        }
    }

    protected void mirrorX() {

    }

    protected void mirrorY() {

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
}

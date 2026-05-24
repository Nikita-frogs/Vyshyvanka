import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createWindow);
    }

    private static void createWindow() {
        JFrame frame = new JFrame("Vyshyvanka Editor");
        EmbroideryCanvas canvas = new EmbroideryCanvas(24, 16, 28);

        JPanel palette = new JPanel();
        Color[] colors = {
                Color.WHITE,
                Color.BLACK,
                new Color(190, 30, 45),
                new Color(25, 95, 55),
                new Color(245, 185, 65)
        };

        for (Color color : colors) {
            JButton button = new JButton();
            button.setBackground(color);
            button.setPreferredSize(new Dimension(36, 36));
            button.addActionListener(event -> canvas.setSelectedColor(color));
            palette.add(button);
        }

        frame.setLayout(new BorderLayout());
        frame.add(palette, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static class EmbroideryCanvas extends JPanel {
        private final Color[][] cells;
        private final int cellSize;
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

        private void paintCell(MouseEvent event) {
            int column = event.getX() / cellSize;
            int row = event.getY() / cellSize;

            if (row >= 0 && row < cells.length && column >= 0 && column < cells[row].length) {
                cells[row][column] = selectedColor;
                repaint();
            }
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
}

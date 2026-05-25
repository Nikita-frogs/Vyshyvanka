package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createWindow);
    }

    public static JFrame frame = new JFrame("Vyshyvanka Editor");
    public static EmbroideryCanvas canvas = new EmbroideryCanvas(30, 30, 14);

    protected static void createWindow() {

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

        JButton mirrorXButton = new JButton("Mirror X");
        mirrorXButton.addActionListener(event -> canvas.mirrorX());
        palette.add(mirrorXButton);

        JButton mirrorYButton = new JButton("Mirror Y");
        mirrorYButton.addActionListener(event -> canvas.mirrorY());
        palette.add(mirrorYButton);

        JButton mirrorXYButton = new JButton("Mirror XY");
        mirrorXYButton.addActionListener(event -> canvas.mirrorXY());
        palette.add(mirrorXYButton);

        frame.setLayout(new BorderLayout());
        frame.add(palette, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

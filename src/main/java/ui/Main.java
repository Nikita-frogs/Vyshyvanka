package ui;

import serialization.EmbroideryPattern;
import serialization.PatternSerializer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
    private static final PatternSerializer patternSerializer = new PatternSerializer();

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

        JButton exportPngButton = new JButton("Export PNG");
        exportPngButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                try {
                    BufferedImage image = canvas.toImage(false);
                    ImageIO.write(image, "png", file);
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(frame, "Could not export PNG.");
                }
            }
        });

        JButton exportJsonButton = new JButton("Export JSON");
        exportJsonButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    patternSerializer.save(canvas.toPattern(), chooser.getSelectedFile().toPath());
                } catch (IOException | IllegalArgumentException exception) {
                    JOptionPane.showMessageDialog(frame, "Could not export JSON.");
                }
            }
        });

        JButton importJsonButton = new JButton("Import JSON");
        importJsonButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();

            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    EmbroideryPattern pattern = patternSerializer.load(chooser.getSelectedFile().toPath());
                    replaceCanvas(pattern);
                } catch (IOException | IllegalArgumentException exception) {
                    JOptionPane.showMessageDialog(frame, "Could not import JSON.");
                }
            }
        });

        palette.add(exportPngButton);
        palette.add(exportJsonButton);
        palette.add(importJsonButton);

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

    private static void replaceCanvas(EmbroideryPattern pattern) {
        frame.remove(canvas);
        canvas = new EmbroideryCanvas(pattern.columns(), pattern.rows(), canvas.cellSize);
        canvas.loadPattern(pattern);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.revalidate();
        frame.repaint();
    }
}

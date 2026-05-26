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
import javax.swing.event.ChangeListener;

public class Main {
    private static final PatternSerializer patternSerializer = new PatternSerializer();
    private static JSpinner columnsSpinner;
    private static JSpinner rowsSpinner;
    private static JSpinner cellSizeSpinner;
    private static boolean updatingGridControls;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createWindow);
    }

    public static JFrame frame = new JFrame("Vyshyvanka Editor");
    public static EmbroideryCanvas canvas = new EmbroideryCanvas(30, 30, 14);

    protected static void createWindow() {
        frame.setJMenuBar(createMenuBar());

        JPanel palette = new JPanel();
        Color[] colors = {
                Color.WHITE,
                Color.BLACK,
                new Color(190, 30, 45),
                new Color(25, 95, 55),
                new Color(245, 185, 65),
                new Color(32, 92, 175),
                new Color(102, 48, 145),
                new Color(238, 125, 49),
                new Color(238, 68, 103),
                new Color(95, 180, 80),
                new Color(33, 150, 165),
                new Color(128, 70, 38),
                new Color(170, 170, 170),
                new Color(255, 230, 190),
                new Color(115, 20, 35),
                new Color(12, 45, 35)
        };

        for (Color color : colors) {
            JButton button = new JButton();
            button.setBackground(color);
            button.setPreferredSize(new Dimension(36, 36));
            button.addActionListener(event -> canvas.setSelectedColor(color));
            palette.add(button);
        }

        palette.add(new JLabel("Columns"));
        columnsSpinner = createGridSpinner(canvas.getColumns(), 1, 200);
        palette.add(columnsSpinner);

        palette.add(new JLabel("Rows"));
        rowsSpinner = createGridSpinner(canvas.getRows(), 1, 200);
        palette.add(rowsSpinner);

        palette.add(new JLabel("Cell"));
        cellSizeSpinner = createGridSpinner(canvas.getCellSize(), 4, 60);
        palette.add(cellSizeSpinner);

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

    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem importJsonItem = new JMenuItem("Import JSON");
        importJsonItem.addActionListener(event -> importJson());
        fileMenu.add(importJsonItem);

        JMenuItem exportJsonItem = new JMenuItem("Export JSON");
        exportJsonItem.addActionListener(event -> exportJson());
        fileMenu.add(exportJsonItem);

        JMenuItem exportPngItem = new JMenuItem("Export PNG");
        exportPngItem.addActionListener(event -> exportPng());
        fileMenu.add(exportPngItem);

        menuBar.add(fileMenu);
        return menuBar;
    }

    private static JSpinner createGridSpinner(int value, int minimum, int maximum) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, minimum, maximum, 1));
        spinner.setPreferredSize(new Dimension(64, 28));

        ChangeListener listener = event -> applyGridSettings();
        spinner.addChangeListener(listener);
        return spinner;
    }

    private static void applyGridSettings() {
        if (updatingGridControls || columnsSpinner == null || rowsSpinner == null || cellSizeSpinner == null) {
            return;
        }

        canvas.resizeGrid(
                (Integer) columnsSpinner.getValue(),
                (Integer) rowsSpinner.getValue(),
                (Integer) cellSizeSpinner.getValue()
        );
        frame.pack();
    }

    private static void exportPng() {
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
    }

    private static void exportJson() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                patternSerializer.save(canvas.toPattern(), chooser.getSelectedFile().toPath());
            } catch (IOException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(frame, "Could not export JSON.");
            }
        }
    }

    private static void importJson() {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                EmbroideryPattern pattern = patternSerializer.load(chooser.getSelectedFile().toPath());
                replaceCanvas(pattern);
            } catch (IOException | IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(frame, "Could not import JSON.");
            }
        }
    }

    private static void replaceCanvas(EmbroideryPattern pattern) {
        canvas.resizeGrid(pattern.columns(), pattern.rows(), canvas.getCellSize());
        canvas.loadPattern(pattern);
        updateGridControls();
        frame.pack();
    }

    private static void updateGridControls() {
        if (columnsSpinner == null || rowsSpinner == null || cellSizeSpinner == null) {
            return;
        }

        updatingGridControls = true;
        columnsSpinner.setValue(canvas.getColumns());
        rowsSpinner.setValue(canvas.getRows());
        cellSizeSpinner.setValue(canvas.getCellSize());
        updatingGridControls = false;
    }
}

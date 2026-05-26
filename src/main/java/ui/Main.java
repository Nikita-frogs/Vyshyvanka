package ui;

import serialization.EmbroideryPattern;
import serialization.PatternSerializer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main {
    private static final PatternSerializer patternSerializer = new PatternSerializer();
    private static final int MIN_GENERATED_CELL_SIZE = 6;
    private static final int MAX_GENERATED_CELL_SIZE = 18;
    private static JSpinner columnsSpinner;
    private static JSpinner rowsSpinner;
    private static JSpinner cellSizeSpinner;
    private static JTextField textPatternField;
    private static JComboBox<TextEmbroideryGenerator.LayoutVariant> patternVariantBox;
    private static boolean updatingGridControls;

    /**
     * When the text field exactly matches a known name from the library, this flag
     * is set so we don't also run the generative text algorithm on top of it.
     */
    private static boolean namedPatternLoaded = false;

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

        textPatternField = new JTextField(16);
        textPatternField.setToolTipText("Type a Ukrainian name (e.g. АННА or ANNA) to load its embroidery pattern");
        textPatternField.addActionListener(event -> handleTextInput());
        textPatternField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { handleTextInput(); }
            @Override public void removeUpdate(DocumentEvent e) { handleTextInput(); }
            @Override public void changedUpdate(DocumentEvent e) { handleTextInput(); }
        });
        textPatternField.setText("Микита");
        palette.add(textPatternField);

        patternVariantBox = new JComboBox<>(TextEmbroideryGenerator.LayoutVariant.values());
        patternVariantBox.addActionListener(event -> handleTextInput());
        palette.add(patternVariantBox);

        JButton stitchTextButton = new JButton("Stitch text");
        stitchTextButton.addActionListener(event -> handleTextInput());
        palette.add(stitchTextButton);

        frame.setLayout(new BorderLayout());
        frame.add(palette, BorderLayout.NORTH);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // -----------------------------------------------------------------------
    // Text input handling
    // -----------------------------------------------------------------------

    /**
     * Called on every keystroke in the text field.
     * <ol>
     *   <li>If the text exactly matches a name in {@link NamePatternLibrary},
     *       load that pre-generated JSON pattern.</li>
     *   <li>Otherwise fall back to the generative {@link TextEmbroideryGenerator}
     *       exactly as before.</li>
     * </ol>
     */
    private static void handleTextInput() {
        String text = textPatternField.getText();

        EmbroideryPattern named = NamePatternLibrary.findPattern(text);
        if (named != null) {
            namedPatternLoaded = true;
            replaceCanvas(named);
            // Tint the field green to signal a library match
            textPatternField.setBackground(new Color(200, 255, 200));
        } else {
            namedPatternLoaded = false;
            textPatternField.setBackground(Color.WHITE);
            stitchTextPattern();
        }
    }

    /** Generative path — unchanged from original. */
    private static void stitchTextPattern() {
        TextEmbroideryGenerator.LayoutVariant variant =
                (TextEmbroideryGenerator.LayoutVariant) patternVariantBox.getSelectedItem();
        TextEmbroideryGenerator.GeneratedPattern pattern =
                TextEmbroideryGenerator.generate(textPatternField.getText(), variant);

        canvas.replaceCells(pattern.cells(), fittedCellSize(pattern));
        updateGridControls();
        frame.pack();
    }

    // -----------------------------------------------------------------------
    // Menu bar
    // -----------------------------------------------------------------------

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

        JMenu nameMenu = new JMenu("Riznyk Nikita");

        menuBar.add(nameMenu);
        menuBar.add(fileMenu);
        return menuBar;
    }

    // -----------------------------------------------------------------------
    // Grid helpers
    // -----------------------------------------------------------------------

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

    // -----------------------------------------------------------------------
    // File I/O
    // -----------------------------------------------------------------------

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

    // -----------------------------------------------------------------------
    // Canvas helpers
    // -----------------------------------------------------------------------

    private static int fittedCellSize(TextEmbroideryGenerator.GeneratedPattern pattern) {
        Dimension available = frame.getContentPane().getSize();
        Insets insets = frame.getInsets();
        int toolbarHeight = frame.getJMenuBar() == null ? 0 : frame.getJMenuBar().getHeight();
        int availableWidth  = Math.max(360, available.width  - insets.left - insets.right  - 20);
        int availableHeight = Math.max(360, available.height - toolbarHeight - insets.top  - insets.bottom - 90);
        int cellSize = Math.min(availableWidth / pattern.columns(), availableHeight / pattern.rows());
        return Math.max(MIN_GENERATED_CELL_SIZE, Math.min(MAX_GENERATED_CELL_SIZE, cellSize));
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

package ui;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.*;

public class SymetryTest {
    public EmbroideryCanvas canvas;
    public MouseEvent e1;
    public int row;
    public int column;

    @BeforeEach
    public void setUp() {
        canvas = new EmbroideryCanvas(31, 31, 28);
        canvas.setSelectedColor(Color.BLACK);
        e1 = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 794, 450, 1, false, MouseEvent.BUTTON1);
        column = e1.getX() / canvas.cellSize;
        row = e1.getY() / canvas.cellSize;
    }

    @Test
    public void testYSymetry() {
        canvas.mirrorY();
        canvas.paintCell(e1);
        assertEquals(canvas.cells[row][column], canvas.cells[row][2 * 16 - column]);
    }

    @Test
    public void testXSymetry() {
        canvas.mirrorX();
        canvas.paintCell(e1);
        assertEquals(canvas.cells[row][column], canvas.cells[2 * 16 - row][column]);
    }

    @Test
    public void testXYSymetry() {
        canvas.mirrorXY();
        canvas.paintCell(e1);
        assertEquals(canvas.cells[row][column], canvas.cells[2 * 16 - row][2 * 16 - column]);
    }
}

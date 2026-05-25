package ui;

import org.junit.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ui.Main.*;

public class CanvaTest {
    public static EmbroideryCanvas canvas = new EmbroideryCanvas(30, 30, 28);
    public static MouseEvent e1;
    public static MouseEvent e2;

    @BeforeEach
    public void setUp() {
        createWindow();
        canvas.setSelectedColor(Color.BLACK);
        e1 = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 794, 450, 1, false, MouseEvent.BUTTON1);
        e2 = new MouseEvent(canvas, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 794, 570, 0, false, MouseEvent.BUTTON1);
    }

    @Test
    public void testPushCellFill() {
        canvas.paintCell(e1);
        int column = e1.getX() / canvas.cellSize;
        int row = e1.getY() / canvas.cellSize;
        assertEquals(Color.BLACK, canvas.cells[row][column]);
    }

    @Test
    public void testDragCellFill() {
        canvas.paintCell(e2);
        int column = e2.getX() / canvas.cellSize;
        int row = e2.getY() / canvas.cellSize;
        assertEquals(Color.BLACK, canvas.cells[row][column]);
    }
}

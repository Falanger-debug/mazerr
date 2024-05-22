package com.falanger.mazerr;

import com.falanger.mazerr.Maze.Cell;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CellTest {

    @Test
    public void testToJson() {
        Cell cell = new Cell(1, 2);
        cell.setTop(false);
        cell.setBottom(true);
        cell.setLeft(true);
        cell.setRight(false);
        cell.setEntry(true);
        cell.setExit(false);

        String expected = "{\"x\":1,\"y\":2,\"top\":false,\"bottom\":true,\"left\":true,\"right\":false,\"entry\":true,\"exit\":false}";
        assertEquals(expected, cell.toJson());
    }

    @Test
    public void testGettersAndSetters(){
        Cell cell = new Cell(1, 2);
        cell.setVisited(true);
        cell.setTop(false);
        cell.setBottom(true);
        cell.setLeft(true);
        cell.setRight(false);
        cell.setEntry(true);
        cell.setExit(false);

        assertTrue(cell.isVisited());
        assertFalse(cell.isTop());
        assertTrue(cell.isBottom());
        assertTrue(cell.isLeft());
        assertFalse(cell.isRight());
        assertTrue(cell.isEntry());
        assertFalse(cell.isExit());
    }
}

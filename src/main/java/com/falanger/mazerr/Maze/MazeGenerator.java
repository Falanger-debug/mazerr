package com.falanger.mazerr.Maze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MazeGenerator.class);
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };

    private int width;
    private int height;
    private Cell[][] maze;
    private WebSocketSession session;
    private Random random = new Random();
    private Cell entryCell;
    private Cell exitCell;

    public MazeGenerator(WebSocketSession session, int width, int height) {
        this.session = session;
        this.width = width;
        this.height = height;
        maze = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = new Cell(x, y);
            }
        }
    }

    public void generateMaze() throws Exception {
        logger.info("Maze generation started");
        setRandomEntryAndExit();
        Stack<Cell> stack = new Stack<>();
        entryCell.visited = true;
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = getUnvisitedNeighbors(current);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                removeWall(current, next);
                next.visited = true;
                stack.push(next);
                sendMazeState(); // Wyślij aktualny stan labiryntu
                Thread.sleep(500); // Opóźnienie 500 milisekund
            }
        }
        logger.info("Maze generation completed");
    }

    private void setRandomEntryAndExit() {
        entryCell = getRandomEdgeCell();
        exitCell = getRandomEdgeCell();
        while (exitCell == entryCell) {
            exitCell = getRandomEdgeCell();
        }
        entryCell.entry = true;
        exitCell.exit = true;
        removeEdgeWall(entryCell);
        removeEdgeWall(exitCell);
    }

    private Cell getRandomEdgeCell() {
        int edge = random.nextInt(4);
        int x = 0, y = 0;
        switch (edge) {
            case 0: // Top edge
                x = random.nextInt(width);
                y = 0;
                break;
            case 1: // Right edge
                x = width - 1;
                y = random.nextInt(height);
                break;
            case 2: // Bottom edge
                x = random.nextInt(width);
                y = height - 1;
                break;
            case 3: // Left edge
                x = 0;
                y = random.nextInt(height);
                break;
        }
        return maze[y][x];
    }

    private void removeEdgeWall(Cell cell) {
        if (cell.y == 0) {
            cell.top = false;
        } else if (cell.x == width - 1) {
            cell.right = false;
        } else if (cell.y == height - 1) {
            cell.bottom = false;
        } else if (cell.x == 0) {
            cell.left = false;
        }
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int nx = cell.x + direction[0];
            int ny = cell.y + direction[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height && !maze[ny][nx].visited) {
                neighbors.add(maze[ny][nx]);
            }
        }
        return neighbors;
    }

    private void removeWall(Cell a, Cell b) {
        int dx = b.x - a.x;
        int dy = b.y - a.y;

        if (dx == 1) {
            a.right = false;
            b.left = false;
        } else if (dx == -1) {
            a.left = false;
            b.right = false;
        } else if (dy == 1) {
            a.bottom = false;
            b.top = false;
        } else if (dy == -1) {
            a.top = false;
            b.bottom = false;
        }
    }

    private void sendMazeState() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int y = 0; y < height; y++) {
            sb.append("[");
            for (int x = 0; x < width; x++) {
                sb.append(maze[y][x].toJson());
                if (x < width - 1) sb.append(",");
            }
            sb.append("]");
            if (y < height - 1) sb.append(",");
        }
        sb.append("]");
        logger.info("Sending maze state:\n" + sb.toString());
        session.sendMessage(new TextMessage(sb.toString()));
    }

    class Cell {
        int x, y;
        boolean visited = false;
        boolean top = true, bottom = true, left = true, right = true;
        boolean entry = false;
        boolean exit = false;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        String toJson() {
            return String.format(
                    "{\"x\":%d,\"y\":%d,\"top\":%b,\"bottom\":%b,\"left\":%b,\"right\":%b,\"entry\":%b,\"exit\":%b}",
                    x, y, top, bottom, left, right, entry, exit
            );
        }
    }
}

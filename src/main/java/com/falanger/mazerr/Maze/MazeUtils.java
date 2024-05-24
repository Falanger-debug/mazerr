package com.falanger.mazerr.Maze;


import org.slf4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

public class MazeUtils {

    static Cell getRandomEdgeCell(Cell[][] maze, int width, int height, Random random) {
        int edge = random.nextInt(4);
        int x = 0, y = 0;
        switch (edge) {
            case 0 -> x = random.nextInt(width); // Top edge
            case 1 -> { // Right edge
                x = width - 1;
                y = random.nextInt(height);
            }
            case 2 -> { // Bottom edge
                x = random.nextInt(width);
                y = height - 1;
            }
            case 3 -> y = random.nextInt(height); // Left edge
        }
        return maze[y][x];
    }

    public static void setRandomEntryAndExit(Cell[][] maze, int width, int height, Random random, MazeGenerator generator) {
        Cell entryCell = MazeUtils.getRandomEdgeCell(maze, width, height, random);
        Cell exitCell = MazeUtils.getRandomEdgeCell(maze, width, height, random);
        while (exitCell == entryCell) {
            exitCell = MazeUtils.getRandomEdgeCell(maze, width, height, random);
        }
        entryCell.entry = true;
        exitCell.exit = true;
        entryCell.removeEdgeWall(width, height);
        exitCell.removeEdgeWall(width, height);
        generator.setEntryCell(entryCell);
        generator.setExitCell(exitCell);
    }

    public static List<Cell> getNeighbors(Cell currentCell, Cell[][] maze, int width, int height, boolean alreadyChecked) {
        List<Cell> neighbors = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            int nx = currentCell.x + dir.getDeltaX();
            int ny = currentCell.y + dir.getDeltaY();

            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                Cell neighbor = maze[ny][nx];
                if (!neighbor.visited) {
                    if (alreadyChecked) {
                        if (!currentCell.isWallBetween(neighbor)) {
                            neighbors.add(neighbor);
                        }
                    } else {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }

    public static void sendMazeState(Cell[][] maze, int width, int height, WebSocketSession session) throws
            Exception {
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
        session.sendMessage(new TextMessage(sb.toString()));
    }

    public static void sendSolutionPath(List<Cell> solutionPath, WebSocketSession session, Object... options) throws
            Exception {
        Logger logger = null;
        for (Object option : options) {
            if (option instanceof Logger) {
                logger = (Logger) option;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Cell cell : solutionPath) {
            sb.append(String.format("{\"x\":%d,\"y\":%d}", cell.x, cell.y));
            if (solutionPath.indexOf(cell) < solutionPath.size() - 1) sb.append(",");
        }
        sb.append("]");
        if (logger != null) {
            logger.info("Sending solution path\n");
        }

        session.sendMessage(new TextMessage(sb.toString()));
    }

    public static void findSolutionPath(Cell[][] maze, int width, int height, Cell entryCell, Cell
            exitCell, List<Cell> solutionPath) {
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> pathMap = new HashMap<>();
        resetVisitedCells(maze, width, height);
        entryCell.visited = true;
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            if (current == exitCell)
                break;

            List<Cell> neighbors = MazeUtils.getNeighbors(current, maze, width, height, true);
            for (Cell neighbor : neighbors) {
                if (!neighbor.visited) {
                    neighbor.visited = true;
                    stack.push(neighbor);
                    pathMap.put(neighbor, current);
                }
            }
        }

        // Path reconstruction
        Cell step = exitCell;
        while (step != null) {
            solutionPath.add(step);
            step = pathMap.get(step);
        }
        Collections.reverse(solutionPath);
    }

    private static void resetVisitedCells(Cell[][] maze, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x].visited = false;
            }
        }
    }
}

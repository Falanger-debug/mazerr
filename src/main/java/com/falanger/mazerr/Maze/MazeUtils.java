package com.falanger.mazerr.Maze;


import org.slf4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static com.falanger.mazerr.Maze.MazeGenerator.DIRECTIONS;

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
        removeEdgeWall(entryCell, width, height);
        removeEdgeWall(exitCell, width, height);
        generator.setEntryCell(entryCell);
        generator.setExitCell(exitCell);
    }


    public static void removeEdgeWall(Cell cell, int width, int height) {
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

    public static List<Cell> getVisitedNeighbors(Cell cell, Cell[][] maze, int width, int height) {
        List<Cell> neighbors = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int nx = cell.x + direction[0];
            int ny = cell.y + direction[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                Cell neighbor = maze[ny][nx];
                if (!neighbor.visited && !isWallBetween(cell, neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    public static List<Cell> getUnvisitedNeighbors(Cell cell, Cell[][] maze, int width, int height) {
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

    public static boolean isWallBetween(Cell a, Cell b) {
        if (a.x == b.x) {
            if (a.y < b.y) {
                return a.bottom || b.top;
            } else {
                return a.top || b.bottom;
            }
        } else if (a.y == b.y) {
            if (a.x < b.x) {
                return a.right || b.left;
            } else {
                return a.left || b.right;
            }
        }
        return true;
    }


    public static void sendMazeState(Cell[][] maze, int width, int height, WebSocketSession session ) throws Exception {
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

    public static void sendSolutionPath(List<Cell> solutionPath, Logger logger, WebSocketSession session) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Cell cell : solutionPath) {
            sb.append(String.format("{\"x\":%d,\"y\":%d}", cell.x, cell.y));
            if (solutionPath.indexOf(cell) < solutionPath.size() - 1) sb.append(",");
        }
        sb.append("]");
        logger.info("Sending solution path\n");
        session.sendMessage(new TextMessage(sb.toString()));
    }

    public static void findSolutionPath(Cell[][] maze, int width, int height, Cell entryCell, Cell exitCell, List<Cell> solutionPath) {
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> pathMap = new HashMap<>();
        resetVisitedCells(maze, width, height);
        entryCell.visited = true;
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            if (current == exitCell)
                break;

            List<Cell> neighbors = MazeUtils.getVisitedNeighbors(current, maze, width, height);
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

    public static void removeWall(Cell a, Cell b) {
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

}

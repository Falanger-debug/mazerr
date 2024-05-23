package com.falanger.mazerr.Maze;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Getter
@Setter
public class MazeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MazeGenerator.class);
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };

    private final int width;
    private final int height;
    private final int speed;
    private final String method;
    private final Cell[][] maze;
    private final WebSocketSession session;
    private final Random random = new Random();
    private Cell entryCell;
    private Cell exitCell;
    private final List<Cell> solutionPath = new ArrayList<>();

    public MazeGenerator(WebSocketSession session, int width, int height, int speed, String method) {
        this.session = session;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.method = method;
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

        if ("Prim".equals(method)) {
            logger.info("Using Prim's algorithm");
            prim();
        } else {
            recursiveBacktracking();
        }

        logger.info("Maze generation completed");
        findSolutionPath();
        sendSolutionPath();
    }

    private void prim() throws Exception {

    }

    private void recursiveBacktracking() throws Exception {
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
                sendMazeState();
                Thread.sleep(speed);
            }
        }
    }

    private void findSolutionPath() {
        Stack<Cell> stack = new Stack<>();
        Map<Cell, Cell> pathMap = new HashMap<>();
        resetVisitedCells();
        entryCell.visited = true;
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

            if (current == exitCell)
                break;

            List<Cell> neighbors = getVisitedNeighbors(current);
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

    private void resetVisitedCells() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x].visited = false;
            }
        }
    }

    private List<Cell> getVisitedNeighbors(Cell cell) {
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

    private boolean isWallBetween(Cell a, Cell b) {
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

    public void setRandomEntryAndExit() {
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

    private List<Cell> getAdjacentWalls(Cell cell) {
        List<Cell> walls = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int nx = cell.x + direction[0];
            int ny = cell.y + direction[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height && !maze[ny][nx].visited) {
                walls.add(maze[ny][nx]);
            }
        }
        return walls;
    }

    private Cell[] getAdjacentCells(Cell wall) {
        Cell[] adjacentCells = new Cell[2];
        int count = 0;

        for (int[] direction : DIRECTIONS) {
            int nx = wall.x + direction[0];
            int ny = wall.y + direction[1];

            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                adjacentCells[count++] = maze[ny][nx];
                if (count == 2) break;
            }
        }

        return adjacentCells;
    }

    private void removeWallBetween(Cell a, Cell b) {
        if (a.x == b.x) {
            if (a.y < b.y) {
                a.bottom = false;
                b.top = false;
            } else {
                a.top = false;
                b.bottom = false;
            }
        } else if (a.y == b.y) {
            if (a.x < b.x) {
                a.right = false;
                b.left = false;
            } else {
                a.left = false;
                b.right = false;
            }
        }
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
        session.sendMessage(new TextMessage(sb.toString()));
    }

    private void sendSolutionPath() throws Exception {
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
}

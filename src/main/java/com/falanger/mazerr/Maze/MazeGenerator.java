package com.falanger.mazerr.Maze;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Getter
@Setter
public class MazeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MazeGenerator.class);

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
    private volatile boolean shouldPause = false;

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
        MazeUtils.setRandomEntryAndExit(maze, width, height, random, this);


        if (shouldPause) {
            synchronized (this) {
                while (shouldPause) {
                    logger.info("Maze generation paused");
                    wait();
                }
            }
        }
        if ("Prim".equals(method)) {
            logger.info("Using Prim's algorithm");
            prim();
        } else if ("Kruskal".equals(method)) {
            logger.info("Using Kruskal's algorithm");
            kruskal();
        }
        else {
            logger.info("Using recursive backtracking algorithm");
            recursiveBacktracking();
        }


        logger.info("Maze generation completed");
        MazeUtils.findSolutionPath(maze, width, height, entryCell, exitCell, solutionPath);
        MazeUtils.sendSolutionPath(solutionPath, session);
    }
    private void kruskal() throws Exception {
        List<Edge> edges = new ArrayList<>();
        DisjointSet ds = new DisjointSet(width * height);

        // Inicjalizacja krawędzi
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width - 1) { // Krawędź pozioma
                    edges.add(new Edge(x, y, x + 1, y, random.nextInt()));
                }
                if (y < height - 1) { // Krawędź pionowa
                    edges.add(new Edge(x, y, x, y + 1, random.nextInt()));
                }
            }
        }

        // Sortowanie krawędzi według wagi
        Collections.sort(edges, Comparator.comparingInt(e -> e.weight));

        for (Edge edge : edges) {
            int cell1 = edge.y1 * width + edge.x1;
            int cell2 = edge.y2 * width + edge.x2;

            if (ds.find(cell1) != ds.find(cell2)) {
                ds.union(cell1, cell2);
                maze[edge.y1][edge.x1].removeWall(maze[edge.y2][edge.x2]);
                maze[edge.y2][edge.x2].visit();
                MazeUtils.sendMazeState(maze, width, height, session);
                pauseIfNeeded();
                Thread.sleep(speed);
            }
        }

        MazeUtils.sendMazeState(maze, width, height, session);
    }


    private void prim() throws Exception {
        Set<Cell> frontier = new HashSet<>(MazeUtils.getAdjacentCells(maze, width, height, entryCell));

        Cell currentCell = entryCell;
        currentCell.visit();


        Cell randomFrontierCell = MazeUtils.getRandomCellFromACollection(frontier);
        if (randomFrontierCell != null) {
            currentCell.removeWall(randomFrontierCell);
            randomFrontierCell.visit();
            frontier.remove(randomFrontierCell);
        }


        while (!frontier.isEmpty()) {
            frontier.addAll(MazeUtils.getAdjacentCells(maze, width, height, randomFrontierCell));

            randomFrontierCell = MazeUtils.getRandomCellFromACollection(frontier);
            if (randomFrontierCell == null) {
                break;
            }

            List<Cell> metNeighbors = MazeUtils.getMetNeighbors(randomFrontierCell, maze, width, height);

            Cell randomNeighbor = MazeUtils.getRandomCellFromACollection(metNeighbors);
            if (randomNeighbor == null) {
                continue;
            }

            randomFrontierCell.visit();
            randomNeighbor.removeWall(randomFrontierCell);
            frontier.remove(randomFrontierCell);

            MazeUtils.sendMazeState(maze, width, height, session);
            pauseIfNeeded();
            Thread.sleep(speed);

        }

        MazeUtils.sendMazeState(maze, width, height, session);
    }


    private void recursiveBacktracking() throws Exception {

        Stack<Cell> stack = new Stack<>();
        entryCell.visit();
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = MazeUtils.getNeighbors(current, maze, width, height, false);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                current.removeWall(next);
                next.visit();
                stack.push(next);
                MazeUtils.sendMazeState(maze, width, height, session);
                pauseIfNeeded();
                Thread.sleep(speed);
            }
        }
    }

    public synchronized void setShouldPause(boolean shouldPause) {
        this.shouldPause = shouldPause;
        if (!shouldPause) {
            notifyAll();
        }
    }

    private synchronized void pauseIfNeeded() throws InterruptedException {
        while (shouldPause) {
            logger.debug("Maze generation paused");
            wait();
        }
    }

}

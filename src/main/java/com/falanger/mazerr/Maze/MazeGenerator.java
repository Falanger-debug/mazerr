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
        } else {
            logger.info("Using recursive backtracking algorithm");
            recursiveBacktracking();
        }


        logger.info("Maze generation completed");
        MazeUtils.findSolutionPath(maze, width, height, entryCell, exitCell, solutionPath);
        MazeUtils.sendSolutionPath(solutionPath, session);
    }

    private void prim() throws Exception {
        Set<Cell> frontier = new HashSet<>(MazeUtils.getAdjacentCells(maze, width, height, entryCell));
        logger.debug("Starting at entry cell: ({}, {})", entryCell.x, entryCell.y);
        logger.debug("Initial frontier set: {}", frontier.size());

        Cell currentCell = entryCell;
        currentCell.visit();
        logger.debug("Visited entry cell: ({}, {})", currentCell.x, currentCell.y);


        Cell randomFrontierCell = MazeUtils.getRandomCellFromACollection(frontier);
        if (randomFrontierCell != null) {
            currentCell.removeWall(randomFrontierCell);
            randomFrontierCell.visit();
            frontier.remove(randomFrontierCell);
            logger.debug("Removed wall between ({}, {}) and ({}, {}), visited", currentCell.x, currentCell.y, randomFrontierCell.x, randomFrontierCell.y);
        }


        while (!frontier.isEmpty()) {
            frontier.addAll(MazeUtils.getAdjacentCells(maze, width, height, randomFrontierCell));
            logger.debug("Frontier expanded to size: {}", frontier.size());

            randomFrontierCell = MazeUtils.getRandomCellFromACollection(frontier);
            if (randomFrontierCell == null) {
                logger.debug("No more frontier cells to process");
                break;
            }

            List<Cell> metNeighbors = MazeUtils.getMetNeighbors(randomFrontierCell, maze, width, height);
            logger.debug("Got {} met neighbors for cell ({}, {})", metNeighbors.size(), randomFrontierCell.x, randomFrontierCell.y);

            Cell randomNeighbor = MazeUtils.getRandomCellFromACollection(metNeighbors);
            if (randomNeighbor == null) {
                logger.debug("No neighbors found for cell ({}, {}), continuing...", randomFrontierCell.x, randomFrontierCell.y);
                continue;
            }

            randomFrontierCell.visit();
            randomNeighbor.removeWall(randomFrontierCell);
            frontier.remove(randomFrontierCell);
            logger.debug("Connecting cell ({}, {}) with cell ({}, {}) and removing wall.", randomFrontierCell.x, randomFrontierCell.y, randomNeighbor.x, randomNeighbor.y);

            MazeUtils.sendMazeState(maze, width, height, session);
            pauseIfNeeded();
            Thread.sleep(speed);

            logger.debug("Current maze state sent, paused if needed, sleeping for {}", speed);
        }

        MazeUtils.sendMazeState(maze, width, height, session);
        logger.debug("Final maze state sent");
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

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
    private volatile boolean shouldStop = false;

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

        while (!shouldStop) {
            if (shouldPause) {
                synchronized (this) {
                    while (shouldPause && !shouldStop) {
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
//            setShouldStop(true);
        }


        logger.info("Maze generation completed");
        MazeUtils.findSolutionPath(maze, width, height, entryCell, exitCell, solutionPath);
        MazeUtils.sendSolutionPath(solutionPath, session);
    }

    private void prim() throws Exception {
        Set<Cell> frontier = new HashSet<>(MazeUtils.getAdjacentCells(maze, width, height, entryCell));
        logger.debug("Initial frontier set: {}", frontier.size());

        Cell currentCell = entryCell;
        currentCell.visit();
        logger.debug("Starting at entry cell: ({}, {})", entryCell.x, entryCell.y);

        while(!MazeUtils.areAllCellVisited(maze,width, height)){
            while(!frontier.isEmpty()){
                logger.debug("Frontier size before selection: {}", frontier.size());
                Cell randomFrontierCell = MazeUtils.getRandomCellFromACollection(frontier);
                assert randomFrontierCell != null;
                logger.debug("Selected random frontier cell: ({}, {})", randomFrontierCell.x, randomFrontierCell.y);
                List<Cell> neighbors = MazeUtils.getNeighbors(randomFrontierCell, maze, width, height, false);
                logger.debug("Neighbors of cell ({}, {}): {}", randomFrontierCell.x, randomFrontierCell.y, neighbors.size());
                Cell randomNeighbor = MazeUtils.getRandomCellFromACollection(neighbors);
                if(randomNeighbor == null){
                    logger.debug("Neighbors list is empty for cell ({}, {}).", randomFrontierCell.x, randomFrontierCell.y);

                    frontier.remove(randomFrontierCell);
                    logger.debug("Removed cell ({}, {}) from frontier", randomFrontierCell.x, randomFrontierCell.y);
                    continue;
                }

                logger.debug("Connecting cell ({}, {}) to cell ({}, {})", randomFrontierCell.x, randomFrontierCell.y, randomNeighbor.x, randomNeighbor.y);

                randomFrontierCell.removeWall(randomNeighbor);
                randomFrontierCell.visit();
                if(neighbors.isEmpty()){
                    frontier.remove(randomFrontierCell);
                    logger.debug("Removed cell ({}, {}) from frontier", randomFrontierCell.x, randomFrontierCell.y);
                }

                currentCell = randomFrontierCell;

                MazeUtils.sendMazeState(maze, width, height, session);
                pauseIfNeeded();
                Thread.sleep(speed);

                frontier.addAll(MazeUtils.getAdjacentCells(maze, width, height, currentCell));
                logger.debug("New cells added to frontier after visiting cell ({}, {})", currentCell.x, currentCell.y);
            }
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
        while (shouldPause && !shouldStop) {
            logger.debug("Maze generation paused");
            wait();
        }
    }

}

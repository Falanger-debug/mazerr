package com.falanger.mazerr.Maze;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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
        // Implementacja Prim's algorithm
    }

    private void recursiveBacktracking() throws Exception {
        Stack<Cell> stack = new Stack<>();
        entryCell.visited = true;
        stack.push(entryCell);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = MazeUtils.getNeighbors(current, maze, width, height, false);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                current.removeWall(next);
                next.visited = true;
                stack.push(next);
                MazeUtils.sendMazeState(maze, width, height, session);
                Thread.sleep(speed);
            }
        }
    }
}

package com.falanger.mazerr.Maze;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final int[][] DIRECTIONS = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
    };

    private Cell[][] maze;
    private WebSocketSession session;
    private Random random = new Random();

    public MazeGenerator(WebSocketSession session) {
        this.session = session;
        maze = new Cell[HEIGHT][WIDTH];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                maze[y][x] = new Cell(x, y);
            }
        }
    }

    public void generateMaze() throws Exception {
        Stack<Cell> stack = new Stack<>();
        Cell start = maze[0][0];
        start.visited = true;
        stack.push(start);

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
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        for (int[] direction : DIRECTIONS) {
            int nx = cell.x + direction[0];
            int ny = cell.y + direction[1];

            if (nx >= 0 && ny >= 0 && nx < WIDTH && ny < HEIGHT && !maze[ny][nx].visited) {
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
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                sb.append(maze[y][x].toString());
            }
            sb.append("\n");
        }
        session.sendMessage(new TextMessage(sb.toString()));
    }

    class Cell {
        int x, y;
        boolean visited = false;
        boolean top = true, bottom = true, left = true, right = true;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return visited ? "1" : "0"; // Lub inna reprezentacja
        }
    }
}

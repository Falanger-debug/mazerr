//package com.falanger.mazerr.Maze.GeneratingMethods;
//
//import java.util.*;
//
//import com.falanger.mazerr.Maze.SingleMaze;
//
//public class RecursiveBacktracking {
//    private SingleMaze maze;
//    private int width;
//    private int height;
//    private static final int[] DX = {0, 1, 0, -1};
//    private static final int[] DY = {-1, 0, 1, 0};
//
//    public RecursiveBacktracking(SingleMaze maze) {
//        this.maze = new SingleMaze(width, height);
//    }
//
//    public int getWidth() {
//        return width;
//    }
//
//    public int getHeight() {
//        return height;
//    }
//
//    public void generateMaze() {
//        SingleMaze maze = new SingleMaze(width, height);
//        int[][] mazeArray = new int[width][height];
//
////        List<Cell> history = new ArrayList<>();
//        Stack<Cell> stack = new Stack<>();
//
//        Random rand = new Random();
//        int i_start = rand.nextInt(width);
//        int j_start = rand.nextInt(height);
//
//        mazeArray[i_start][j_start] = 1;
//        stack.push(new Cell(i_start, j_start));
////        history.add(new Cell(i_start, j_start));
//
//        while (!stack.isEmpty()) {
//            Cell current = stack.peek();
//            Cell next = getUnvisitedNeighbour(current, mazeArray);
//
//            if(next != null){
//                mazeArray[next.i][next.j] = 1;
//                stack.push(next);
//            }else {
//                stack.pop();
//            }
//        }
//    }
//
//    private Cell getUnvisitedNeighbour(Cell current, int[][] mazeArray) {
//        Integer[] directions = {0, 1, 2, 3};
//        Collections.shuffle(Arrays.asList(directions));
//
//        for (int direction : directions) {
//            int i = current.i + DX[direction] * 2;
//            int j = current.j + DY[direction] * 2;
//
//            if (isInBounds(i, j, width, height) && mazeArray[i][j] == 0) {
//                mazeArray[i][j] = 1;
//                mazeArray[current.i + DX[direction]][current.j + DY[direction]] = 1;
//                return new Cell(i, j);
//            }
//        }
//        return null;
//    }
//
//    private static boolean isInBounds(int x, int y, int width, int height) {
//        return x >= 0 && x < width && y >= 0 && y < height;
//    }
//
//    private static void printMaze(SingleMaze maze) {
//        for (int i = 0; i < maze.getWidth(); i++) {
//            for (int j = 0; j < maze.getHeight(); j++) {
//                System.out.print(maze.getMaze()[i][j] + " ");
//            }
//            System.out.println();
//        }
//    }
//
//    public SingleMaze getMaze() {
//        return ;
//    }
//}

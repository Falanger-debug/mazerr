package com.falanger.mazerr.Maze;


import com.falanger.mazerr.Maze.GeneratingMethods.GeneratingMethods;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleMaze {
    private int width;
    private int height;
    private int[][] maze;
    private GeneratingMethods method;

    public SingleMaze(int width, int height, GeneratingMethods method) {
        this.width = width;
        this.height = height;
        this.maze = new int[width][height];
        this.method = method;
    }

//    void generateMaze() {
//        switch (method) {
//            case PRIM:
//                Prim prim = new Prim(width, height);
//                prim.generateMaze();
//                maze = prim.getMaze();
//                break;
//            case KRUSKAL:
//                Kruskal kruskal = new Kruskal(width, height);
//                kruskal.generateMaze();
//                maze = kruskal.getMaze();
//                break;
//            case RECURSIVE_BACKTRACKING:
//                RecursiveBacktracking recursiveBacktracking = new RecursiveBacktracking(width, height);
//                recursiveBacktracking.generateMaze();
//                maze = recursiveBacktracking.getMaze();
//                break;
//        }
//    }
}

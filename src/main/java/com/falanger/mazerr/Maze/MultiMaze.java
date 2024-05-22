package com.falanger.mazerr.Maze;

import java.util.List;

import com.falanger.mazerr.Maze.GeneratingMethods.GeneratingMethods;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiMaze {
    private int size;
    private int numMazes;
    private List<int[][]> mazes;
    private GeneratingMethods method;


    public MultiMaze(int size, int numMazes, GeneratingMethods method) {
        this.size = size;
        this.numMazes = numMazes;
        this.method = method;
    }

//    public void generateMazes() {
//        for (int i = 0; i < numMazes; i++) {
////            SingleMaze maze = new SingleMaze(size);
////            maze.generateMaze(method);
////            mazes.add(maze.getMaze());
//        }
//    }
//


}

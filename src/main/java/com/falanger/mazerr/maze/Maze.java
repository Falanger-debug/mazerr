package com.falanger.mazerr.maze;


import lombok.Getter;
import lombok.Setter;


public class Maze {
    @Getter
    @Setter
    private int size;
    private boolean[][] mazeBinaryRepresented;

    public Maze(int size){
        this.size = size;
        mazeBinaryRepresented = new boolean[size][size];
        generateMaze();
    }


    private void generateMaze(){
        for(int i = 0; i < size; i ++){
            for(int j = 0; j < size; j++){
                mazeBinaryRepresented[i][j] = i % 2 == 0;
            }
        }
    }


    public void printMaze() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(mazeBinaryRepresented[i][j])
                    System.out.print("██");
                else
                    System.out.print("  ");
            }
            System.out.println();
        }
    }
}

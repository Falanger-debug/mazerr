package com.falanger.mazerr.maze;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MazeRepository {
    private static final Logger logger = LoggerFactory.getLogger(MazeRepository.class);
    private Maze maze;


    public void create(Maze maze){
        maze = new Maze(maze.getSize());
    }


}

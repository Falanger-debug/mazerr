package com.falanger.mazerr.maze;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
public class MazeController {

    @GetMapping("/")
    public RedirectView mainPage(){
        return new RedirectView("main.html");
    }

    @PostMapping("/generate")
    public int[][] generateMaze(@RequestBody Map<String, Integer> body){
        int size = body.get("size");
        int[][] maze = new int[size][size];
        String method = String.valueOf(body.get("method"));
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = 0; // 0 represents an empty space in the maze
            }
        }
        return maze;
    }
}
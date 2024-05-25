package com.falanger.mazerr.Maze;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MazeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
}

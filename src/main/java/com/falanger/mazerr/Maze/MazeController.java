package com.falanger.mazerr.Maze;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MazeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to the Maze Generator!");
        return "index"; // Nazwa widoku Thymeleaf (index.html)
    }
}

package com.falanger.mazerr.Maze;

import com.falanger.mazerr.Maze.GeneratingMethods.GeneratingMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/generate")
public class MazeController {

    private static final Logger logger = LoggerFactory.getLogger(MazeController.class);

    @PostMapping
    public String generateSingle(@RequestParam int width, @RequestParam int height, @RequestParam GeneratingMethods method, Model model){
        logger.info("Generating maze with width: {}, height: {}, method: {}", width, height, method);
        // Możesz dodać tutaj logikę generowania labiryntu, jeśli chcesz
        String responseMessage = "Maze generated with width: " + width + ", height: " + height + ", method: " + method;
        model.addAttribute("message", responseMessage);
        return "generated"; // nazwa szablonu HTML
    }
}

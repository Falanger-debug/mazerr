//package com.falanger.mazerr;
//
//import com.falanger.mazerr.Maze.MazeController;
//import org.junit.jupiter.api.Test;
//import org.springframework.ui.Model;
//import org.springframework.validation.support.BindingAwareModelMap;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class MazeControllerTest {
//
//    @Test
//    public void testHome() {
//        MazeController controller = new MazeController();
//        Model model = new BindingAwareModelMap();
//        String viewName = controller.home(model);
//
//        assertEquals("index", viewName);
//        assertEquals("Welcome to the Maze Generator!", model.getAttribute("message"));
//    }
//}

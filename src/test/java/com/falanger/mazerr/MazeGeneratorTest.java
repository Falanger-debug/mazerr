//package com.falanger.mazerr;
//
//import com.falanger.mazerr.Maze.MazeGenerator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.web.socket.WebSocketSession;
//
//import static org.junit.jupiter.api.Assertions.assertNotEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.mock;
//
//public class MazeGeneratorTest {
//
//    private WebSocketSession session;
//    private MazeGenerator generator;
//
//    @BeforeEach
//    public void setUp(){
//        session = mock(WebSocketSession.class);
//        generator = new MazeGenerator(session, 10, 10, 0, "R_B");
//    }
//
//    @Test
//    public void testGenerateMaze() throws Exception {
//        generator.generateMaze();
//    }
//
//    @Test
//    public void testSetRandomEntryAndExit(){
//        generator.setRandomEntryAndExit();
//        assertNotNull(generator.getEntryCell());
//        assertNotNull(generator.getExitCell());
//        assertNotEquals(generator.getEntryCell(), generator.getExitCell());
//    }
//
//}

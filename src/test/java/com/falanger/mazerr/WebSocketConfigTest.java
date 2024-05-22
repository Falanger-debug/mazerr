package com.falanger.mazerr;

import com.falanger.mazerr.Maze.MazeWebSocketHandler;
import com.falanger.mazerr.Maze.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.mockito.Mockito.*;


public class WebSocketConfigTest {

    @Test
    public void testRegisterWebSocketHandlers() {
        WebSocketConfig config = new WebSocketConfig();
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);

        config.registerWebSocketHandlers(registry);
        verify(registry).addHandler(any(MazeWebSocketHandler.class), eq("/maze-generation"));
    }
}

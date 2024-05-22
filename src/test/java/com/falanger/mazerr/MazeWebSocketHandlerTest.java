package com.falanger.mazerr;

import com.falanger.mazerr.Maze.MazeWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;

import static org.mockito.Mockito.*;

public class MazeWebSocketHandlerTest {

    @Test
    public void testHandleTextMessage() throws Exception {
        MazeWebSocketHandler handler = new MazeWebSocketHandler();
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/maze?size=15&speed=0"));

        TextMessage message = new TextMessage("start");
        handler.handleTextMessage(session, message);

        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }
}

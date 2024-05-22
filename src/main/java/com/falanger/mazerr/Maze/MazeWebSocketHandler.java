package com.falanger.mazerr.Maze;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

public class MazeWebSocketHandler extends TextWebSocketHandler {

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        int size = 20; // Domyślny rozmiar

        if (query != null && query.startsWith("size=")) {
            try {
                size = Integer.parseInt(query.substring(5));
                if (size < 5 || size > 25) {
                    size = 20; // Domyślny rozmiar, jeśli wartość jest poza zakresem
                }
            } catch (NumberFormatException e) {
                // Ignoruj niepoprawne wartości i użyj domyślnego rozmiaru
            }
        }

        MazeGenerator generator = new MazeGenerator(session, size, size);
        generator.generateMaze();
    }
}

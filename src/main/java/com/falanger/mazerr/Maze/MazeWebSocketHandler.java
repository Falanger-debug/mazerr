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
        int speed = 50; // Domyślne tempo

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("size=")) {
                    try {
                        size = Integer.parseInt(param.substring(5));
                        if (size < 5 || size > 40) {
                            size = 20; // Domyślny rozmiar, jeśli wartość jest poza zakresem
                        }
                    } catch (NumberFormatException e) {
                        // Ignoruj niepoprawne wartości i użyj domyślnego rozmiaru
                    }
                } else if (param.startsWith("speed=")) {
                    try {
                        speed = Integer.parseInt(param.substring(6));
                        if (speed < 0 || speed > 2000) {
                            speed = 50; // Domyślne tempo, jeśli wartość jest poza zakresem
                        }
                    } catch (NumberFormatException e) {
                        // Ignoruj niepoprawne wartości i użyj domyślnego tempa
                    }
                }
            }
        }

        MazeGenerator generator = new MazeGenerator(session, size, size, speed);
        generator.generateMaze();
    }
}

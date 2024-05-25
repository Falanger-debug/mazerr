package com.falanger.mazerr.Maze;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MazeWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, MazeGenerator> sessionGenerators = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message){
        String payload = message.getPayload();
        MazeGenerator generator = sessionGenerators.get(session.getId());

        if ("start".equals(payload)) {
            if (generator == null) {
                generator = createMazeGenerator(session);
                sessionGenerators.put(session.getId(), generator);
                MazeGenerator finalGenerator = generator;
                new Thread(() -> {
                    try {
                        finalGenerator.generateMaze();
                    } catch (Exception ignored) {
                    }
                }).start();

            }
        } else if ("pause".equals(payload) && generator != null) {
            generator.setShouldPause(true);
        } else if ("continue".equals(payload) && generator != null) {
            generator.setShouldPause(false);
        }
    }

    private MazeGenerator createMazeGenerator(WebSocketSession session) {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        int size = 15, speed = 0;
        String algorithm = "R_B";

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("size=")) size = Integer.parseInt(param.substring(5));
                else if (param.startsWith("speed=")) speed = Integer.parseInt(param.substring(6));
                else if (param.startsWith("algorithm=")) algorithm = param.substring(10);
            }
        }

        return new MazeGenerator(session, size, size, speed, algorithm);
    }
}

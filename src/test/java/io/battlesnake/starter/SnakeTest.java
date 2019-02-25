package io.battlesnake.starter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.manedev79.SimpleSnake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SnakeTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private SimpleSnake snake;

    @BeforeEach
    void setUp() {
        snake = new SimpleSnake();
    }

    @Test
    void pingTest() {
        Map<String, String> response = snake.ping();
        assertEquals("{}", response.toString());
    }

    @Test
    void startTest() throws IOException {
        JsonNode startRequest = OBJECT_MAPPER.readTree("{}");
        Map<String, String> response = snake.start(startRequest);
        assertEquals("#ff00ff", response.get("color"));
    }

    @Test
    void moveTest() throws IOException {
        JsonNode moveRequest = OBJECT_MAPPER.readTree("{}");
        Map<String, String> response = snake.move(moveRequest);
        assertNotNull(response.get("move"));
    }

    @Test
    void endTest() throws IOException {
        JsonNode endRequest = OBJECT_MAPPER.readTree("{}");
        Map<String, String> response = snake.end(endRequest);
        assertEquals(0, response.size());
    }
}
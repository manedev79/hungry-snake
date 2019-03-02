package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HungrySnakeTest {
    private ObjectMapper mapper = new ObjectMapper();
    private HungrySnake snake;

    @BeforeEach
    void setUp() {
        snake = new HungrySnake();
    }

    @Test
    void moveToFood() throws IOException {
        JsonNode moveRequest = mapper.readTree(from("/hungry-snake-test/moveToFood.json"));
        snake.moveIntoDirection(moveRequest);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void doNotReverseIntoOwnBody() throws IOException {
        JsonNode moveRequest = mapper.readTree(from("/hungry-snake-test/foodRightBehind.json"));
        snake.moveIntoDirection(moveRequest);

        assertNotEquals("down", snake.nextMove);
    }

    private InputStream from(String fileName) {
        return this.getClass().getResourceAsStream(fileName);
    }
}
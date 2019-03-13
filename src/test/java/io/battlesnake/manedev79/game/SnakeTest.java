package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        JsonNode board = JsonNodes.fromFile("/snake-test/singleSnake.json");
        snake = new Snake(board.get("you"));
    }

    @Test
    void snakeBodyWithoutTail() {
        assertEquals(2, snake.bodyWithoutTail.size());
    }

    @Test
    void snakeBody() {
        assertEquals(3, snake.body.size());
    }
}
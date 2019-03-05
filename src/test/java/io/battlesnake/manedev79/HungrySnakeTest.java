package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.battlesnake.manedev79.testutils.JsonNodes;
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
    void moveToFood() {
        JsonNode moveRequest = JsonNodes.fromFile("/hungry-snake-test/moveToFood.json");
        snake.determineNextMove(moveRequest);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void doNotReverseIntoOwnBody() {
        JsonNode moveRequest = JsonNodes.fromFile("/hungry-snake-test/foodRightBehind.json");
        snake.determineNextMove(moveRequest);

        assertNotEquals("down", snake.nextMove);
    }

    @Test
    void avoidCollisionWithOtherSnakes() {
        JsonNode moveRequest = JsonNodes.fromFile("/hungry-snake-test/otherSnakeAhead.json");
        snake.determineNextMove(moveRequest);

        assertNotEquals("down", snake.nextMove);
    }

    @Test
    void killShorterSnake() {
        JsonNode moveRequest = JsonNodes.fromFile("/hungry-snake-test/killShorterSnake.json");
        snake.determineNextMove(moveRequest);

        assertEquals("left", snake.nextMove);
    }

}
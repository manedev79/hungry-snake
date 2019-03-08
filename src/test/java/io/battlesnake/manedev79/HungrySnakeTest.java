package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

class HungrySnakeTest {
    private static final long REQUEST_TIMEOUT = 250L;

    private HungrySnake snake;

    @BeforeEach
    void setUp() {
        snake = new HungrySnake();
    }

    @Test
    void moveToFood() {
        JsonNode board = givenBoard("/hungry-snake-test/moveToFood.json");

        determineMovement(board);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void doNotReverseIntoOwnBody() {
        JsonNode board = givenBoard("/hungry-snake-test/foodRightBehind.json");

        determineMovement(board);

        assertNotEquals("down", snake.nextMove);
    }

    @Test
    void avoidCollisionWithOtherSnakes() {
        JsonNode board = givenBoard("/hungry-snake-test/otherSnakeAhead.json");

        determineMovement(board);

        assertNotEquals("down", snake.nextMove);
    }

    @Test
    void killShorterSnake() {
        JsonNode board = givenBoard("/hungry-snake-test/killShorterSnake.json");

        determineMovement(board);

        assertEquals("left", snake.nextMove);
    }

    @Test
    void avoidWallCrash() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidWallCrash.json");

        determineMovement(board);

        assertEquals("right", snake.nextMove);
    }

    @Test
    void noPathToFood() {
        JsonNode board = givenBoard("/hungry-snake-test/noPathToFood.json");

        determineMovement(board);

        assertEquals("down", snake.nextMove);
    }

    private JsonNode givenBoard(String fileName) {
        return JsonNodes.fromFile(fileName);
    }

    private void determineMovement(JsonNode moveRequest) {
        assertTimeout(ofMillis(REQUEST_TIMEOUT), () -> {
            snake.determineNextMove(moveRequest);
        });
    }
}
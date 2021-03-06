package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Pathfinder;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

class HungrySnakeTest {
    private static final long REQUEST_TIMEOUT = 250L;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Pathfinder pathfinder = new Pathfinder(executorService);
    private HungrySnake snake;

    @BeforeEach
    void setUp() {
        snake = new HungrySnake(pathfinder);
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

    @Disabled("Rather kill than idle")
    @Test
    void chaseTail() {
        JsonNode board = givenBoard("/hungry-snake-test/chasingTail.json");

        determineMovement(board);

        assertEquals("up", snake.nextMove);
    }

    @Test
    void avoidBodyCollision() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidBodyCollision.json");

        determineMovement(board);

        assertNotEquals("left", snake.nextMove);
        assertNotEquals("right", snake.nextMove);
    }

    @Test
    void avoidOwnBodyCollision() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidOwnBodyCollision.json");

        determineMovement(board);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void bewareOfLongerSnake() {
        JsonNode board = givenBoard("/hungry-snake-test/bewareOfLongerSnake.json");

        determineMovement(board);

        assertEquals("left", snake.nextMove);
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
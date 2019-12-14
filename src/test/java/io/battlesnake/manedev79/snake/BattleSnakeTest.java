package io.battlesnake.manedev79.snake;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.AStarPathfinder;
import io.battlesnake.manedev79.game.Pathfinder;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;

class BattleSnakeTest {
    private static final long REQUEST_TIMEOUT = 250L;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Pathfinder pathfinder = new AStarPathfinder(executorService);
    private BattleSnake snake;

    @BeforeEach
    void setUp() {
        snake = new BattleSnake(pathfinder);
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
    void avoidOwnBodyCollision2() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidOwnBodyCollision2.json");

        determineMovement(board);

        assertNotEquals("right", snake.nextMove);
    }


    @Test
    void bewareOfLongerSnake() {
        JsonNode board = givenBoard("/hungry-snake-test/bewareOfLongerSnake.json");

        determineMovement(board);

        assertEquals("left", snake.nextMove);
    }

    @Test
    void bewareOfLongerSnake2() {
        JsonNode board = givenBoard("/hungry-snake-test/bewareOfLongerSnake2.json");

        determineMovement(board);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void bewareOfLongerSnake3() {
        JsonNode board = givenBoard("/hungry-snake-test/bewareOfLongerSnake3.json");

        determineMovement(board);

        assertEquals("left", snake.nextMove);
    }

    @Test
    void avoidDeadEnd() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidDeadEnd.json");

        determineMovement(board);

        assertEquals("down", snake.nextMove);
    }

    @Test
    void avoidDeadEnd2() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidDeadEnd2.json");

        determineMovement(board);

        assertEquals("right", snake.nextMove);
    }

    @Disabled
    @Test
    void avoidDetours() {
        JsonNode board = givenBoard("/hungry-snake-test/avoidDetours.json");

        determineMovement(board);

        assertNotEquals("right", snake.nextMove);
        assertNotEquals("up", snake.nextMove);
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
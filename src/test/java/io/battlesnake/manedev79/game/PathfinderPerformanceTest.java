package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.StopWatch;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertTimeout;

class PathfinderPerformanceTest {
    private static final long REQUEST_TIMEOUT = 250L;
    private static final Field UPPER_LEFT = new Field(0, 0);

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private StopWatch stopWatch;

    private Pathfinder pathfinder;
    private Board board;

    @BeforeEach
    void setUp() {
        stopWatch = new StopWatch();
        pathfinder = new AStarPathfinder(executorService);
    }

    @Test
    void straightPath() {
        board = givenBoard("/pathfinder-performance-test/basicallyEmptyBoard.json");

        stopWatch.start();
        findPath(board, UPPER_LEFT, Field.of(board.maxX, board.maxY));
        stopWatch.stop();
    }

    @Test
    void straightPathReverse() {
        board = givenBoard("/pathfinder-performance-test/basicallyEmptyBoard.json");

        stopWatch.start();
        findPath(board, Field.of(board.maxX, board.maxY), UPPER_LEFT);
        stopWatch.stop();
    }

    @Test
    void straightPathUpperRightToLowerLeft() {
        board = givenBoard("/pathfinder-performance-test/basicallyEmptyBoard.json");

        stopWatch.start();
        findPath(board, Field.of(board.maxX, 0), Field.of(0, board.maxY));
        stopWatch.stop();
    }

    @Test
    void straightPathLowerRightToUpperLeft() {
        board = givenBoard("/pathfinder-performance-test/basicallyEmptyBoard.json");

        stopWatch.start();
        findPath(board,Field.of(0, board.maxY), Field.of(board.maxX, 0));
        stopWatch.stop();
    }

    @Test
    void straightPathLargeBoard() {
        board = givenBoard("/pathfinder-performance-test/basicallyEmptyBoardLarge.json");

        stopWatch.start();
        findPath(board, UPPER_LEFT, Field.of(board.maxX, board.maxY));
        stopWatch.stop();
    }

    @Test
    void onlyOneWayAroundObstacle() {
        board = givenBoard("/pathfinder-performance-test/obstacleOnBoard.json");

        stopWatch.start();
        findPath(board, UPPER_LEFT, Field.of(board.maxX, board.maxY));
        stopWatch.stop();
    }

    @Test
    void onlyOneWayAroundObstacle2() {
        board = givenBoard("/pathfinder-performance-test/obstacleOnBoard2.json");

        stopWatch.start();
        findPath(board, UPPER_LEFT, Field.of(board.maxX, board.maxY));
        stopWatch.stop();
    }

    @Test
    void noPath() {
        board = givenBoard("/pathfinder-performance-test/noPath.json");

        stopWatch.start();
        findPath(board, UPPER_LEFT, Field.of(board.maxX, board.maxY));
        stopWatch.stop();
    }

    private Board givenBoard(String fileName) {
        return Board.of(JsonNodes.fromFile(fileName));
    }

    private void findPath(Board board, Field start, Field destination) {
        assertTimeout(ofMillis(REQUEST_TIMEOUT), () -> {
            pathfinder.findPath(board, start, destination);
        });
    }

}

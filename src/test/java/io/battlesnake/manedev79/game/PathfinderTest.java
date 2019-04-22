package io.battlesnake.manedev79.game;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.FieldAccessor_Double;
import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class PathfinderTest {

    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() {
        pathfinder = new AStarPathfinder(Executors.newSingleThreadExecutor());
    }

    @Test
    void straightPath() {
        Board board = Board.of(JsonNodes.fromFile("/pathfinder-test/emptyBoard.json"));
        assertIterableEquals(asList(new Field(2, 3), new Field(3, 3)),
                pathfinder.findPath(board, new Field(1, 3), new Field(3, 3)).getSteps());
    }

    @Test
    void shortestPathAroundObstacle() {
        Board board = Board.of(JsonNodes.fromFile("/pathfinder-test/boardWithObstacle.json"));
        assertEquals(8, pathfinder.findPath(board, new Field(4, 2), new Field(0, 2)).getSteps().size());
    }

    @Test
    void pathToOwnTail() {
        Board board = Board.of(JsonNodes.fromFile("/pathfinder-test/pathToOwnTail.json"));
        Field start = board.ownSnake.headPosition;
        Field destination = board.ownSnake.tailPosition;

        assertEquals(asList(new Field( 0, 1), new Field(0, 0)), pathfinder.findPath(board, start, destination).getSteps());
    }
}

package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class PathfinderTest {

    private Pathfinder pathfinder;

    @BeforeEach
    void setUp() {
        pathfinder = new Pathfinder();
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
}

package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.of(JsonNodes.fromFile("/board-test/emptyBoard.json"));
    }

    @Test
    void straightPath() {
        assertIterableEquals(asList(new Coordinates(2, 3), new Coordinates(3, 3)),
                board.getPath(new Coordinates(1, 3), new Coordinates(3, 3)).getSteps());
    }

    @Test
    void shortestPathAroundObstacle() {
        board = Board.of(JsonNodes.fromFile("/board-test/boardWithObstacle.json"));
        assertEquals(8, board.getPath(new Coordinates(4, 2), new Coordinates(0, 2)).getSteps().size());
    }

    @Test
    void neighbors() {
        Collection<Coordinates> neighbors = board.getNeighbors(new Coordinates(1, 1));
        assertTrue(neighbors.contains(new Coordinates(1, 0)));
        assertTrue(neighbors.contains(new Coordinates(0, 1)));
        assertTrue(neighbors.contains(new Coordinates(1, 2)));
        assertTrue(neighbors.contains(new Coordinates(2, 1)));
        assertEquals(4, neighbors.size());
    }

    @Test
    void neighborsTopLeft() {
        Collection<Coordinates> neighbors = board.getNeighbors(new Coordinates(0, 0));
        assertTrue(neighbors.contains(new Coordinates(1, 0)));
        assertTrue(neighbors.contains(new Coordinates(0, 1)));
        assertEquals(2, neighbors.size());
    }

    @Test
    void neighborsLowerRight() {
        Collection<Coordinates> neighbors = board.getNeighbors(new Coordinates(14, 14));
        assertTrue(neighbors.contains(new Coordinates(13, 14)));
        assertTrue(neighbors.contains(new Coordinates(14, 13)));
        assertEquals(2, neighbors.size());
    }
}
package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = Board.of(JsonNodes.fromFile("/board-test/emptyBoard.json"));
    }

    @Test
    void neighbors() {
        Collection<Field> neighbors = board.getFreeAdjacentFields(new Field(1, 1));
        assertTrue(neighbors.contains(new Field(1, 0)));
        assertTrue(neighbors.contains(new Field(0, 1)));
        assertTrue(neighbors.contains(new Field(1, 2)));
        assertTrue(neighbors.contains(new Field(2, 1)));
        assertEquals(4, neighbors.size());
    }

    @Test
    void neighborsTopLeft() {
        Collection<Field> neighbors = board.getFreeAdjacentFields(new Field(0, 0));
        assertTrue(neighbors.contains(new Field(1, 0)));
        assertTrue(neighbors.contains(new Field(0, 1)));
        assertEquals(2, neighbors.size());
    }

    @Test
    void neighborsLowerRight() {
        Collection<Field> neighbors = board.getFreeAdjacentFields(new Field(14, 14));
        assertTrue(neighbors.contains(new Field(13, 14)));
        assertTrue(neighbors.contains(new Field(14, 13)));
        assertEquals(2, neighbors.size());
    }
}
package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

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
}
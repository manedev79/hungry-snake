package io.battlesnake.manedev79.game;

import io.battlesnake.manedev79.testutils.JsonNodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LookaheadTest {

    private Lookahead lookahead;

    @BeforeEach
    void setUp() {
        Board board = Board.of(JsonNodes.fromFile("/board-test/emptyBoard.json"));
        lookahead = new Lookahead(board, new DirectWayPathfinder());
    }

    @Test
    void lookaheadContainsSamplePath() {
        List<Path> paths = new ArrayList<>();

        assertTimeout(Duration.ofMillis(100), () -> {
            paths.addAll(lookahead.findPathsFrom(Field.of(8, 8)));
        });

        assertTrue(paths.contains(Path.of(
                Field.of(9, 8),
                Field.of(9, 9),
                Field.of(10, 9),
                Field.of(10, 10),
                Field.of(11, 10),
                Field.of(11, 11),
                Field.of(12, 11))));
    }
}
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
        lookahead = new Lookahead(board);
    }

    @Test
    void findFreePathsInTime() {
        List<Path> paths = new ArrayList<>();

        assertTimeout(Duration.ofMillis(100), () -> {
            paths.addAll(lookahead.findPathsFrom(Field.of(8, 8)));
        });

        assertEquals(paths.size(), 4);
        assertTrue(paths.get(0).getLength() >= Lookahead.SEARCH_DEPTH);
    }
}
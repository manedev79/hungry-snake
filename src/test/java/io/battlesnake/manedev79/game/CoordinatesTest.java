package io.battlesnake.manedev79.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinatesTest {

    private Coordinates coordinates;

    @BeforeEach
    void setUp() {
        coordinates = new Coordinates(1, 1);
    }

    @Test
    void directionUp() {
        assertEquals("up", coordinates.directionTo(new Coordinates(1, 0)));
    }

    @Test
    void directionDown() {
        assertEquals("down", coordinates.directionTo(new Coordinates(1, 2)));
    }

    @Test
    void directionLeft() {
        assertEquals("left", coordinates.directionTo(new Coordinates(0, 1)));
    }

    @Test
    void directionRight() {
        assertEquals("right", coordinates.directionTo(new Coordinates(2, 1)));
    }
}
package io.battlesnake.manedev79.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void directionsInLine() {
        assertEquals(Collections.singleton("down"), coordinates.directionsTo(new Coordinates(1, 3)));
    }

    @Test
    void directionsDiagonal() {
        assertTrue(coordinates.directionsTo(new Coordinates(2, 2)).containsAll(asList("right", "down")));
    }

    @Test
    void distanceDiagonal() {
        assertEquals(2, coordinates.distanceTo(new Coordinates(2, 2)));
    }

    @Test
    void distanceZero() {
        assertEquals(0, coordinates.distanceTo(coordinates));
    }

    @Test
    void distanceNeighbor() {
        assertEquals(1, coordinates.distanceTo(new Coordinates(1, 2)));
        assertEquals(1, coordinates.distanceTo(new Coordinates(0, 1)));
    }
}
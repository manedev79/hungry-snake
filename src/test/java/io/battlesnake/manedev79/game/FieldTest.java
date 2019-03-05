package io.battlesnake.manedev79.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTest {

    private Field field;

    @BeforeEach
    void setUp() {
        field = new Field(1, 1);
    }

    @Test
    void directionUp() {
        assertEquals("up", field.directionTo(new Field(1, 0)));
    }

    @Test
    void directionDown() {
        assertEquals("down", field.directionTo(new Field(1, 2)));
    }

    @Test
    void directionLeft() {
        assertEquals("left", field.directionTo(new Field(0, 1)));
    }

    @Test
    void directionRight() {
        assertEquals("right", field.directionTo(new Field(2, 1)));
    }

    @Test
    void directionsInLine() {
        assertEquals(Collections.singleton("down"), field.directionsTo(new Field(1, 3)));
    }

    @Test
    void directionsDiagonal() {
        assertTrue(field.directionsTo(new Field(2, 2)).containsAll(asList("right", "down")));
    }

    @Test
    void distanceDiagonal() {
        assertEquals(2, field.distanceTo(new Field(2, 2)));
    }

    @Test
    void distanceZero() {
        assertEquals(0, field.distanceTo(field));
    }

    @Test
    void distanceNeighbor() {
        assertEquals(1, field.distanceTo(new Field(1, 2)));
        assertEquals(1, field.distanceTo(new Field(0, 1)));
    }
}
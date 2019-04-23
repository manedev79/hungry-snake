package io.battlesnake.manedev79.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WeightedDirectionsTest {
    private WeightedDirections weightedDirections;

    @BeforeEach
    void setUp() {
        weightedDirections = new WeightedDirections();
    }

    @Test
    void defaultWeight() {
        assertEquals(0, weightedDirections.getHighest().weight);
    }

    @Test
    void containsAllDirections() {
        assertEquals(4, weightedDirections.size());
        assertTrue(weightedDirections.contains(new WeightedDirection("up", 0)));
        assertTrue(weightedDirections.contains(new WeightedDirection("down", 0)));
        assertTrue(weightedDirections.contains(new WeightedDirection("left", 0)));
        assertTrue(weightedDirections.contains(new WeightedDirection("right", 0)));

    }

    @Test
    void doesNotContainIllegalDirection() {
        assertFalse(weightedDirections.contains(new WeightedDirection("middle", 0)));
    }

    @Test
    void updateWeight() {
        weightedDirections.addWeight("up", 10);

        assertEquals(new WeightedDirection("up", 10), weightedDirections.getHighest());
    }

    @Test
    void highestWeight() {
        weightedDirections.addWeight("down", 100);
        weightedDirections.addWeight("left", 100);
        weightedDirections.addWeight("down", 1);

        assertEquals(new WeightedDirection("down", 101), weightedDirections.getHighest());
    }
}
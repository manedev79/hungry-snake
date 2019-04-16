package io.battlesnake.manedev79.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathTest {
    @Test
    void lastStep() {
        Field field = Field.of(1, 2);
        Path path = Path.of(field);

        assertEquals(field, path.getLastStep());
    }

    @Test
    void lastStepPath() {
        Field field = Field.of(1, 2);
        Field lastField = Field.of(1, 3);
        Path path = Path.of(field, lastField);

        assertEquals(lastField, path.getLastStep());
    }

    @Test
    void firstStep() {
        Field field = Field.of(1, 2);
        Path path = Path.of(field);

        assertEquals(field, path.getFirstStep());
    }

    @Test
    void mustNotContainDuplicates() {
        Path path = Path.of(Field.of(1, 2), Field.of(2, 2));
        path = Path.of(path, Field.of(1, 2));

        assertEquals(2, path.getLength());
    }
}
package io.battlesnake.manedev79.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class Path {
    private List<Field> steps = new ArrayList<>();

    private Path(Collection<Field> coordinates) {
        this.steps.addAll(coordinates);
    }

    public static Path of(Field... coordinates) {
        return new Path(asList(coordinates));
    }

    static Path of(Collection<Field> coordinates) {
        return new Path(coordinates);
    }

    public Field getFirstStep() {
        return steps.get(0);
    }

    public List<Field> getSteps() {
        return Collections.unmodifiableList(steps);
    }
}

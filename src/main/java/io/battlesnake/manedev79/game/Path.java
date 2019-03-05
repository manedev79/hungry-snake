package io.battlesnake.manedev79.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class Path {
    private List<Coordinates> steps = new ArrayList<>();

    private Path(Collection<Coordinates> coordinates) {
        this.steps.addAll(coordinates);
    }

    public static Path of(Coordinates... coordinates) {
        return new Path(asList(coordinates));
    }

    static Path of(Collection<Coordinates> coordinates) {
        return new Path(coordinates);
    }

    public Coordinates getFirstStep() {
        return steps.get(0);
    }

    public List<Coordinates> getSteps() {
        return Collections.unmodifiableList(steps);
    }
}

package io.battlesnake.manedev79.game;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_SET;

public class Path {
    @SuppressWarnings("unchecked")
    static final Path NO_PATH = new Path(EMPTY_SET);

    private List<Field> steps = new ArrayList<>();

    private Path(Collection<Field> coordinates) {
        this.steps.addAll(coordinates);
    }

    public static Path of(Field... coordinates) {
        return new Path(asList(coordinates));
    }

    public static Path of(Path somePath, Field field) {
        Path path = new Path(somePath.steps);
        if (!path.steps.contains(field)) {
            path.steps.add(field);
        }
        return path;
    }
    static Path of(Collection<Field> coordinates) {
        return new Path(coordinates);
    }

    public Field getFirstStep() {
        return steps.get(0);
    }

    Field getLastStep() {
        return steps.get(steps.size() - 1);
    }

    public List<Field> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    int getLength() {
        return steps.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return Objects.equals(steps, path.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps);
    }

    @Override
    public String toString() {
        return "Path{" +
                "steps=" + steps +
                '}';
    }
}

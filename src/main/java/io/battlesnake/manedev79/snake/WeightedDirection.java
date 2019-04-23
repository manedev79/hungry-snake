package io.battlesnake.manedev79.snake;

import java.util.Objects;

import static java.lang.Integer.compare;

public class WeightedDirection implements Comparable<WeightedDirection> {

    final String direction;
    final int weight;

    WeightedDirection(String direction, int weight) {
        this.direction = direction;
        this.weight = weight;
    }

    @Override
    public int compareTo(WeightedDirection other) {
        int weightComparison = compare(this.weight, other.weight);
        if (weightComparison == 0) {
            return this.direction.compareTo(other.direction);
        }
        return weightComparison;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightedDirection that = (WeightedDirection) o;
        return weight == that.weight &&
                direction.equals(that.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, weight);
    }

    @Override
    public String toString() {
        return "{" + direction + ", " + weight + "}";
    }
}

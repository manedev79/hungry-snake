package io.battlesnake.manedev79.snake;

import java.util.HashMap;
import java.util.Map;

public class WeightedDirections {
    private Map<String, WeightedDirection> weightedDirections;

    WeightedDirections() {
        weightedDirections = new HashMap<>();
        weightedDirections.put("up", new WeightedDirection("up", 0));
        weightedDirections.put("down", new WeightedDirection("down", 0));
        weightedDirections.put("left", new WeightedDirection("left", 0));
        weightedDirections.put("right", new WeightedDirection("right", 0));
    }

    WeightedDirection getHighest() {
        return weightedDirections.values()
                                 .stream()
                                 .max(WeightedDirection::compareTo)
                                 .orElseThrow(NoWeightedDirectionsException::new);
    }

    boolean contains(WeightedDirection weightedDirection) {
        return weightedDirections.containsKey(weightedDirection.direction) &&
                weightedDirections.containsValue(weightedDirection);
    }

    int size() {
        return weightedDirections.size();
    }

    void addWeight(String direction, int additionalWeight) {
        WeightedDirection weightedDirection = new WeightedDirection(direction, additionalWeight);
        weightedDirections.merge(direction, weightedDirection, this::plus);
    }

    void addWeight(WeightedDirection additionalDirection) {
        weightedDirections.merge(additionalDirection.direction, additionalDirection, this::plus);
    }

    private WeightedDirection plus(WeightedDirection value, WeightedDirection additionalValue) {
        return new WeightedDirection(value.direction, value.weight + additionalValue.weight);
    }

    @Override
    public String toString() {
        return "{" + weightedDirections + '}';
    }

    private class NoWeightedDirectionsException extends RuntimeException {

    }
}

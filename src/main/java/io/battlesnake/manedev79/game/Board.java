package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toList;

public class Board {
    final int maxX;
    final int maxY;
    private final Collection<Field> blockedFields;
    private JsonNode jsonNode;
    public final Snake ownSnake;
    public final Set<Snake> otherSnakes;


    private Board(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
        maxX = jsonNode.get("board").get("width").asInt() - 1;
        maxY = jsonNode.get("board").get("height").asInt() - 1;
        ownSnake = new Snake(jsonNode.get("you"));
        otherSnakes = unmodifiableSet(getOtherSnakes(jsonNode));
        blockedFields = allSnakeBodies();
    }

    public Field middleField() {
        return new Field(maxX / 2, maxY / 2);
    }

    private Collection<Field> allSnakeBodies() {
        Collection<Field> allSnakeBodies = new HashSet<>(ownSnake.bodyWithoutTail);
        otherSnakes.forEach(otherSnake -> allSnakeBodies.addAll(otherSnake.bodyWithoutTail));

        return allSnakeBodies;
    }

    public static Board of(JsonNode jsonNode) {
        return new Board(jsonNode);
    }

    public Collection<Field> getFreeAdjacentFields(Field current) {
        List<Field> neighbors = new ArrayList<>();

        if (current.x > 0) {
            neighbors.add(new Field(current.x - 1, current.y));
        }
        if (current.y > 0) {
            neighbors.add(new Field(current.x, current.y - 1));
        }
        if (current.x < maxX) {
            neighbors.add(new Field(current.x + 1, current.y));
        }
        if (current.y < maxY) {
            neighbors.add(new Field(current.x, current.y + 1));
        }

        return neighbors.stream().filter(it -> !blockedFields.contains(it)).collect(toList());
    }

    public Field closestFoodLocation() {
        Collection<Field> foodLocations = new LinkedList<>();
        jsonNode.get("board").get("food").forEach(food -> foodLocations.add(Field.of(food)));

        return foodLocations.stream()
                            .min(this::compareDistanceFromCurrentPosition)
                            .orElse(middleField());
    }

    private int compareDistanceFromCurrentPosition(Field firstFood, Field secondFood) {
        int distanceToFirst = ownSnake.headPosition.distanceTo(firstFood);
        int distanceToSecond = ownSnake.headPosition.distanceTo(secondFood);
        return Integer.compare(distanceToFirst, distanceToSecond);
    }


    private Set<Snake> getOtherSnakes(JsonNode moveRequest) {
        Set<Snake> otherSnakes = new HashSet<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> {
                    Snake otherSnake = new Snake(snake);
                    if (!otherSnake.equals(ownSnake)) {
                        otherSnakes.add(otherSnake);
                    }
                });
        return otherSnakes;
    }

    public boolean containsFood() {
        return jsonNode.get("board").get("food").size() > 0;
    }
}

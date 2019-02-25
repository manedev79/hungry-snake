package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Collections.singletonList;

public class HungrySnake extends AbstractSnake {
    private Coordinates myPosition;
    private Coordinates someFoodLocation;
    private Collection<String> badDirections = new ArrayList<>();

    @Override
    protected String moveIntoDirection(JsonNode moveRequest) {
        myPosition = myPosition(moveRequest);
        someFoodLocation = someFoodLocation(moveRequest);
        badDirections = checkDirections(moveRequest);

        return moveToFood();
    }

    private Collection<String> checkDirections(JsonNode moveRequest) {
        badDirections = new ArrayList<>();
        badDirections.addAll(directionOfSelf(moveRequest));
        return badDirections;
    }

    private Collection<? extends String> directionOfSelf(JsonNode moveRequest) {
        JsonNode snakeBody = moveRequest.get("you").get("body").get(1);
        String directionOfBody = myPosition.directionTo(Coordinates.of(snakeBody));
        return singletonList(directionOfBody);
    }

    private String moveToFood() {
        Collection<String> intendedDirections = new ArrayList<>();
        if (myPosition.x > someFoodLocation.x) intendedDirections.add("left");
        if (myPosition.x < someFoodLocation.x) intendedDirections.add("right");
        if (myPosition.y < someFoodLocation.y) intendedDirections.add("down");
        if (myPosition.y > someFoodLocation.y) intendedDirections.add("up");

        return intendedDirections.stream()
                .filter(i -> !badDirections.contains(i))
                .findFirst()
                .orElseGet(this::anySaveDirection);
    }

    private String anySaveDirection() {
        Collection<String> allDirections = Arrays.asList("up", "down", "left", "right");
        return allDirections.stream()
                .filter(i -> !badDirections.contains(i))
                .findFirst()
                .orElse("down");
    }

    private Coordinates myPosition(JsonNode moveRequest) {
        JsonNode snakeHead = moveRequest.get("you").get("body").get(0);

        return Coordinates.of(snakeHead);
    }

    private Coordinates someFoodLocation(JsonNode moveRequest) {
        JsonNode firstFood = moveRequest.get("board").get("food").get(0);
        int x = firstFood.get("x").asInt();
        int y = firstFood.get("y").asInt();

        return new Coordinates(x, y);
    }
}

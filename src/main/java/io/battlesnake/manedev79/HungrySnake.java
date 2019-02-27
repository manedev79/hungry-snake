package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class HungrySnake extends AbstractSnake {
    private static final Logger LOG = LoggerFactory.getLogger(HungrySnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private Coordinates myPosition;
    private Collection<String> badDirections = new ArrayList<>();
    private String nextMove = "up";
    private JsonNode moveRequest;

    @Override
    protected String moveIntoDirection(final JsonNode moveRequest) {
        this.moveRequest = moveRequest;
        myPosition = myPosition();
        moveToFood();
        avoidCollision();
        return nextMove;
    }

    private Coordinates myPosition() {
        JsonNode snakeHead = moveRequest.get("you").get("body").get(0);

        return Coordinates.of(snakeHead);
    }

    private void moveToFood() {
        Coordinates someFoodLocation = someFoodLocation(moveRequest);
        Collection<String> intendedDirections = new ArrayList<>();
        if (myPosition.x > someFoodLocation.x) intendedDirections.add("left");
        if (myPosition.x < someFoodLocation.x) intendedDirections.add("right");
        if (myPosition.y < someFoodLocation.y) intendedDirections.add("down");
        if (myPosition.y > someFoodLocation.y) intendedDirections.add("up");

        nextMove = intendedDirections.stream()
                .findFirst()
                .orElse(DEFAULT_DIRECTION);
        LOG.debug("Next move: {}", nextMove);
    }

    private Coordinates someFoodLocation(JsonNode moveRequest) {
        JsonNode firstFood = moveRequest.get("board").get("food").get(0);
        try {
            int x = firstFood.get("x").asInt();
            int y = firstFood.get("y").asInt();

            return new Coordinates(x, y);
        } catch (NullPointerException e) {
            return new Coordinates(0, 0);
        }
    }

    private void avoidSelf() {
        Collection<Coordinates> snakeBody = new ArrayList<>();
        for (JsonNode jsonNode : moveRequest.get("you").get("body")) {
            snakeBody.add(Coordinates.of(jsonNode));
        }
        badDirections.addAll(snakeBody.stream()
                .filter(it -> myPosition.distanceTo(it) == 1)
                .map(it -> myPosition.directionTo(it))
                .collect(Collectors.toSet()));
    }

    private void avoidCollision() {
        badDirections = new ArrayList<>();
        avoidSelf();
        avoidWalls();

        moveSafely();
    }

    private void avoidWalls() {
        int maxX = moveRequest.get("board").get("width").asInt() - 1;
        int maxY = moveRequest.get("board").get("height").asInt() - 1;
        if (myPosition.x <= 0) badDirections.add("left");
        if (myPosition.y <= 0) badDirections.add("up");
        if (myPosition.x >= maxX) badDirections.add("right");
        if (myPosition.y >= maxY) badDirections.add("down");
    }

    private void moveSafely() {
        if (badDirections.contains(nextMove)) {
            nextMove = ALL_DIRECTIONS.stream()
                    .filter(i -> !badDirections.contains(i))
                    .findFirst()
                    .orElse(DEFAULT_DIRECTION);
        }
        LOG.debug("Next move: {}", nextMove);
    }
}

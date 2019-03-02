package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class HungrySnake extends AbstractSnake {
    private static final Logger LOG = LoggerFactory.getLogger(HungrySnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    String nextMove = DEFAULT_DIRECTION;
    private JsonNode moveRequest;
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();
    private SnakeStats ownSnake;

    @Override
    protected String moveIntoDirection(final JsonNode moveRequest) {
        this.moveRequest = moveRequest;
        getMyPosition();
        moveToFood();
        avoidCollision();
        return nextMove;
    }

    private void getMyPosition() {
        JsonNode snakeHead = moveRequest.get("you").get("body").get(0);
        int bodyLength = moveRequest.get("you").get("body").size();
        ownSnake = new SnakeStats(Coordinates.of(snakeHead), bodyLength);
    }

    private void moveToFood() {
        Coordinates someFoodLocation = someFoodLocation(moveRequest);
        Collection<String> intendedDirections = new ArrayList<>();
        if (ownSnake.headPosition.x > someFoodLocation.x) intendedDirections.add("left");
        if (ownSnake.headPosition.x < someFoodLocation.x) intendedDirections.add("right");
        if (ownSnake.headPosition.y < someFoodLocation.y) intendedDirections.add("down");
        if (ownSnake.headPosition.y > someFoodLocation.y) intendedDirections.add("up");

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

    private void avoidCollision() {
        badDirections = new HashSet<>();
        dangerousDirections = new HashSet<>();
        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();

        moveSafely();
    }

    private void avoidSelf() {
        Collection<Coordinates> snakeBody = new HashSet<>();
        for (JsonNode jsonNode : moveRequest.get("you").get("body")) {
            snakeBody.add(Coordinates.of(jsonNode));
        }
        avoidSnakeBody(snakeBody);
    }

    private void avoidSnakeBody(Collection<Coordinates> snakeBody) {
        badDirections.addAll(snakeBody.stream()
                .filter(it -> ownSnake.headPosition.distanceTo(it) == 1)
                .map(it -> ownSnake.headPosition.directionTo(it))
                .collect(Collectors.toSet()));
    }

    private void avoidSnakeHeadCollision() {
        Collection<SnakeStats> snakeStats = new LinkedList<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> snakeStats.add(new SnakeStats(Coordinates.of(snake.get("body").get(0)), snake.get("body").size())));
        HashSet<String> dangerousDirections = snakeStats.stream()
                .filter(it -> ownSnake.headPosition.distanceTo(it.headPosition) == 2)
                .filter(it -> it.length >= ownSnake.length)
                .map(it -> ownSnake.headPosition.directionsTo(it.headPosition))
                .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.badDirections.addAll(dangerousDirections);
        this.dangerousDirections.addAll(dangerousDirections);
    }

    private void avoidWalls() {
        int maxX = moveRequest.get("board").get("width").asInt() - 1;
        int maxY = moveRequest.get("board").get("height").asInt() - 1;
        if (ownSnake.headPosition.x <= 0) badDirections.add("left");
        if (ownSnake.headPosition.y <= 0) badDirections.add("up");
        if (ownSnake.headPosition.x >= maxX) badDirections.add("right");
        if (ownSnake.headPosition.y >= maxY) badDirections.add("down");
    }

    private void avoidOtherSnakes() {
        Collection<Coordinates> snakeBodies = new LinkedList<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> snake.get("body").forEach(
                        element -> snakeBodies.add(Coordinates.of(element))
                )
        );
        avoidSnakeBody(snakeBodies);
    }

    private void moveSafely() {
        if (badDirections.contains(nextMove)) {
            nextMove = ALL_DIRECTIONS.stream()
                    .filter(i -> !badDirections.contains(i))
                    .findFirst()
                    .orElse(dangerousDirections.stream()
                            .findFirst()
                            .orElse(DEFAULT_DIRECTION));
        }
        LOG.debug("Next move: {}", nextMove);
    }

    private static class SnakeStats {
        final Coordinates headPosition;
        final int length;

        SnakeStats(Coordinates headPosition, int length) {

            this.headPosition = headPosition;
            this.length = length;
        }
    }
}

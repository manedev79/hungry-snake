package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Field;
import io.battlesnake.manedev79.game.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class HungrySnake extends AbstractSnake {
    private static final Logger LOG = LoggerFactory.getLogger(HungrySnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private static final Field DEFAULT_LOCATION = new Field(0, 0);
    String nextMove = DEFAULT_DIRECTION;
    private JsonNode moveRequest;
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();
    private SnakeStats ownSnake;
    private Board board;

    @Override
    protected String determineNextMove(final JsonNode moveRequest) {
        this.moveRequest = moveRequest;
        this.board = Board.of(moveRequest);
        getMyPosition();
        moveToFoodByPath();
        avoidCollision();
        return nextMove;
    }

    private void getMyPosition() {
        JsonNode snakeHead = moveRequest.get("you").get("body").get(0);
        int bodyLength = moveRequest.get("you").get("body").size();
        ownSnake = new SnakeStats(Field.of(snakeHead), bodyLength);
    }

    private void moveToFoodByPath() {
        Field foodLocation = closestFoodLocation(moveRequest);
        Path path = board.getPath(ownSnake.headPosition, foodLocation);
        Field nextField = path.getSteps().stream().findFirst().orElse(DEFAULT_LOCATION);
        nextMove = ownSnake.headPosition.directionTo(nextField);
    }

    private Field closestFoodLocation(JsonNode moveRequest) {
        Collection<Field> foodLocations = new LinkedList<>();
        moveRequest.get("board").get("food").forEach(food -> foodLocations.add(Field.of(food)));

        return foodLocations.stream()
                .min(this::compareDistanceToFood)
                .orElse(DEFAULT_LOCATION);
    }

    private int compareDistanceToFood(Field firstFood, Field secondFood) {
        int distanceToFirst = ownSnake.headPosition.distanceTo(firstFood);
        int distanceToSecond = ownSnake.headPosition.distanceTo(secondFood);
        return Integer.compare(distanceToFirst, distanceToSecond);
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
        Collection<Field> snakeBody = new HashSet<>();
        for (JsonNode jsonNode : moveRequest.get("you").get("body")) {
            snakeBody.add(Field.of(jsonNode));
        }
        avoidSnakeBody(snakeBody);
    }

    private void avoidSnakeBody(Collection<Field> snakeBody) {
        badDirections.addAll(snakeBody.stream()
                .filter(it -> ownSnake.headPosition.distanceTo(it) == 1)
                .map(it -> ownSnake.headPosition.directionTo(it))
                .collect(Collectors.toSet()));
    }

    private void avoidSnakeHeadCollision() {
        Collection<SnakeStats> snakeStats = new LinkedList<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> snakeStats.add(new SnakeStats(Field.of(snake.get("body").get(0)), snake.get("body").size())));
        HashSet<String> dangerousDirections = snakeStats.stream()
                .filter(potentiallyCollidingSnakes())
                .filter(equalOrLargerSnakes())
                .map(allPossibleDirections())
                .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.badDirections.addAll(dangerousDirections);
        this.dangerousDirections.addAll(dangerousDirections);
    }

    private Function<SnakeStats, Collection<String>> allPossibleDirections() {
        return it -> ownSnake.headPosition.directionsTo(it.headPosition);
    }

    private Predicate<SnakeStats> equalOrLargerSnakes() {
        return it -> it.length >= ownSnake.length;
    }

    private Predicate<SnakeStats> potentiallyCollidingSnakes() {
        return it -> ownSnake.headPosition.distanceTo(it.headPosition) == 2;
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
        Collection<Field> snakeBodies = new LinkedList<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> snake.get("body").forEach(
                        element -> snakeBodies.add(Field.of(element))
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
        final Field headPosition;
        final int length;

        SnakeStats(Field headPosition, int length) {
            this.headPosition = headPosition;
            this.length = length;
        }
    }
}

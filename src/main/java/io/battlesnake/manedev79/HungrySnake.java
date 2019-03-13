package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

public class HungrySnake implements SnakeAI {
    private static final Logger LOG = LoggerFactory.getLogger(HungrySnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private static final int HUNGRY_THRESHOULD = 50;
    String nextMove = DEFAULT_DIRECTION;
    private JsonNode moveRequest;
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();
    private Collection<String> preferredDirections = new HashSet<>();
    private Snake ownSnake;
    private Board board;
    private Pathfinder pathfinder;
    private Collection<Snake> otherSnakes;

    public HungrySnake(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    @Override
    public String determineNextMove(final JsonNode moveRequest) {
        this.moveRequest = moveRequest;
        board = Board.of(moveRequest);
        ownSnake = new Snake(moveRequest.get("you"));
        otherSnakes = getOtherSnakes(moveRequest);

        if (ownSnake.health < HUNGRY_THRESHOULD || !isLongestSnake(ownSnake)) {
            moveToFood();
        } else {
            followOwnTail();
        }

        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();
        eatTheWeak();

        moveSafely();
        return nextMove;
    }

    private boolean isLongestSnake(Snake ownSnake) {
        return otherSnakes.stream()
                          .filter(otherSnake -> !otherSnake.id.equals(ownSnake.id))
                          .map(otherSnake -> ownSnake.length > otherSnake.length)
                          .reduce(TRUE, Boolean::logicalAnd);
    }

    private void followOwnTail() {
        Path path = pathfinder.findPath(board, ownSnake.headPosition, ownSnake.tailPosition);
        Field nextField = path.getSteps().stream().findFirst().orElse(board.middleField());
        preferredDirections.add(ownSnake.headPosition.directionTo(nextField));
    }

    private void moveToFood() {
        Field foodLocation = closestFoodLocation(moveRequest);
        Path path = pathfinder.findPath(board, ownSnake.headPosition, foodLocation);
        Field nextField = path.getSteps().stream().findFirst().orElse(board.middleField());
        preferredDirections.add(ownSnake.headPosition.directionTo(nextField));
    }

    private Field closestFoodLocation(JsonNode moveRequest) {
        Collection<Field> foodLocations = new LinkedList<>();
        moveRequest.get("board").get("food").forEach(food -> foodLocations.add(Field.of(food)));

        return foodLocations.stream()
                            .min(this::compareDistanceFromCurrentPosition)
                            .orElse(board.middleField());
    }

    private int compareDistanceFromCurrentPosition(Field firstFood, Field secondFood) {
        int distanceToFirst = ownSnake.headPosition.distanceTo(firstFood);
        int distanceToSecond = ownSnake.headPosition.distanceTo(secondFood);
        return Integer.compare(distanceToFirst, distanceToSecond);
    }

    private void avoidSelf() {
        if (ownSnake.length <= 2) {
            avoidSnakeBody(ownSnake.body);
        } else {
            avoidSnakeBody(ownSnake.bodyWithoutTail);
        }
    }

    private void avoidSnakeBody(List<Field> snakeBody) {
        badDirections.addAll(snakeBody.stream()
                                      .filter(it -> ownSnake.headPosition.distanceTo(it) == 1)
                                      .map(it -> ownSnake.headPosition.directionTo(it))
                                      .collect(Collectors.toSet()));
    }

    private void avoidSnakeHeadCollision() {
        HashSet<String> dangerousDirections = otherSnakes.stream()
                                                         .filter(potentiallyCollidingSnakes())
                                                         .filter(equalOrLargerSnakes())
                                                         .map(allPossibleDirections())
                                                         .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.dangerousDirections.addAll(dangerousDirections);
    }


    private void eatTheWeak() {
        HashSet<String> killDirections = otherSnakes.stream()
                                                    .filter(potentiallyCollidingSnakes())
                                                    .filter(smallerSnakes())
                                                    .map(allPossibleDirections())
                                                    .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.preferredDirections.addAll(killDirections);
    }


    private Collection<Snake> getOtherSnakes(JsonNode moveRequest) {
        Collection<Snake> otherSnakes = new LinkedList<>();
        moveRequest.get("board").get("snakes").forEach(
                snake -> {
                    Snake otherSnake = new Snake(snake);
                    if (!otherSnake.equals(ownSnake)) {
                        otherSnakes.add(otherSnake);
                    }
                });
        return otherSnakes;
    }

    private Function<Snake, Collection<String>> allPossibleDirections() {
        return it -> ownSnake.headPosition.directionsTo(it.headPosition);
    }

    private Predicate<Snake> equalOrLargerSnakes() {
        return it -> it.length >= ownSnake.length;
    }

    private Predicate<Snake> smallerSnakes() {
        return equalOrLargerSnakes().negate();
    }

    private Predicate<Snake> potentiallyCollidingSnakes() {
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
        otherSnakes.forEach(it -> avoidSnakeBody(it.bodyWithoutTail));
    }

    private void moveSafely() {
        Collection<String> preferredAndSafeDirections = preferredDirections.stream()
                                                                           .filter(it -> !badDirections.contains(it))
                                                                           .filter(it -> !dangerousDirections.contains(it))
                                                                           .collect(Collectors.toList());
        Collection<String> dangerousButNotFatalDirections = dangerousDirections.stream()
                                                                                .filter(it -> !badDirections.contains(it))
                                                                                .collect(Collectors.toList());
        Collection<String> safeDirections = ALL_DIRECTIONS.stream()
                                                          .filter(it -> !badDirections.contains(it))
                                                          .filter(it -> !dangerousDirections.contains(it))
                                                          .collect(Collectors.toList());

        nextMove = preferredAndSafeDirections.stream()
                                             .findFirst()
                                             .orElse(safeDirections
                                                     .stream()
                                                     .findFirst()
                                                     .orElse(dangerousButNotFatalDirections
                                                             .stream()
                                                             .findFirst()
                                                             .orElse(dangerousDirections
                                                                     .stream()
                                                                     .findFirst()
                                                                     .orElse(DEFAULT_DIRECTION))));
        LOG.debug("Next move: {}", nextMove);
    }
}

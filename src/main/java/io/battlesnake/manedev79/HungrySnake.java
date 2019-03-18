package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

public class HungrySnake implements SnakeAI {
    private static final Logger LOG = LoggerFactory.getLogger(HungrySnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private static final int HUNGRY_THRESHOLD = 50;
    String nextMove = DEFAULT_DIRECTION;
    private JsonNode moveRequest;
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();
    private Collection<String> preferredDirections = new HashSet<>();
    private Board board;
    private Pathfinder pathfinder;

    public HungrySnake(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    @Override
    public String determineNextMove(final JsonNode moveRequest) {
        this.moveRequest = moveRequest;
        board = Board.of(moveRequest);

        if (board.ownSnake.health < HUNGRY_THRESHOLD || !isLongestSnake(board.ownSnake)) {
            moveToFood();
        } else {
            chaseShortestSnake();
        }

        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();
        eatTheWeak();

        moveSafely();
        return nextMove;
    }

    private void chaseShortestSnake() {
        Field shortesSnakeHead = board.otherSnakes.stream().reduce((snake, snake2) -> {
            if (snake.length < snake2.length) {
                return snake;
            } else {
                return snake2;
            }
        }).map(snake -> snake.headPosition).orElse(board.middleField());
        LOG.debug("Shortest snake head is at {}", shortesSnakeHead);

        preferredDirections.addAll(board.ownSnake.headPosition.directionsTo(shortesSnakeHead));
    }

    private boolean isLongestSnake(Snake snake) {
        return board.otherSnakes.stream()
                                .filter(otherSnake -> !otherSnake.id.equals(snake.id))
                                .map(otherSnake -> snake.length > otherSnake.length)
                                .reduce(TRUE, Boolean::logicalAnd);
    }

    private void followOwnTail() {
        Path path = pathfinder.findPath(board, board.ownSnake.headPosition, board.ownSnake.tailPosition);
        Field nextField = path.getSteps().stream().findFirst().orElse(board.middleField());
        preferredDirections.add(board.ownSnake.headPosition.directionTo(nextField));
    }

    private void moveToFood() {
        Field foodLocation = board.closestFoodLocation();
        Path path = pathfinder.findPath(board, board.ownSnake.headPosition, foodLocation);
        Field nextField = path.getSteps().stream().findFirst().orElse(board.middleField());
        preferredDirections.add(board.ownSnake.headPosition.directionTo(nextField));
    }

    private void avoidSelf() {
        if (board.ownSnake.length <= 2) {
            avoidSnakeBody(board.ownSnake.body);
        } else {
            avoidSnakeBody(board.ownSnake.bodyWithoutTail);
        }
    }

    private void avoidSnakeBody(List<Field> snakeBody) {
        badDirections.addAll(snakeBody.stream()
                                      .filter(it -> board.ownSnake.headPosition.distanceTo(it) == 1)
                                      .map(it -> board.ownSnake.headPosition.directionTo(it))
                                      .collect(Collectors.toSet()));
    }

    private void avoidSnakeHeadCollision() {
        HashSet<String> dangerousDirections = board.otherSnakes.stream()
                                                               .filter(potentiallyCollidingSnakes())
                                                               .filter(equalOrLargerSnakes())
                                                               .map(allPossibleDirections())
                                                               .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.dangerousDirections.addAll(dangerousDirections);
    }


    private void eatTheWeak() {
        HashSet<String> killDirections = board.otherSnakes.stream()
                                                          .filter(potentiallyCollidingSnakes())
                                                          .filter(smallerSnakes())
                                                          .map(allPossibleDirections())
                                                          .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.preferredDirections.addAll(killDirections);
    }

    private Function<Snake, Collection<String>> allPossibleDirections() {
        return it -> board.ownSnake.headPosition.directionsTo(it.headPosition);
    }

    private Predicate<Snake> equalOrLargerSnakes() {
        return it -> it.length >= board.ownSnake.length;
    }

    private Predicate<Snake> smallerSnakes() {
        return equalOrLargerSnakes().negate();
    }

    private Predicate<Snake> potentiallyCollidingSnakes() {
        return it -> board.ownSnake.headPosition.distanceTo(it.headPosition) == 2;
    }

    private void avoidWalls() {
        int maxX = moveRequest.get("board").get("width").asInt() - 1;
        int maxY = moveRequest.get("board").get("height").asInt() - 1;
        if (board.ownSnake.headPosition.x <= 0) badDirections.add("left");
        if (board.ownSnake.headPosition.y <= 0) badDirections.add("up");
        if (board.ownSnake.headPosition.x >= maxX) badDirections.add("right");
        if (board.ownSnake.headPosition.y >= maxY) badDirections.add("down");
    }

    private void avoidOtherSnakes() {
        board.otherSnakes.forEach(it -> avoidSnakeBody(it.bodyWithoutTail));
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

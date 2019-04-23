package io.battlesnake.manedev79.snake;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BattleSnake implements SnakeAI {
    static final int HUNGRY_THRESHOLD = 30; // TODO: Verify value
    private static final Logger LOG = LoggerFactory.getLogger(BattleSnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private final SnakeMind snakeMind;
    private final Pathfinder pathfinder;
    String nextMove = DEFAULT_DIRECTION;
    private Board board;
    private Collection<String> preferredDirections = new HashSet<>();
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();

    // Using weighted approach
    private static final int FREE_DIRECTION_WEIGHT = 3;
    private static final int PREFERRED_DIRECTION_WEIGHT = 2;
    private static final int EASY_KILL_DIRECTION_WEIGHT = 1;
    private static final int DANGEROUS_DIRECTION_WEIGHT = -2;
    private static final int FATAL_DIRECTION_WEIGHT = -100;
    private WeightedDirections weightedDirections = new WeightedDirections();

    public BattleSnake(Pathfinder pathfinder) {
        this.snakeMind = new SnakeMind(pathfinder);
        this.pathfinder = pathfinder;
    }

    @Override
    public String determineNextMove(final JsonNode moveRequest) {
        board = Board.of(moveRequest);

        Collection<String> preferredDirections = snakeMind.getPreferredDirections(board);
        this.preferredDirections.addAll(preferredDirections);
        preferredDirections.forEach(direction -> weightedDirections.addWeight(direction, PREFERRED_DIRECTION_WEIGHT));

        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();
        avoidDeadEnds();
        eatTheWeak();

        moveSafely();

        LOG.info("Weighted directions: {}", weightedDirections);
        nextMove = weightedDirections.getHighest().direction;
        return nextMove;
    }

    private void avoidDeadEnds() {
        Lookahead lookahead = new Lookahead(board, pathfinder);

        Set<String> nonDeadendDirections = lookahead.findPathsFrom(board.ownSnake.headPosition).stream()
                                                    .map(path -> board.ownSnake.headPosition.directionTo(path.getFirstStep()))
                                                    .collect(Collectors.toSet());
        LOG.debug("Free directions: {}", nonDeadendDirections);
        nonDeadendDirections.forEach(direction -> weightedDirections.addWeight(direction, FREE_DIRECTION_WEIGHT));
        badDirections.addAll(ALL_DIRECTIONS.stream()
                                           .filter(direction -> !nonDeadendDirections.contains(direction))
                                           .collect(Collectors.toList()));
    }

    private void avoidSelf() {
        if (board.ownSnake.length <= 2) {
            avoidSnakeBody(board.ownSnake.body);
        } else {
            avoidSnakeBody(board.ownSnake.bodyWithoutTail);
        }
    }

    private void avoidSnakeBody(List<Field> snakeBody) {
        Set<String> snakeBodyDirections = snakeBody.stream()
                                       .filter(it -> board.ownSnake.headPosition.distanceTo(it) == 1)
                                       .map(it -> board.ownSnake.headPosition.directionTo(it))
                                       .collect(Collectors.toSet());
        badDirections.addAll(snakeBodyDirections);
        snakeBodyDirections.forEach(direction -> weightedDirections.addWeight(direction, FATAL_DIRECTION_WEIGHT));
    }

    private void avoidSnakeHeadCollision() {
        HashSet<String> dangerousDirections = board.otherSnakes.stream()
                                                               .filter(potentiallyCollidingSnakes())
                                                               .filter(equalOrLargerSnakes())
                                                               .map(allPossibleDirections())
                                                               .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        this.dangerousDirections.addAll(dangerousDirections);
        dangerousDirections.forEach(direction -> weightedDirections.addWeight(direction, DANGEROUS_DIRECTION_WEIGHT));
    }


    private void eatTheWeak() {
        HashSet<String> killDirections = board.otherSnakes.stream()
                                                          .filter(potentiallyCollidingSnakes())
                                                          .filter(smallerSnakes())
                                                          .map(allPossibleDirections())
                                                          .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        killDirections.forEach(direction -> weightedDirections.addWeight(direction, EASY_KILL_DIRECTION_WEIGHT));
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
        if (board.ownSnake.headPosition.x <= 0) {
            badDirections.add("left");
            weightedDirections.addWeight("left", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.y <= 0) {
            badDirections.add("up");
            weightedDirections.addWeight("up", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.x >= board.maxX) {
            badDirections.add("right");
            weightedDirections.addWeight("right", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.y >= board.maxY) {
            badDirections.add("down");
            weightedDirections.addWeight("down", FATAL_DIRECTION_WEIGHT);
        }
    }

    private void avoidOtherSnakes() {
        board.otherSnakes.forEach(it -> avoidSnakeBody(it.bodyWithoutTail));
    }

    private void moveSafely() {
        Collection<String> preferredAndSafeDirections = preferredDirections.stream()
                                                                           .filter(it -> !badDirections.contains(it))
                                                                           .filter(it -> !dangerousDirections.contains(it))
                                                                           .collect(Collectors.toSet());
        Collection<String> dangerousButNotFatalDirections = dangerousDirections.stream()
                                                                               .filter(it -> !badDirections.contains(it))
                                                                               .collect(Collectors.toSet());
        Collection<String> safeDirections = ALL_DIRECTIONS.stream()
                                                          .filter(it -> !badDirections.contains(it))
                                                          .filter(it -> !dangerousDirections.contains(it))
                                                          .collect(Collectors.toSet());
        Collection<String> freeDirections = board.getFreeAdjacentFields(board.ownSnake.headPosition)
                                                 .stream()
                                                 .map(freeField -> board.ownSnake.headPosition.directionTo(freeField))
                                                 .collect(Collectors.toSet());

        nextMove = preferredAndSafeDirections.stream()
                                             .findAny()
                                             .orElse(safeDirections
                                                     .stream()
                                                     .findAny()
                                                     .orElse(dangerousButNotFatalDirections
                                                             .stream()
                                                             .findAny()
                                                             .orElse(dangerousDirections
                                                                     .stream()
                                                                     .findAny()
                                                                     .orElse(freeDirections
                                                                             .stream()
                                                                             .findAny()
                                                                             .orElse(DEFAULT_DIRECTION)))));
        LOG.info("Next move (old way): {}", nextMove);
    }

}

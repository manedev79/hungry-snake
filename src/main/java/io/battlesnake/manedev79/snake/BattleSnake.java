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
    static final int HUNGRY_THRESHOLD = 50;
    private static final Logger LOG = LoggerFactory.getLogger(BattleSnake.class);
    private static final Collection<String> ALL_DIRECTIONS = Arrays.asList("up", "down", "left", "right");
    private static final String DEFAULT_DIRECTION = "up";
    private final SnakeMoodFactory snakeMoodFactory;
    String nextMove = DEFAULT_DIRECTION;
    private Board board;
    private Collection<String> preferredDirections = new HashSet<>();
    private Collection<String> badDirections = new HashSet<>();
    private Collection<String> dangerousDirections = new HashSet<>();

    public BattleSnake(Pathfinder pathfinder) {
        this.snakeMoodFactory = new SnakeMoodFactory(pathfinder);
    }

    @Override
    public String determineNextMove(final JsonNode moveRequest) {
        board = Board.of(moveRequest);

        SnakeMood snakeMood = snakeMoodFactory.determineSnakeMood(board);
        preferredDirections.addAll(snakeMood.provideDirections());

        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();
        avoidDeadEnds();
        eatTheWeak();

        moveSafely();
        return nextMove;
    }

    private void avoidDeadEnds() {
        Lookahead lookahead = new Lookahead(board);

        Set<String> nonDeadendDirections = lookahead.findPathsFrom(board.ownSnake.headPosition).stream()
                                                    .map(path -> board.ownSnake.headPosition.directionTo(path.getFirstStep()))
                                                    .collect(Collectors.toSet());
        LOG.debug("Free directions: {}", nonDeadendDirections);
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
        if (board.ownSnake.headPosition.x <= 0) badDirections.add("left");
        if (board.ownSnake.headPosition.y <= 0) badDirections.add("up");
        if (board.ownSnake.headPosition.x >= board.maxX) badDirections.add("right");
        if (board.ownSnake.headPosition.y >= board.maxY) badDirections.add("down");
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
                                                          .collect(Collectors.toList());
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
        LOG.debug("Next move: {}", nextMove);
    }

}

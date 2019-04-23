package io.battlesnake.manedev79.snake;

import com.fasterxml.jackson.databind.JsonNode;
import io.battlesnake.manedev79.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class BattleSnake implements SnakeAI {
    static final int HUNGRY_THRESHOLD = 30; // TODO: Verify value
    private static final Logger LOG = LoggerFactory.getLogger(BattleSnake.class);
    private static final String DEFAULT_DIRECTION = "up";
    private final SnakeMind snakeMind;
    String nextMove = DEFAULT_DIRECTION;
    private Board board;

    // Using weighted approach
    private static final int FREE_DIRECTION_WEIGHT = 5;
    private static final int PREFERRED_DIRECTION_WEIGHT = 5;
    private static final int EASY_KILL_DIRECTION_WEIGHT = 7;
    private static final int DANGEROUS_DIRECTION_WEIGHT = -10;
    private static final int FATAL_DIRECTION_WEIGHT = -1000;
    private WeightedDirections weightedDirections = new WeightedDirections();

    public BattleSnake(Pathfinder pathfinder) {
        this.snakeMind = new SnakeMind(pathfinder);
    }

    @Override
    public String determineNextMove(final JsonNode moveRequest) {
        board = Board.of(moveRequest);
        snakeMind.getPreferredDirections(board)
                 .forEach(direction -> weightedDirections.addWeight(direction, PREFERRED_DIRECTION_WEIGHT));

        avoidSelf();
        avoidWalls();
        avoidOtherSnakes();
        avoidSnakeHeadCollision();
        avoidDeadEnds();
        eatTheWeak();

        LOG.debug("Weighted directions: {}", weightedDirections);
        nextMove = weightedDirections.getHighest().direction;
        return nextMove;
    }

    private void avoidDeadEnds() {
        Lookahead lookahead = new Lookahead(board);

        lookahead.findPathsFrom(board.ownSnake.headPosition).stream()
                 .map(path -> new WeightedDirection(board.ownSnake.headPosition.directionTo(path.getFirstStep()), path.getLength() / 10))
                 .forEach(direction -> weightedDirections.addWeight(direction));
    }

    private void avoidSelf() {
        if (board.ownSnake.length <= 2) {
            avoidSnakeBody(board.ownSnake.body);
        } else {
            avoidSnakeBody(board.ownSnake.bodyWithoutTail);
        }
    }

    private void avoidSnakeBody(List<Field> snakeBody) {
        snakeBody.stream()
                 .filter(it -> board.ownSnake.headPosition.distanceTo(it) == 1)
                 .map(it -> board.ownSnake.headPosition.directionTo(it))
                 .forEach(direction -> weightedDirections.addWeight(direction, FATAL_DIRECTION_WEIGHT));
    }

    private void avoidSnakeHeadCollision() {
        HashSet<String> dangerousDirections = board.otherSnakes.stream()
                                                               .filter(potentiallyCollidingSnakes())
                                                               .filter(equalOrLargerSnakes())
                                                               .map(allPossibleDirections())
                                                               .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        dangerousDirections.forEach(direction -> weightedDirections.addWeight(direction, DANGEROUS_DIRECTION_WEIGHT));
    }


    private void eatTheWeak() {
        HashSet<String> killDirections = board.otherSnakes.stream()
                                                          .filter(potentiallyCollidingSnakes())
                                                          .filter(smallerSnakes())
                                                          .map(allPossibleDirections())
                                                          .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
        killDirections.forEach(direction -> weightedDirections.addWeight(direction, EASY_KILL_DIRECTION_WEIGHT));
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
            weightedDirections.addWeight("left", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.y <= 0) {
            weightedDirections.addWeight("up", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.x >= board.maxX) {
            weightedDirections.addWeight("right", FATAL_DIRECTION_WEIGHT);
        }
        if (board.ownSnake.headPosition.y >= board.maxY) {
            weightedDirections.addWeight("down", FATAL_DIRECTION_WEIGHT);
        }
    }

    private void avoidOtherSnakes() {
        board.otherSnakes.forEach(it -> avoidSnakeBody(it.bodyWithoutTail));
    }
}

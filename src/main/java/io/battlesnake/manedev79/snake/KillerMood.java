package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Field;
import io.battlesnake.manedev79.game.Path;
import io.battlesnake.manedev79.game.Pathfinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class KillerMood implements SnakeMood {

    private static final Logger LOG = LoggerFactory.getLogger(KillerMood.class);
    private Board board;
    private Pathfinder pathfinder;

    KillerMood(Board board, Pathfinder pathfinder) {
        this.board = board;
        this.pathfinder = pathfinder;
    }

    @Override
    public Optional<Collection<String>> provideDirections() {
        return Optional.ofNullable(chaseClosestShorterSnake());
    }

    private Collection<String> chaseClosestShorterSnake() {
        Optional<Field> closestShorterSnakeHead = board.otherSnakes.stream()
                         .filter(otherSnake -> otherSnake.length < board.ownSnake.length)
                         .reduce((snake, snake2) -> {
                             if (board.compareDistanceFromCurrentPosition(snake.headPosition, snake2.headPosition) > 0) {
                                return snake2;
                             } else {
                                 return snake;
                             }
                         }).map(snake -> snake.headPosition);

        if (!closestShorterSnakeHead.isPresent()) {
            LOG.debug("There is no closest shorter snake");
            return null;
        }

        LOG.debug("Closest shorter snake head is at {}", closestShorterSnakeHead);

        Path path = pathfinder.findPath(board, board.ownSnake.headPosition, closestShorterSnakeHead.get());
        Field nextField = path.getSteps().stream().findFirst().orElse(board.middleField());
        return singletonList(board.ownSnake.headPosition.directionTo(nextField));
    }
}
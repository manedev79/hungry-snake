package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Pathfinder;
import io.battlesnake.manedev79.game.Snake;

import java.util.Collection;
import java.util.Collections;

import static io.battlesnake.manedev79.snake.BattleSnake.Config.HUNGRY_THRESHOLD;
import static java.lang.Boolean.TRUE;

class SnakeMind {

    private Pathfinder pathfinder;

    SnakeMind(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    Collection<String> getPreferredDirections(Board board) {
        return determineSnakeMood(board).provideDirections()
                                        .orElse(new LazyMood(board).provideDirections()
                                                                   .orElse(Collections.singletonList("UP")));
    }

    private SnakeMood determineSnakeMood(Board board) {
        if (!board.containsFood()) {
            return new LazyMood(board);
        } else if (board.ownSnake.health < HUNGRY_THRESHOLD || !isLongestSnake(board.ownSnake, board)) {
            return new HungryMood(board, pathfinder);
        } else {
            return new KillerMood(board, pathfinder);
        }
    }

    private boolean isLongestSnake(Snake snake, Board board) {
        return board.otherSnakes.stream()
                                .filter(otherSnake -> !otherSnake.id.equals(snake.id))
                                .map(otherSnake -> snake.length > otherSnake.length)
                                .reduce(TRUE, Boolean::logicalAnd);
    }
}

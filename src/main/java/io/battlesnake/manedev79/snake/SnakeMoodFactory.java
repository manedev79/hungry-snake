package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Pathfinder;
import io.battlesnake.manedev79.game.Snake;

import static io.battlesnake.manedev79.snake.BattleSnake.HUNGRY_THRESHOLD;
import static java.lang.Boolean.TRUE;

class SnakeMoodFactory {

    private Pathfinder pathfinder;

    SnakeMoodFactory(Pathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    SnakeMood determineSnakeMood(Board board) {
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

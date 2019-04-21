package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class KillerMood implements SnakeMood {

    private static final Logger LOG = LoggerFactory.getLogger(KillerMood.class);
    private Board board;

    KillerMood(Board board) {
        this.board = board;
    }

    @Override
    public Collection<String> provideDirections() {
        return chaseShortestSnake();
    }

    private Collection<String> chaseShortestSnake() {
        Field shortesSnakeHead = board.otherSnakes.stream().reduce((snake, snake2) -> {
            if (snake.length < snake2.length) {
                return snake;
            } else {
                return snake2;
            }
        }).map(snake -> snake.headPosition).orElse(board.middleField()); // TOOD: Do not 'chase' in orElse
        LOG.debug("Shortest snake head is at {}", shortesSnakeHead);

        return board.ownSnake.headPosition.directionsTo(shortesSnakeHead);
    }
}

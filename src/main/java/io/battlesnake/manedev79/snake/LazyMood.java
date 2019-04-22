package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;

import java.util.Collection;
import java.util.Optional;

public class LazyMood implements SnakeMood {

    private final Board board;

    LazyMood(Board board) {
        this.board = board;
    }

    @Override
    public Optional<Collection<String>> provideDirections() {
        return Optional.of(followOwnTail());
    }

    private Collection<String> followOwnTail() {
        return board.ownSnake.headPosition.directionsTo(board.ownSnake.tailPosition);
    }
}

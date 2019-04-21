package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;

import java.util.Collection;

public class LazyMood implements SnakeMood {

    private final Board board;

    LazyMood(Board board) {
        this.board = board;
    }

    @Override
    public Collection<String> provideDirections() {
        return followOwnTail();
    }

    private Collection<String> followOwnTail() {
        return board.ownSnake.headPosition.directionsTo(board.ownSnake.tailPosition);
    }
}

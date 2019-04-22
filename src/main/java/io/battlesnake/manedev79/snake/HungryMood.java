package io.battlesnake.manedev79.snake;

import io.battlesnake.manedev79.game.Board;
import io.battlesnake.manedev79.game.Field;
import io.battlesnake.manedev79.game.Path;
import io.battlesnake.manedev79.game.Pathfinder;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class HungryMood implements SnakeMood {
    private final Pathfinder pathfinder;
    private final Board board;

    HungryMood(Board board, Pathfinder pathfinder) {
        this.board = board;
        this.pathfinder = pathfinder;
    }

    @Override
    public Optional<Collection<String>> provideDirections() {
        return Optional.ofNullable(moveToFood());
    }

    private Collection<String> moveToFood() {
        Field foodLocation = board.closestFoodLocation();
        Path path = pathfinder.findPath(board, board.ownSnake.headPosition, foodLocation);
        Field nextField = path.getSteps().stream().findFirst().orElse(null);
        return board.ownSnake.headPosition.directionsTo(nextField);
    }
}

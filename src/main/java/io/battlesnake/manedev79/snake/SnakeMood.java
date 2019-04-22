package io.battlesnake.manedev79.snake;

import java.util.Collection;
import java.util.Optional;

public interface SnakeMood {
    Optional<Collection<String>> provideDirections();
}

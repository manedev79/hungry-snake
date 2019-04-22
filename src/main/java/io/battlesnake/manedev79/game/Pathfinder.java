package io.battlesnake.manedev79.game;

public interface Pathfinder {
    Path findPath(Board board, Field start, Field destination);
}

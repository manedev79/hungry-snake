package io.battlesnake.manedev79.snake;

import com.fasterxml.jackson.databind.JsonNode;

public interface SnakeAI {
    String determineNextMove(JsonNode moveRequest);
}

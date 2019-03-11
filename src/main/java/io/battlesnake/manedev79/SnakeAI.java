package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;

public interface SnakeAI {
    String determineNextMove(JsonNode moveRequest);
}

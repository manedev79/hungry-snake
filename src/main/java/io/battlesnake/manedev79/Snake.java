package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface Snake {
    String determineNextMove(JsonNode moveRequest);

    Map<String, String> getSnakeConfig();
}

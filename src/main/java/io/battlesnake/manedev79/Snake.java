package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;

public interface Snake {
    String determineNextMove(JsonNode moveRequest);
}

package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;

public class SimpleSnake extends AbstractSnake {
    @Override
    protected String moveIntoDirection(JsonNode moveRequest) {
        return "up";
    }
}

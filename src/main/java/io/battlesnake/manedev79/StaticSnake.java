package io.battlesnake.manedev79;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.Iterator;

public class StaticSnake extends AbstractSnake {

    private String[] directions = new String[]{"up", "up", "left", "left", "down", "down", "right", "right"};
    private Iterator<String> iterator;

    @Override
    protected String moveIntoDirection(JsonNode moveRequest) {
        return getDirections().next();
    }

    private Iterator<String> getDirections() {
        if (iterator == null || !iterator.hasNext()) {
            iterator = Arrays.asList(directions).iterator();
        }
        return iterator;
    }
}

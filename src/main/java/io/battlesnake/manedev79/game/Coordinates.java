package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

public class Coordinates {
    public final int x;
    public final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinates of(JsonNode jsonCoordinates) {
        return new Coordinates(jsonCoordinates.get("x").asInt(), jsonCoordinates.get("y").asInt());
    }

    public String directionTo(Coordinates other) {
        if (x < other.x) return "right";
        if (x > other.x) return "left";
        if (y > other.y) return "up";
        if (y < other.y) return "down";
        return "";
    }

    public int distanceTo(Coordinates other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }
}

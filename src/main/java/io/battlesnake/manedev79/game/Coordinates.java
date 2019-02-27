package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

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

    public Collection<String> directionsTo(Coordinates other) {
        Collection<String> directions = new HashSet<>();
        if (x < other.x) directions.add("right");
        if (x > other.x) directions.add("left");
        if (y > other.y) directions.add("up");
        if (y < other.y) directions.add("down");
        return directions;
    }

    public int distanceTo(Coordinates other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}

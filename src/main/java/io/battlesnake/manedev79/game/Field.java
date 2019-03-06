package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Field {
    public final int x;
    public final int y;

    Field(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Field of(JsonNode jsonCoordinates) {
        return new Field(jsonCoordinates.get("x").asInt(), jsonCoordinates.get("y").asInt());
    }

    public String directionTo(Field other) {
        if (x < other.x) return "right";
        if (x > other.x) return "left";
        if (y > other.y) return "up";
        if (y < other.y) return "down";
        return "up"; // Default direction
    }

    public Collection<String> directionsTo(Field other) {
        Collection<String> directions = new HashSet<>();
        if (x < other.x) directions.add("right");
        if (x > other.x) directions.add("left");
        if (y > other.y) directions.add("up");
        if (y < other.y) directions.add("down");
        return directions;
    }

    public int distanceTo(Field other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field that = (Field) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Field{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

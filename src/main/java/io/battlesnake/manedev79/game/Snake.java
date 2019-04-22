package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.unmodifiableList;

public class Snake {
    public final String id;
    public final String name;
    public final Field headPosition;
    public final Field tailPosition;
    public final List<Field> body;
    public final List<Field> bodyWithoutTail;
    public final int length;
    public final int health;

    public Snake(JsonNode jsonNode) {
        this.id = jsonNode.get("id").asText();
        this.name = jsonNode.get("name").asText();
        this.health = jsonNode.get("health").asInt();
        LinkedList<Field> tempBody = getBody(jsonNode.get("body"));
        this.headPosition = tempBody.getFirst();
        this.tailPosition = tempBody.getLast();
        this.body = unmodifiableList(tempBody);
        this.length = tempBody.size();
        this.bodyWithoutTail = unmodifiableList(tempBody.subList(0, length - 1));
    }

    private LinkedList<Field> getBody(JsonNode jsonBody) {
        LinkedList<Field> snakeBody = new LinkedList<>();
        jsonBody.forEach(it -> snakeBody.add(Field.of(it)));
        return snakeBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snake snake = (Snake) o;
        return id.equals(snake.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Snake{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", headPosition=" + headPosition +
                ", tailPosition=" + tailPosition +
                ", body=" + body +
                ", length=" + length +
                ", health=" + health +
                '}';
    }
}

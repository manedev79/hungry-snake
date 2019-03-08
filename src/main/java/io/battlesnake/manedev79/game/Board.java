package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Integer.signum;
import static java.util.stream.Collectors.toList;

public class Board {
    private static final Logger LOG = LoggerFactory.getLogger(Board.class);
    private final int maxX;
    private final int maxY;
    private final Collection<Field> blockedFields;
    private JsonNode jsonNode;


    private Board(JsonNode jsonNode) {

        this.jsonNode = jsonNode;
        this.maxX = jsonNode.get("board").get("width").asInt() - 1;
        this.maxY = jsonNode.get("board").get("height").asInt() - 1;
        this.blockedFields = allSnakeBodies();
    }

    public Field middleField() {
        return new Field(maxX / 2, maxY / 2);
    }

    private Collection<Field> allSnakeBodies() {
        Collection<Field> allSnakeBodies = new HashSet<>();

        jsonNode.get("board").get("snakes").forEach(
                snake -> snake.get("body").forEach(
                        element -> allSnakeBodies.add(Field.of(element))
                )
        );

        return allSnakeBodies;
    }

    public static Board of(JsonNode jsonNode) {
        return new Board(jsonNode);
    }

    Collection<Field> getNeighbors(Field current) {
        List<Field> neighbors = new ArrayList<>();

        if (current.x > 0) {
            neighbors.add(new Field(current.x - 1, current.y));
        }
        if (current.y > 0) {
            neighbors.add(new Field(current.x, current.y - 1));
        }
        if (current.x < maxX) {
            neighbors.add(new Field(current.x + 1, current.y));
        }
        if (current.y < maxY) {
            neighbors.add(new Field(current.x, current.y + 1));
        }

        return neighbors.stream().filter(it -> !blockedFields.contains(it)).collect(toList());
    }
}

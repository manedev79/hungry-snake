package io.battlesnake.manedev79.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import static java.lang.Integer.signum;

public class Board {
    private JsonNode jsonNode;

    private Board(JsonNode jsonNode) {

        this.jsonNode = jsonNode;
    }

    public static Board of(JsonNode jsonNode) {
        return new Board(jsonNode);
    }

    public Path getPath(Coordinates start, Coordinates destination) {
        return  Path.of(directWay(start, destination));
    }

    private Collection<Coordinates> directWay(Coordinates start, Coordinates destination) {
        if (start.x == destination.x && start.y == destination.y) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        int deltaX = destination.x - start.x;
        int deltaY = destination.y - start.y;

        Coordinates newStart;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            int newX = start.x + signum(deltaX);
            newStart = new Coordinates(newX, start.y);
        } else {
            int newY = start.y + signum(deltaY);
            newStart = new Coordinates(start.x, newY);
        }

        return combine(newStart, directWay(newStart, destination));
    }

    private Collection<Coordinates> combine(Coordinates newStart, Collection<Coordinates> directWay) {
        ArrayList<Coordinates> combinedCollection = new ArrayList<>();
        combinedCollection.add(newStart);
        combinedCollection.addAll(directWay);
        return combinedCollection;
    }

}

package io.battlesnake.manedev79.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.lang.Integer.signum;

public class DirectWayPathfinder implements Pathfinder {

    @Override
    public Path findPath(Board board, Field start, Field destination) {
        return Path.of(directWay(start, destination));
    }

    private Collection<Field> directWay(Field start, Field destination) {
        if (start.x == destination.x && start.y == destination.y) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }

        int deltaX = destination.x - start.x;
        int deltaY = destination.y - start.y;

        Field newStart;
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            int newX = start.x + signum(deltaX);
            newStart = new Field(newX, start.y);
        } else {
            int newY = start.y + signum(deltaY);
            newStart = new Field(start.x, newY);
        }

        return combine(newStart, directWay(newStart, destination));
    }

    private Collection<Field> combine(Field newStart, Collection<Field> directWay) {
        ArrayList<Field> combinedCollection = new ArrayList<>();
        combinedCollection.add(newStart);
        combinedCollection.addAll(directWay);
        return combinedCollection;
    }
}

package io.battlesnake.manedev79.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Integer.signum;

public class Pathfinder {
    private static final Logger LOG = LoggerFactory.getLogger(Pathfinder.class);

    private PriorityQueue<Field> openSet = new PriorityQueue<>(this::compareFScore);
    private Set<Field> closedSet = new HashSet<>();
    private Map<Field, Field> cameFrom = new HashMap<>();
    private Map<Field, Integer> gScore = new HashMap<>();
    private Map<Field, Integer> fScore = new HashMap<>();
    private Board board;

    public Path findPath(Board board, Field start, Field destination) {
        this.board = board;
        return Path.of(aStarWay(start, destination));
    }

    private Collection<Field> aStarWay(Field start, Field destination) {
        LOG.debug("A* way from {} to {}", start, destination);
        openSet.add(start);
        gScore.put(start, 0);
        fScore.put(start, start.distanceTo(destination));

        while (!openSet.isEmpty()) {
            LOG.debug("OpenSet head:{}, all {}", openSet.peek(), openSet);
            Field current = openSet.remove();
            if (current.equals(destination)) {
                return reconstructPath(cameFrom, current);
            }
            closedSet.add(current);
            board.getNeighbors(current).stream()
                                 .filter(it -> !closedSet.contains(it))
                                 .forEach(neighbor -> {
                                             int tentativeGScore = gScoreFor(current) + current.distanceTo(neighbor);
                                             openSet.add(neighbor);
                                             if (tentativeGScore < gScoreFor(neighbor)) {
                                                 cameFrom.put(neighbor, current);
                                                 gScore.put(neighbor, tentativeGScore);
                                                 fScore.put(neighbor, tentativeGScore + neighbor.distanceTo(destination));
                                             }
                                         }
                                 );
        }
        //noinspection unchecked
        return Collections.EMPTY_LIST;
    }

    private Integer gScoreFor(Field current) {
        return gScore.getOrDefault(current, Integer.MAX_VALUE);
    }

    private Collection<Field> reconstructPath(Map<Field, Field> cameFrom, Field destination) {
        List<Field> path = new ArrayList<>();
        path.add(destination);
        Field current = destination;

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        // Glitches:
        // Invert
        Collections.reverse(path);
        // Remove current field
        path.remove(0);

        LOG.debug("Reconstructed path: {}", path);
        return path;
    }

    private Integer compareFScore(Field field, Field otherField) {
        return Integer.compare(fScore.getOrDefault(field, Integer.MAX_VALUE),
                fScore.getOrDefault(otherField, Integer.MAX_VALUE));
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

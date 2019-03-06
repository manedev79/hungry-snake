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
    private PriorityQueue<Field> openSet = new PriorityQueue<>(this::compareFScore);
    private Set<Field> closedSet = new HashSet<>();
    private Map<Field, Field> cameFrom = new HashMap<>();
    private Map<Field, Integer> gScore = new HashMap<>();
    private Map<Field, Integer> fScore = new HashMap<>();

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

    public Path getPath(Field start, Field destination) {
//        return Path.of(directWay(start, destination));
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
            getNeighbors(current).stream()
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

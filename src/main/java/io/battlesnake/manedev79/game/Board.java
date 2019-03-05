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
    private final Collection<Coordinates> blockedFields;
    private JsonNode jsonNode;
    private PriorityQueue<Coordinates> openSet = new PriorityQueue<>(this::compareFScore);
    private Set<Coordinates> closedSet = new HashSet<>();
    private Map<Coordinates, Coordinates> cameFrom = new HashMap<>();
    private Map<Coordinates, Integer> gScore = new HashMap<>();
    private Map<Coordinates, Integer> fScore = new HashMap<>();

    private Board(JsonNode jsonNode) {

        this.jsonNode = jsonNode;
        this.maxX = jsonNode.get("board").get("width").asInt() - 1;
        this.maxY = jsonNode.get("board").get("height").asInt() - 1;
        this.blockedFields = allSnakeBodies();
    }

    private Collection<Coordinates> allSnakeBodies() {
        Collection<Coordinates> allSnakeBodies = new HashSet<>();

        jsonNode.get("board").get("snakes").forEach(
                snake -> snake.get("body").forEach(
                        element -> allSnakeBodies.add(Coordinates.of(element))
                )
        );

        return allSnakeBodies;
    }

    public static Board of(JsonNode jsonNode) {
        return new Board(jsonNode);
    }

    public Path getPath(Coordinates start, Coordinates destination) {
//        return Path.of(directWay(start, destination));
        return Path.of(aStarWay(start, destination));
    }

    private Collection<Coordinates> aStarWay(Coordinates start, Coordinates destination) {
        LOG.info("A* way from {} to {}", start, destination);
        openSet.add(start);
        gScore.put(start, 0);
        fScore.put(start, start.distanceTo(destination));

        while (!openSet.isEmpty()) {
            LOG.info("OpenSet head:{}, all {}", openSet.peek(), openSet);
            Coordinates current = openSet.remove();
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

    private Integer gScoreFor(Coordinates current) {
        return gScore.getOrDefault(current, Integer.MAX_VALUE);
    }

    Collection<Coordinates> getNeighbors(Coordinates current) {
        List<Coordinates> neighbors = new ArrayList<>();

        if (current.x > 0) {
            neighbors.add(new Coordinates(current.x - 1, current.y));
        }
        if (current.y > 0) {
            neighbors.add(new Coordinates(current.x, current.y - 1));
        }
        if (current.x < maxX) {
            neighbors.add(new Coordinates(current.x + 1, current.y));
        }
        if (current.y < maxY) {
            neighbors.add(new Coordinates(current.x, current.y + 1));
        }

        return neighbors.stream().filter(it -> !blockedFields.contains(it)).collect(toList());
    }

    private Collection<Coordinates> reconstructPath(Map<Coordinates, Coordinates> cameFrom, Coordinates destination) {
        List<Coordinates> path = new ArrayList<>();
        path.add(destination);
        Coordinates current = destination;

        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        // Glitches:
        // Invert
        Collections.reverse(path);
        // Remove current field
        path.remove(0);

        LOG.info("Reconstructed path: {}", path);
        return path;
    }

    private Integer compareFScore(Coordinates field, Coordinates otherField) {
        return Integer.compare(fScore.getOrDefault(field, Integer.MAX_VALUE),
                fScore.getOrDefault(otherField, Integer.MAX_VALUE));
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

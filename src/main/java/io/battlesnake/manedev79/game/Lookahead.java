package io.battlesnake.manedev79.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Lookahead {
    private static Logger LOG = LoggerFactory.getLogger(Lookahead.class);
    private static final int SEARCH_DEPTH = 6;
    private Board board;
    private List<Path> possiblePaths;
    private Pathfinder pathfinder;

    public Lookahead(Board board, Pathfinder pathfinder) {
        this.board = board;
        this.pathfinder = pathfinder;
    }

    public List<Path> findPathsFrom(Field from) {
        possiblePaths = new ArrayList<>();
        board.getFreeAdjacentFields(from).forEach(neighbor -> possiblePaths.add(Path.of(neighbor)));

        for (int i = 0; i < SEARCH_DEPTH; i++) {
            lookahead(i);
        }

        LOG.trace("Possible paths: {}", possiblePaths);

        if (possiblePaths.isEmpty()) {
            LOG.trace("Trying to find path to own tail");
            Path path = pathfinder.findPath(board, board.ownSnake.headPosition, board.ownSnake.tailPosition);
            if (!path.getSteps().isEmpty()) {
                possiblePaths.add(path);
            }
        }

        return possiblePaths;
    }

    private void lookahead(int currentSearchDepth) {
        ArrayList<Path> newPaths = new ArrayList<>();
        possiblePaths.stream()
                     .filter(path -> path.getLength() >= currentSearchDepth)
                     .forEach(path -> board.getFreeAdjacentFields(path.getLastStep()).forEach(neighbor -> newPaths.add(Path.of(path, neighbor))));
        possiblePaths = newPaths;
    }
}

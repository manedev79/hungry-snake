package io.battlesnake.manedev79.game;

import java.util.List;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

public class Lookahead {
    static final int SEARCH_DEPTH = 10;
    private Board board;

    public Lookahead(Board board) {
        this.board = board;
    }

    public List<Path> findPathsFrom(Field field) {
        return board.getFreeAdjacentFields(field)
                    .stream()
                    .map(this::searchPathFromNeighbor)
                    .collect(toList());
    }

    // Like the FloodFill algorithm (iterative 4fill)
    private Path searchPathFromNeighbor(Field neighbor) {
        Stack<Field> stack = new Stack<>();
        stack.push(neighbor);
        Path.Builder path = Path.Builder.newPath();

        while (!stack.empty()) {
            Field currentField = stack.pop();
            if (!path.contains(currentField)) {
                path.with(currentField);
                board.getFreeAdjacentFields(currentField).forEach(stack::push);
            }
        }

        return path.build();
    }
}

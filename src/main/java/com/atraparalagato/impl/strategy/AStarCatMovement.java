package com.atraparalagato.impl.strategy;

import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;

public class AStarCatMovement {
    private final HexGameBoard board;

    public AStarCatMovement(HexGameBoard board) {
        this.board = board;
    }

    public boolean hasPathToGoal(HexPosition catPos) {
        return getNextMove(catPos).isPresent();
    }

    public Optional<HexPosition> getNextMove(HexPosition catPos) {
        Set<HexPosition> visited = new HashSet<>();
        Queue<List<HexPosition>> queue = new LinkedList<>();
        queue.add(List.of(catPos));

        while (!queue.isEmpty()) {
            List<HexPosition> path = queue.poll();
            HexPosition current = path.get(path.size() - 1);

            if (current.isAtBorder(board.getSize()) && !current.equals(catPos)) {
                return Optional.of(path.get(1));
            }
            for (HexPosition n : getNeighbors(current)) {
                if (!visited.contains(n) && !board.isBlocked(n)) {
                    visited.add(n);
                    List<HexPosition> newPath = new ArrayList<>(path);
                    newPath.add(n);
                    queue.add(newPath);
                }
            }
        }
        return Optional.empty();
    }

    private List<HexPosition> getNeighbors(HexPosition pos) {
        int[][] deltas = {{1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, -1}, {1, -1}};
        List<HexPosition> neighbors = new ArrayList<>();
        for (int[] d : deltas) {
            HexPosition neighbor = new HexPosition(pos.getQ() + d[0], pos.getR() + d[1]);
            if (board.isPositionInBounds(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
}

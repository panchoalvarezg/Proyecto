package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition getNextMove(HexPosition catPosition, HexGameBoard board) {
        Queue<HexPosition> queue = new LinkedList<>();
        Set<HexPosition> visited = new HashSet<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();

        queue.add(catPosition);
        visited.add(catPosition);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();

            if (current.isAtBorder(board.getSize())) {
                return reconstructPath(cameFrom, current);
            }

            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor) && !board.isBlocked(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return null; // sin camino
    }

    private HexPosition reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition current) {
        while (cameFrom.containsKey(current) && !cameFrom.get(current).equals(current)) {
            HexPosition previous = cameFrom.get(current);
            if (cameFrom.get(previous) == null) return current;
            current = previous;
        }
        return current;
    }
}

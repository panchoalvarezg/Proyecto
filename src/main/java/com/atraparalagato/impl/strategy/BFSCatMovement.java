package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {
    @Override
    public HexPosition selectMove(HexGameBoard board, HexPosition start) {
        Queue<HexPosition> queue = new LinkedList<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();
            if (board.isAtBorder(current)) {
                return reconstructFirstStep(cameFrom, start, current);
            }
            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return start;
    }

    private HexPosition reconstructFirstStep(Map<HexPosition, HexPosition> cameFrom, HexPosition start, HexPosition end) {
        HexPosition current = end;
        HexPosition prev = null;
        while (current != null && !current.equals(start)) {
            prev = current;
            current = cameFrom.get(current);
        }
        return prev != null ? prev : start;
    }
}

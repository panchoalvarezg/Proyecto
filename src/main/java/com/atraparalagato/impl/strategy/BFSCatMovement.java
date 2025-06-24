package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato usando BFS (camino más corto garantizado).
 */
public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition selectMove(HexGameBoard board, HexPosition start, Predicate<HexPosition> isGoal) {
        List<HexPosition> path = getBFSPath(board, start, isGoal);
        if (path.size() > 1) {
            return path.get(1); // Siguiente paso en el camino más corto
        }
        return start; // No hay movimiento posible
    }

    private List<HexPosition> getBFSPath(HexGameBoard board, HexPosition start, Predicate<HexPosition> isGoal) {
        Queue<HexPosition> queue = new LinkedList<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();
            if (isGoal.test(current)) {
                return reconstructPath(cameFrom, current);
            }
            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.singletonList(start);
    }

    private List<HexPosition> reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition end) {
        List<HexPosition> path = new ArrayList<>();
        HexPosition current = end;
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}

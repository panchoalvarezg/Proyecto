package com.atraparalagato.impl.strategy;

import com.atraparalagato.impl.model.*;
import java.util.*;
import java.util.function.*;

public class BFSCatMovement {
    public List<HexPosition> getPossibleMoves(HexGameBoard board, HexPosition from) {
        return board.getAdjacentPositions(from);
    }

    public HexPosition selectBestMove(HexGameBoard board, HexPosition from, Predicate<HexPosition> goal) {
        List<HexPosition> path = getFullPath(board, from, goal);
        if (path.size() > 1) return path.get(1);
        return from;
    }

    public boolean hasPathToGoal(HexGameBoard board, HexPosition from, Predicate<HexPosition> goalPredicate) {
        return !getFullPath(board, from, goalPredicate).isEmpty();
    }

    public List<HexPosition> getFullPath(HexGameBoard board, HexPosition from, Predicate<HexPosition> goalPredicate) {
        Queue<HexPosition> queue = new LinkedList<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();
        queue.add(from);
        visited.add(from);
        while (!queue.isEmpty()) {
            HexPosition curr = queue.poll();
            if (goalPredicate.test(curr)) {
                // reconstruir camino
                List<HexPosition> path = new ArrayList<>();
                HexPosition step = curr;
                while (step != null) {
                    path.add(step);
                    step = cameFrom.get(step);
                }
                Collections.reverse(path);
                return path;
            }
            for (HexPosition neighbor : board.getAdjacentPositions(curr)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    cameFrom.put(neighbor, curr);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }
}

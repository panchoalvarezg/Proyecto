package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;

public class BFSCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition getNextMove(HexGameBoard board, HexPosition catPosition) {
        Set<HexPosition> visited = new HashSet<>();
        Queue<HexPosition> queue = new LinkedList<>();

        queue.add(catPosition);
        visited.add(catPosition);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();

            if (current.isAtBorder(board.getSize())) {
                return reconstructPath(catPosition, current);
            }

            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!board.isBlocked(neighbor) && visited.add(neighbor)) {
                    neighbor.setPrevious(current);
                    queue.add(neighbor);
                }
            }
        }

        return null; // no path found
    }

    @Override
    public List<HexPosition> getFullPath(HexGameBoard board, HexPosition catPosition) {
        List<HexPosition> path = new ArrayList<>();
        HexPosition move = getNextMove(board, catPosition);

        if (move == null) return path;

        while (move != null && !move.equals(catPosition)) {
            path.add(0, move);
            move = move.getPrevious();
        }
        return path;
    }

    private HexPosition reconstructPath(HexPosition start, HexPosition end) {
        HexPosition step = end;
        while (step != null && !step.equals(start)) {
            if (step.getPrevious() != null && step.getPrevious().equals(start)) {
                return step;
            }
            step = step.getPrevious();
        }
        return null;
    }
} 

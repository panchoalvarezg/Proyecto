package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition getNextMove(HexPosition catPosition, HexGameBoard board) {
        List<HexPosition> path = getFullPath(catPosition, board);
        return (path != null && path.size() > 1) ? path.get(1) : catPosition;
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition catPosition, HexGameBoard board) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Map<HexPosition, Integer> gScore = new HashMap<>();

        gScore.put(catPosition, 0);
        openSet.add(new Node(catPosition, heuristic(catPosition, board)));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.position.isAtBorder(board.getSize())) {
                return reconstructPath(cameFrom, current.position);
            }
            for (HexPosition neighbor : board.getAdjacentPositions(current.position)) {
                if (board.isBlocked(neighbor)) continue;
                int tentativeG = gScore.get(current.position) + 1;
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.position);
                    gScore.put(neighbor, tentativeG);
                    int fScore = tentativeG + heuristic(neighbor, board);
                    openSet.add(new Node(neighbor, fScore));
                }
            }
        }
        return null;
    }

    private int heuristic(HexPosition position, HexGameBoard board) {
        int size = board.getSize();
        int distanceToEdge = Math.min(Math.min(position.q(), size - position.q() - 1),
                                      Math.min(position.r(), size - position.r() - 1));
        return distanceToEdge;
    }

    private List<HexPosition> reconstructPath(Map<HexPosition, HexPosition> cameFrom, HexPosition current) {
        List<HexPosition> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }

    private record Node(HexPosition position, int fScore) {}
}

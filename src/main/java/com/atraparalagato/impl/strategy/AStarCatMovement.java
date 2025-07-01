package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

    private record Node(HexPosition position, int g, int h) {
        int f() {
            return g + h;
        }
    }

    @Override
    public HexPosition getNextMove(HexPosition catPosition, HexGameBoard board) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::f));
        Set<HexPosition> closedSet = new HashSet<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Map<HexPosition, Integer> gScore = new HashMap<>();

        openSet.add(new Node(catPosition, 0, heuristic(catPosition, board.getSize())));
        gScore.put(catPosition, 0);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.position().isAtBorder(board.getSize())) {
                return reconstructPath(cameFrom, current.position());
            }

            closedSet.add(current.position());

            for (HexPosition neighbor : board.getAdjacentPositions(current.position())) {
                if (board.isBlocked(neighbor) || closedSet.contains(neighbor)) continue;

                int tentativeG = gScore.get(current.position()) + 1;
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current.position());
                    gScore.put(neighbor, tentativeG);
                    openSet.add(new Node(neighbor, tentativeG, heuristic(neighbor, board.getSize())));
                }
            }
        }

        return null; // sin camino
    }

    private int heuristic(HexPosition pos, int boardSize) {
        int borderDistance = boardSize - Math.max(Math.abs(pos.q()), Math.abs(pos.r()));
        return Math.max(0, borderDistance);
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

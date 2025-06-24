package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {
    @Override
    public HexPosition selectMove(HexGameBoard board, HexPosition start) {
        // El objetivo es cualquier borde
        Set<HexPosition> visited = new HashSet<>();
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Map<HexPosition, Integer> gScore = new HashMap<>();

        open.add(new Node(start, 0, heuristic(start, board)));
        gScore.put(start, 0);

        while (!open.isEmpty()) {
            Node curr = open.poll();
            if (board.isAtBorder(curr.position)) {
                return reconstructFirstStep(cameFrom, start, curr.position);
            }
            visited.add(curr.position);
            for (HexPosition neighbor : board.getAdjacentPositions(curr.position)) {
                if (visited.contains(neighbor)) continue;
                int tentativeG = gScore.get(curr.position) + 1;
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, curr.position);
                    gScore.put(neighbor, tentativeG);
                    int f = tentativeG + heuristic(neighbor, board);
                    open.add(new Node(neighbor, tentativeG, f));
                }
            }
        }
        return start;
    }

    private int heuristic(HexPosition pos, HexGameBoard board) {
        int size = board.getSize();
        int distQ = Math.abs(pos.getQ()) == size ? 0 : size - Math.abs(pos.getQ());
        int distR = Math.abs(pos.getR()) == size ? 0 : size - Math.abs(pos.getR());
        int distS = Math.abs(pos.getS()) == size ? 0 : size - Math.abs(pos.getS());
        return Math.min(distQ, Math.min(distR, distS));
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

    static class Node {
        HexPosition position;
        int g, f;
        Node(HexPosition position, int g, int f) {
            this.position = position;
            this.g = g;
            this.f = f;
        }
    }
}

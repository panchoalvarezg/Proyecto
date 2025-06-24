package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato usando algoritmo A*.
 */
public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

    @Override
    public HexPosition selectMove(HexGameBoard board, HexPosition start, Predicate<HexPosition> isGoal) {
        List<HexPosition> path = getAStarPath(board, start, isGoal);
        if (path.size() > 1) {
            return path.get(1); // El siguiente paso óptimo
        }
        return start; // No hay movimiento posible
    }

    private List<HexPosition> getAStarPath(HexGameBoard board, HexPosition start, Predicate<HexPosition> isGoal) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<HexPosition, Integer> gScore = new HashMap<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> closed = new HashSet<>();
        open.add(new Node(start, 0, heuristic(start)));
        gScore.put(start, 0);

        while (!open.isEmpty()) {
            Node curr = open.poll();
            if (isGoal.test(curr.position)) {
                return reconstructPath(cameFrom, curr.position);
            }
            closed.add(curr.position);

            for (HexPosition neighbor : board.getAdjacentPositions(curr.position)) {
                if (closed.contains(neighbor)) continue;
                int tentativeG = gScore.get(curr.position) + 1;
                if (tentativeG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, curr.position);
                    gScore.put(neighbor, tentativeG);
                    int f = tentativeG + heuristic(neighbor);
                    open.add(new Node(neighbor, tentativeG, f));
                }
            }
        }
        return Collections.singletonList(start);
    }

    private int heuristic(HexPosition pos) {
        // Heurística: distancia mínima a un borde
        int size = 9; // Ajusta según el tamaño real del tablero si lo necesitas
        int distQ = Math.min(Math.abs(pos.getQ() - size), Math.abs(pos.getQ() + size));
        int distR = Math.min(Math.abs(pos.getR() - size), Math.abs(pos.getR() + size));
        int distS = Math.min(Math.abs(pos.getS() - size), Math.abs(pos.getS() + size));
        return Math.min(distQ, Math.min(distR, distS));
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

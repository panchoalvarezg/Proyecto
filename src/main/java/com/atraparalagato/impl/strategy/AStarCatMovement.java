package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;

import java.util.*;

public class AStarCatMovement implements CatMovementStrategy<HexPosition> {

    private static class Node {
        HexPosition position;
        Node parent;
        double g;
        double h;

        Node(HexPosition position, Node parent, double g, double h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        double f() {
            return g + h;
        }
    }

    @Override
    public HexPosition getNextMove(HexPosition start, HexGameBoard board) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::f));
        Set<HexPosition> closed = new HashSet<>();

        open.add(new Node(start, null, 0, heuristic(start, board)));

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (closed.contains(current.position)) continue;

            closed.add(current.position);

            if (current.position.isAtBorder(board.getSize())) {
                return reconstructPath(current);
            }

            for (HexPosition neighbor : board.getAdjacentPositions(current.position)) {
                if (board.isBlocked(neighbor) || closed.contains(neighbor)) continue;
                double gScore = current.g + current.position.distanceTo(neighbor);
                double hScore = heuristic(neighbor, board);
                open.add(new Node(neighbor, current, gScore, hScore));
            }
        }

        return start; // No se encontró camino, gato se queda en su lugar
    }

    private double heuristic(HexPosition pos, HexGameBoard board) {
        int size = board.getSize();
        // Distancia mínima a un borde (heurística simple)
        return Math.min(Math.min(Math.abs(pos.getQ()), Math.abs(pos.getR())), Math.abs(-pos.getQ() - pos.getR()));
    }

    private HexPosition reconstructPath(Node node) {
        List<HexPosition> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path.isEmpty() ? null : path.get(0); // Devuelve el primer paso
    }
}

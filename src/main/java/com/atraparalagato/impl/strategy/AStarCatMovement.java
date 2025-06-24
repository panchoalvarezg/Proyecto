package com.atraparalagato.impl.strategy;

import com.atraparalagato.impl.model.*;
import java.util.*;
import java.util.function.*;

public class AStarCatMovement {
    public List<HexPosition> getPossibleMoves(HexGameBoard board, HexPosition from) {
        return board.getAdjacentPositions(from);
    }

    public HexPosition selectBestMove(HexGameBoard board, HexPosition from, Predicate<HexPosition> goal) {
        List<HexPosition> path = getFullPath(board, from, goal);
        if (path.size() > 1) return path.get(1);
        return from;
    }

    public ToIntFunction<HexPosition> getHeuristicFunction(HexPosition goal) {
        return pos -> Math.abs(goal.getX() - pos.getX()) + Math.abs(goal.getY() - pos.getY());
    }

    public Predicate<HexPosition> getGoalPredicate(HexGameBoard board) {
        return pos -> {
            int x = pos.getX(), y = pos.getY();
            return x == 0 || y == 0 || x == board.getWidth()-1 || y == board.getHeight()-1;
        };
    }

    public boolean hasPathToGoal(HexGameBoard board, HexPosition from, Predicate<HexPosition> goalPredicate) {
        return !getFullPath(board, from, goalPredicate).isEmpty();
    }

    public List<HexPosition> getFullPath(HexGameBoard board, HexPosition from, Predicate<HexPosition> goalPredicate) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Map<HexPosition, Integer> gScore = new HashMap<>();
        Map<HexPosition, HexPosition> cameFrom = new HashMap<>();
        Set<HexPosition> closed = new HashSet<>();
        open.add(new Node(from, 0, 0));
        gScore.put(from, 0);
        while (!open.isEmpty()) {
            Node curr = open.poll();
            if (goalPredicate.test(curr.pos)) {
                // Reconstruir camino
                List<HexPosition> path = new ArrayList<>();
                HexPosition step = curr.pos;
                while (step != null) {
                    path.add(step);
                    step = cameFrom.get(step);
                }
                Collections.reverse(path);
                return path;
            }
            closed.add(curr.pos);
            for (HexPosition neighbor : board.getAdjacentPositions(curr.pos)) {
                if (closed.contains(neighbor)) continue;
                int tentative = gScore.get(curr.pos) + 1;
                if (tentative < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, curr.pos);
                    gScore.put(neighbor, tentative);
                    int h = getHeuristicFunction(curr.pos).applyAsInt(neighbor);
                    open.add(new Node(neighbor, tentative, tentative + h));
                }
            }
        }
        return Collections.emptyList();
    }

    private static class Node {
        HexPosition pos;
        int g, f;
        Node(HexPosition pos, int g, int f) { this.pos = pos; this.g = g; this.f = f; }
    }
}

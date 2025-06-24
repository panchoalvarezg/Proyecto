package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Paradigma: POO + Funcional (heurísticas, predicados y caminos)
 */
public class AStarCatMovement extends CatMovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position from, GameBoard board) {
        return board.getAdjacentPositions(from).stream().filter(pos -> !board.isBlocked(pos)).collect(Collectors.toList());
    }

    @Override
    public Position selectBestMove(Position from, GameBoard board) {
        // A*: POO (estructura) + funcional (heurística)
        List<Position> path = getFullPath(from, board);
        if (path.size() > 1) return path.get(1); // el primer paso tras la posición actual
        return from;
    }

    @Override
    protected Function<Position, Double> getHeuristicFunction(Position target) {
        // Funcional: lambda para distancia Manhattan
        return (Position p) -> {
            int dq = ((HexPosition) p).getQ() - ((HexPosition) target).getQ();
            int dr = ((HexPosition) p).getR() - ((HexPosition) target).getR();
            return (double)(Math.abs(dq) + Math.abs(dr));
        };
    }

    @Override
    protected Predicate<Position> getGoalPredicate(GameBoard board) {
        // Funcional: lambda para detectar si es borde
        return pos -> {
            HexPosition hp = (HexPosition) pos;
            int size = ((HexGameBoard)board).getSize();
            return hp.getQ() == 0 || hp.getQ() == size-1 || hp.getR() == 0 || hp.getR() == size-1;
        };
    }

    @Override
    public boolean hasPathToGoal(Position from, GameBoard board) {
        return !getFullPath(from, board).isEmpty();
    }

    @Override
    public List<Position> getFullPath(Position from, GameBoard board) {
        // POO: estructura A*; Funcional: heurísticas y predicados
        Predicate<Position> goal = getGoalPredicate(board);
        Function<Position, Double> heuristic = getHeuristicFunction(from);

        // ... Implementación A* (puedes completarla)
        return new ArrayList<>(); // TODO: implementar
    }
}

package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Predicate;

/**
 * Paradigma: POO + Funcional (predicados para meta, movimientos)
 */
public class BFSCatMovement extends CatMovementStrategy {
    @Override
    public List<Position> getPossibleMoves(Position from, GameBoard board) {
        return board.getAdjacentPositions(from).stream().filter(pos -> !board.isBlocked(pos)).collect(Collectors.toList());
    }

    @Override
    public Position selectBestMove(Position from, GameBoard board) {
        List<Position> path = getFullPath(from, board);
        if (path.size() > 1) return path.get(1);
        return from;
    }

    @Override
    public boolean hasPathToGoal(Position from, GameBoard board) {
        return !getFullPath(from, board).isEmpty();
    }

    @Override
    public List<Position> getFullPath(Position from, GameBoard board) {
        Predicate<Position> isGoal = pos -> {
            HexPosition hp = (HexPosition) pos;
            int size = ((HexGameBoard)board).getSize();
            return hp.getQ() == 0 || hp.getQ() == size-1 || hp.getR() == 0 || hp.getR() == size-1;
        };
        // Implementación BFS aquí
        // ...
        return new ArrayList<>(); // TODO: implementar
    }
}

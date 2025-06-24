package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.base.model.GameBoard;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    public BFSCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }

    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        // Implementa lógica BFS aquí
        return List.of();
    }

    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, HexPosition currentPosition, HexPosition targetPosition) {
        // Implementa lógica BFS aquí
        return possibleMoves.stream().findFirst();
    }

    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        // BFS normalmente no usa heurística, pero debes devolver una función (por ejemplo, siempre 0.0)
        return pos -> 0.0;
    }

    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        // Implementa el predicado de objetivo para BFS
        return pos -> true;
    }

    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        // BFS normalmente considera todos los costos iguales (1.0)
        return 1.0;
    }

    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        // Implementa lógica adecuada
        return false;
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        // Implementa lógica adecuada
        return List.of();
    }
}

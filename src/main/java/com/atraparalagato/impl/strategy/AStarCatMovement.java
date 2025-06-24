package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.base.model.GameBoard;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

    public AStarCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }

    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        // Implementación concreta
        return List.of();
    }

    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, HexPosition currentPosition, HexPosition targetPosition) {
        // Implementación concreta
        return possibleMoves.stream().findFirst(); // Ejemplo base
    }

    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        // Implementación concreta (ejemplo: distancia hexagonal)
        return pos -> pos.distanceTo(targetPosition);
    }

    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        // Implementación concreta (ejemplo: llegar a un borde)
        return pos -> true;
    }

    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        // Implementación concreta (ejemplo: costo fijo 1)
        return 1.0;
    }

    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        // Implementación concreta
        return false;
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        // Implementación concreta
        return List.of();
    }
}

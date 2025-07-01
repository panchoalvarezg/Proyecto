package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new LinkedHashSet<>();
    }

    @Override
    protected boolean isPositionInBounds(HexPosition position) {
        return position.isWithinBounds(size);
    }

    @Override
    protected boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !isBlocked(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> positions = new ArrayList<>();
        for (int q = -size; q <= size; q++) {
            for (int r = Math.max(-size, -q - size); r <= Math.min(size, -q + size); r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        List<HexPosition> adjacents = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
            if (isPositionInBounds(neighbor) && !isBlocked(neighbor)) {
                adjacents.add(neighbor);
            }
        }
        return adjacents;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    // Setter opcional necesario para restaurar desde repositorio
    public void setBlockedPositions(Set<HexPosition> positions) {
        this.blockedPositions = new LinkedHashSet<>(positions);
    }
}

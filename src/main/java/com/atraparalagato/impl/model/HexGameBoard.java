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
        return new HashSet<>();
    }

    @Override
    protected boolean isPositionInBounds(HexPosition position) {
        return position.isWithinBounds(size);
    }

    @Override
    protected boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && !blockedPositions.contains(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> valid = new ArrayList<>();
        for (int q = -size; q <= size; q++) {
            for (int r = Math.max(-size, -q - size); r <= Math.min(size, -q + size); r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) {
                    valid.add(pos);
                }
            }
        }
        return valid;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        int[][] directions = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        List<HexPosition> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(pos.getQ() + dir[0], pos.getR() + dir[1]);
            if (isPositionInBounds(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }
}

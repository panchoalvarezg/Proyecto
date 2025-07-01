package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;

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
        return isPositionInBounds(position) && !isBlocked(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> result = new ArrayList<>();
        for (int q = -size / 2; q <= size / 2; q++) {
            for (int r = -size / 2; r <= size / 2; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (pos.isWithinBounds(size) && condition.test(pos)) {
                    result.add(pos);
                }
            }
        }
        return result;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
                {1, 0}, {1, -1}, {0, -1},
                {-1, 0}, {-1, 1}, {0, 1}
        };

        List<HexPosition> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
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

    public void setBlockedPositions(Set<HexPosition> blockedPositions) {
        this.blockedPositions = blockedPositions;
    }
}

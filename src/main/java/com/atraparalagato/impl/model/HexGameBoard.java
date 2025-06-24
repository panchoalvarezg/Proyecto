package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;

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
        for (int q = 0; q < size; q++)
            for (int r = 0; r < size; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (condition.test(pos)) result.add(pos);
            }
        return result;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] dirs = {{1,0},{0,1},{-1,1},{-1,0},{0,-1},{1,-1}};
        List<HexPosition> adj = new ArrayList<>();
        for (int[] d : dirs) {
            HexPosition neighbor = new HexPosition(position.getQ() + d[0], position.getR() + d[1]);
            if (isPositionInBounds(neighbor) && !isBlocked(neighbor)) adj.add(neighbor);
        }
        return adj;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }
}

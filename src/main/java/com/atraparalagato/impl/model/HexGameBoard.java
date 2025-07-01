package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;

public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    public boolean isPositionInBounds(HexPosition pos) {
        int radius = getSize() / 2;
        return Math.abs(pos.getQ()) <= radius &&
               Math.abs(pos.getR()) <= radius &&
               Math.abs(pos.getQ() + pos.getR()) <= radius;
    }

    @Override
    public boolean isValidMove(HexPosition pos) {
        return isPositionInBounds(pos) && !isBlocked(pos);
    }

    @Override
    public void executeMove(HexPosition pos) {
        if (!isValidMove(pos)) throw new IllegalArgumentException("Invalid move");
        getBlockedPositions().add(pos);
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        int[][] directions = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};
        List<HexPosition> adj = new ArrayList<>();
        for (int[] d : directions) {
            HexPosition p = new HexPosition(pos.getQ() + d[0], pos.getR() + d[1]);
            if (isPositionInBounds(p)) adj.add(p);
        }
        return adj;
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        List<HexPosition> positions = new ArrayList<>();
        int radius = getSize() / 2;
        for (int q = -radius; q <= radius; q++) {
            for (int r = -radius; r <= radius; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (isPositionInBounds(pos) && condition.test(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.*;

public class HexGameBoard extends GameBoard<HexPosition> {
    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new LinkedHashSet<>();
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = position.getS();
        return Math.abs(q) < size && Math.abs(r) < size && Math.abs(s) < size;
    }

    @Override
    public boolean isValidMove(HexPosition position) {
        if (!isPositionInBounds(position)) {
            throw new IllegalArgumentException("Posición fuera de los límites del tablero.");
        }
        if (isBlocked(position)) {
            throw new IllegalArgumentException("La celda ya está bloqueada.");
        }
        return true;
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] deltas = {{1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, -1}, {1, -1}};
        List<HexPosition> neighbors = new ArrayList<>();
        for (int[] d : deltas) {
            HexPosition neighbor = new HexPosition(position.getQ() + d[0], position.getR() + d[1]);
            if (isPositionInBounds(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public void setBlockedPositions(Set<HexPosition> blockedPositions) {
        this.blockedPositions.clear();
        this.blockedPositions.addAll(blockedPositions);
    }
}

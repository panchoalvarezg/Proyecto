package com.atraparalagato.impl.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class HexGameBoard {
    private int size;
    private LinkedHashSet<HexPosition> blockedPositions;

    public HexGameBoard(int size) {
        this.size = size;
        this.blockedPositions = new LinkedHashSet<>();
    }

    public int getSize() {
        return size;
    }

    public Set<HexPosition> getBlockedPositions() {
        return blockedPositions;
    }

    public void setBlockedPositions(Set<HexPosition> blockedPositions) {
        this.blockedPositions.clear();
        this.blockedPositions.addAll(blockedPositions);
    }

    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    public boolean isPositionInBounds(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = position.getS();
        return Math.abs(q) < size && Math.abs(r) < size && Math.abs(s) < size;
    }

    public boolean isValidMove(HexPosition position) {
        if (!isPositionInBounds(position)) {
            throw new IllegalArgumentException("Posición fuera de los límites del tablero.");
        }
        if (isBlocked(position)) {
            throw new IllegalArgumentException("La celda ya está bloqueada.");
        }
        return true;
    }

    public boolean makeMove(HexPosition position) {
        if (isValidMove(position)) {
            blockedPositions.add(position);
            return true;
        }
        return false;
    }
}

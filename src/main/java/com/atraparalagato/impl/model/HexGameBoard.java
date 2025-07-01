package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.*;

public class HexGameBoard extends GameBoard<HexPosition> {

    private final int size;
    private final Set<HexPosition> blockedPositions;

    public HexGameBoard(int size) {
        this.size = size;
        this.blockedPositions = new LinkedHashSet<>();
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    @Override
    public void makeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        List<HexPosition> neighbors = new ArrayList<>();
        for (HexPosition neighbor : position.neighbors()) {
            if (neighbor.isWithinBounds(size) && !isBlocked(neighbor)) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public Set<HexPosition> getBlockedPositions() {
        return blockedPositions;
    }

    public void setBlockedPositions(Set<HexPosition> positions) {
        blockedPositions.clear();
        blockedPositions.addAll(positions);
    }

    protected boolean isPositionInBounds(HexPosition position) {
        return position.isWithinBounds(size);
    }

    protected boolean isValidMove(HexPosition position) {
        return !isBlocked(position) && isPositionInBounds(position);
    }
}

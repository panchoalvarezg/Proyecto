// HexGameBoard.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HexGameBoard extends GameBoard<HexPosition> {
    private final int size;
    private final Set<HexPosition> blockedPositions = new HashSet<>();

    public HexGameBoard(int size) {
        this.size = size;
    }

    public int getSize() { return size; }

    @Override
    public Set<HexPosition> getBlockedPositions() {
        return blockedPositions;
    }

    @Override
    public boolean isBlocked(HexPosition pos) {
        return blockedPositions.contains(pos);
    }

    @Override
    public boolean isPositionInBounds(HexPosition pos) {
        int q = pos.getQ(), r = pos.getR(), s = pos.getS();
        return Math.abs(q) <= size && Math.abs(r) <= size && Math.abs(s) <= size;
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> p) {
        return generateAllPositions().stream().filter(p).collect(Collectors.toList());
    }

    private Set<HexPosition> generateAllPositions() {
        Set<HexPosition> all = new HashSet<>();
        for (int q = -size; q <= size; q++) {
            for (int r = -size; r <= size; r++) {
                int s = -q - r;
                if (Math.abs(s) <= size) {
                    all.add(new HexPosition(q, r));
                }
            }
        }
        return all;
    }
}


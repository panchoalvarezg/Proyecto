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
            for (int r = -size; r <= size; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (isPositionInBounds(pos) && condition.test(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
            {1, 0}, {0, 1}, {-1, 1},
            {-1, 0}, {0, -1}, {1, -1}
        };
        List<HexPosition> adjacents = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(position.getQ() + dir[0], position.getR() + dir[1]);
            if (isPositionInBounds(neighbor)) {
                adjacents.add(neighbor);
            }
        }
        return adjacents;
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }
} 


// HexPosition.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

public class HexPosition extends Position {
    private final int q;
    private final int r;

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) return Double.MAX_VALUE;
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(q - o.q);
        int dr = Math.abs(r - o.r);
        int ds = Math.abs((-q - r) - (-o.q - o.r));
        return (dq + dr + ds) / 2.0;
    }

    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition)) return this;
        HexPosition o = (HexPosition) other;
        return new HexPosition(q + o.q, r + o.r);
    }

    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition)) return this;
        HexPosition o = (HexPosition) other;
        return new HexPosition(q - o.q, r - o.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        return distanceTo(other) == 1.0;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        int s = -q - r;
        return Math.abs(q) <= maxSize && Math.abs(r) <= maxSize && Math.abs(s) <= maxSize;
    }

    @Override
    public int hashCode() {
        return 31 * q + r;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HexPosition that = (HexPosition) obj;
        return q == that.q && r == that.r;
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ")";
    }
}

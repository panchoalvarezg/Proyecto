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

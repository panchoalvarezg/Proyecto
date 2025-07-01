package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

import java.util.Objects;

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

    public int getS() {
        return -q - r;
    }

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) return Double.MAX_VALUE;
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(this.q - o.q);
        int dr = Math.abs(this.r - o.r);
        int ds = Math.abs(this.getS() - o.getS());
        return (dq + dr + ds) / 2.0;
    }

    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition)) return this;
        HexPosition o = (HexPosition) other;
        return new HexPosition(this.q + o.q, this.r + o.r);
    }

    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition)) return this;
        HexPosition o = (HexPosition) other;
        return new HexPosition(this.q - o.q, this.r - o.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        return distanceTo(other) == 1.0;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        int s = getS();
        return Math.abs(q) <= maxSize && Math.abs(r) <= maxSize && Math.abs(s) <= maxSize;
    }

    public boolean isAtBorder(int size) {
        return Math.abs(q) == size || Math.abs(r) == size || Math.abs(getS()) == size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
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

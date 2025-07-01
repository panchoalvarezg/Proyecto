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
        return (Math.abs(q - o.q) + Math.abs(r - o.r) + Math.abs(getS() - o.getS())) / 2.0;
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
        if (!(other instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) other;
        return this.distanceTo(o) == 1.0;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        return Math.abs(q) <= maxSize && Math.abs(r) <= maxSize && Math.abs(getS()) <= maxSize;
    }

    public boolean isAtBorder(int boardSize) {
        return Math.abs(q) == boardSize || Math.abs(r) == boardSize || Math.abs(getS()) == boardSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) obj;
        return this.q == o.q && this.r == o.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "(" + q + "," + r + ")";
    }
}

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

    public int getQ() { return q; }
    public int getR() { return r; }

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(q - o.q);
        int dr = Math.abs(r - o.r);
        return Math.max(dq, dr); // Distancia hexagonal básica
    }

    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        return new HexPosition(q + o.q, r + o.r);
    }

    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        return new HexPosition(q - o.q, r - o.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        if (!(other instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(q - o.q);
        int dr = Math.abs(r - o.r);
        int ds = Math.abs((-q - r) - (-o.q - o.r));
        return (dq + dr + ds) == 2;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        return q >= 0 && r >= 0 && q < maxSize && r < maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition other = (HexPosition) obj;
        return q == other.q && r == other.r;
    }

    @Override
    public String toString() {
        return "(" + q + "," + r + ")";
    }
}

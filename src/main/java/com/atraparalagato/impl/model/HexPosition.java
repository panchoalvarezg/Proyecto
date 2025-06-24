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
    // S = -(q + r) para coordenadas hexagonales cúbicas
    public int getS() { return -q - r; }

    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        // Distancia hexagonal: máximo de las diferencias absolutas
        return (Math.abs(q - o.q) + Math.abs(r - o.r) + Math.abs(getS() - o.getS())) / 2.0;
    }

    @Override
    public Position add(Position other) {
        HexPosition o = (HexPosition) other;
        return new HexPosition(q + o.q, r + o.r);
    }

    @Override
    public Position subtract(Position other) {
        HexPosition o = (HexPosition) other;
        return new HexPosition(q - o.q, r - o.r);
    }

    @Override
    public boolean isAdjacentTo(Position other) {
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(q - o.q);
        int dr = Math.abs(r - o.r);
        int ds = Math.abs(getS() - o.getS());
        return (dq + dr + ds) == 2;
    }

    @Override
    public boolean isWithinBounds(int maxSize) {
        return q >= 0 && r >= 0 && -q - r >= 0 && q < maxSize && r < maxSize && -q - r < maxSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) obj;
        return q == o.q && r == o.r;
    }

    @Override
    public String toString() {
        return "(" + q + "," + r + ")";
    }
}

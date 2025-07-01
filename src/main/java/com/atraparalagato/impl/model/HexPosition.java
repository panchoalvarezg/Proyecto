package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

import java.util.Objects;

public class HexPosition implements Position {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HexPosition)) return false;
        HexPosition that = (HexPosition) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "HexPosition(" + q + "," + r + ")";
    }

    public double distanceTo(HexPosition other) {
        return (Math.abs(q - other.q)
                + Math.abs(q + r - other.q - other.r)
                + Math.abs(r - other.r)) / 2.0;
    }
}

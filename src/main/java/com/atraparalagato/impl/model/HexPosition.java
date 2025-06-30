package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;
import java.util.Objects;

// IMPLEMENTA Position
public class HexPosition implements Position {
    private int q;
    private int r;

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() { return q; }
    public int getR() { return r; }
    public void setQ(int q) { this.q = q; }
    public void setR(int r) { this.r = r; }
    public int getS() { return -q - r; }

    public boolean isAtBorder(int size) {
        int border = size - 1;
        int s = getS();
        return (Math.abs(q) == border) || (Math.abs(r) == border) || (Math.abs(s) == border);
    }

    public int distanceTo(HexPosition other) {
        int dq = Math.abs(this.q - other.q);
        int dr = Math.abs(this.r - other.r);
        int ds = Math.abs(this.getS() - other.getS());
        return (dq + dr + ds) / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition other = (HexPosition) obj;
        return this.q == other.q && this.r == other.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    @Override
    public String toString() {
        return "HexPosition{" + "q=" + q + ", r=" + r + '}';
    }
}

package com.atraparalagato.impl.model;

import java.util.Objects;

public class HexPosition {
    private int q;
    private int r;

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

    public void setQ(int q) {
        this.q = q;
    }

    public void setR(int r) {
        this.r = r;
    }

    /**
     * Un punto está en el borde si |q| == size-1 o |r| == size-1 o |s| == size-1,
     * donde s = -q - r
     */
    public boolean isAtBorder(int size) {
        int border = size - 1;
        int s = -q - r;
        return (Math.abs(q) == border) || (Math.abs(r) == border) || (Math.abs(s) == border);
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

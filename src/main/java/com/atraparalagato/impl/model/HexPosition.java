// HexPosition.java
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

    public int getQ() { return q; }
    public int getR() { return r; }
    public int getS() { return -q - r; }

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
        return String.format("HexPosition(q=%d,r=%d)", q, r);
    }

    public boolean isAtBorder(int size) {
        int s = getS();
        return Math.abs(q) == size || Math.abs(r) == size || Math.abs(s) == size;
    }
}

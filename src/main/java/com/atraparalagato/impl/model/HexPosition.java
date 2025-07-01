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

    public boolean isWithinBounds(int boardSize) {
        return q >= 0 && r >= 0 && q < boardSize && r < boardSize;
    }

    public boolean isAtBorder(int boardSize) {
        return q == 0 || r == 0 || q == boardSize - 1 || r == boardSize - 1;
    }

    public HexPosition[] neighbors() {
        return new HexPosition[] {
            new HexPosition(q + 1, r),
            new HexPosition(q - 1, r),
            new HexPosition(q, r + 1),
            new HexPosition(q, r - 1),
            new HexPosition(q + 1, r - 1),
            new HexPosition(q - 1, r + 1)
        };
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
        return "(" + q + ", " + r + ")";
    }
}

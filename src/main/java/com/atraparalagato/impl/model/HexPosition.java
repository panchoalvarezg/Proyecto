package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

import java.io.Serializable;
import java.util.Objects;

/**
 * Posición en un tablero hexagonal usando coordenadas cúbicas.
 */
public class HexPosition implements Position, Serializable {
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

    // Los siguientes métodos pueden ser útiles según la lógica de tu juego
    public int getX() {
        return q;
    }

    public int getY() {
        return r;
    }

    public HexPosition add(HexPosition other) {
        return new HexPosition(this.q + other.q, this.r + other.r);
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
        return "HexPosition{" + "q=" + q + ", r=" + r + ", s=" + getS() + '}';
    }
}

package com.atraparalagato.impl.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una posición en un tablero hexagonal usando coordenadas cúbicas (q,r,s) donde q + r + s = 0.
 * Compatible con los ejemplos y estrategias que esperan estos métodos.
 */
public class HexPosition implements Serializable {
    private final int q; // columna
    private final int r; // fila
    // s se calcula como -(q + r)

    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    /** Retorna la coordenada q (columna axial) */
    public int getQ() {
        return q;
    }

    /** Retorna la coordenada r (fila axial) */
    public int getR() {
        return r;
    }

    /** Calcula la coordenada s automáticamente (propiedad del sistema cúbico) */
    public int getS() {
        return -q - r;
    }

    /** Alias para q (para compatibilidad con código que usa getX) */
    public int getX() {
        return q;
    }

    /** Alias para r (para compatibilidad con código que usa getY) */
    public int getY() {
        return r;
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
        return Objects.hash(q, r, getS());
    }

    @Override
    public String toString() {
        return "HexPosition{" +
                "q=" + q +
                ", r=" + r +
                ", s=" + getS() +
                '}';
    }
}

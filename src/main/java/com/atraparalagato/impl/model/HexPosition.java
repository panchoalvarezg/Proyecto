package com.atraparalagato.impl.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa una posición en un tablero hexagonal.
 * Basado en el ejemplo de ExampleGameBoard/ExampleGameState,
 * pero adaptado para el sistema de coordenadas hexagonales.
 */
public class HexPosition implements Serializable {
    private final int x;
    private final int y;

    public HexPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Dos posiciones hexagonales son iguales si sus coordenadas x e y son iguales.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition other = (HexPosition) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "HexPosition{" + "x=" + x + ", y=" + y + '}';
    }
}

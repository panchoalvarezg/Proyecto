package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.model.GameBoard;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Paradigma: Programación Orientada a Objetos y Funcional (para búsquedas/filtrado)
 */
public class HexGameBoard extends GameBoard {
    private final int size;
    private Set<HexPosition> blockedPositions;

    public HexGameBoard(int size) {
        this.size = size;
        this.blockedPositions = new HashSet<>();
    }

    @Override
    public boolean isPositionInBounds(Position position) {
        // POO: validación de límites del tablero hexagonal
        if (!(position instanceof HexPosition)) return false;
        HexPosition pos = (HexPosition) position;
        int q = pos.getQ(), r = pos.getR();
        return q >= 0 && r >= 0 && q < size && r < size;
    }

    @Override
    public boolean isValidMove(Position from, Position to) {
        // POO: regla de movimiento + funcional (uso de isBlocked)
        return isPositionInBounds(to) && !isBlocked(to) && getAdjacentPositions(from).contains(to);
    }

    @Override
    public void executeMove(Position from, Position to) {
        // POO: ejecutar movimiento (p.ej. bloquear celda)
        if (isValidMove(from, to)) {
            blockedPositions.add((HexPosition) to);
        }
    }

    @Override
    public List<Position> getPositionsWhere(Predicate<Position> condition) {
        // Funcional: filtra todas las posiciones válidas según el predicate
        List<Position> result = new ArrayList<>();
        for (int q = 0; q < size; q++) {
            for (int r = 0; r < size; r++) {
                HexPosition p = new HexPosition(q, r);
                if (condition.test(p)) result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Position> getAdjacentPositions(Position position) {
        // POO: posiciones adyacentes en hexágono (sin salirse)
        int[][] directions = {{1,0},{0,1},{-1,1},{-1,0},{0,-1},{1,-1}};
        List<Position> adj = new ArrayList<>();
        HexPosition p = (HexPosition) position;
        for (int[] d : directions) {
            HexPosition neighbor = new HexPosition(p.getQ() + d[0], p.getR() + d[1]);
            if (isPositionInBounds(neighbor) && !isBlocked(neighbor)) adj.add(neighbor);
        }
        return adj;
    }

    @Override
    public boolean isBlocked(Position position) {
        return blockedPositions.contains(position);
    }

    public int getSize() {
        return size;
    }

    public Set<HexPosition> getBlockedPositions() {
        return blockedPositions;
    }
}

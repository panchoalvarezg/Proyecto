package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación de GameBoard para tableros hexagonales.
 *
 * Incluye validaciones robustas para evitar movimientos inválidos y devolver mensajes claros al controlador.
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    // Inicializa la lista de posiciones bloqueadas como un LinkedHashSet
    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        // Se usa un LinkedHashSet por su eficiente rendimiento y por mantener el orden de inserción, que es útil para implementar una función de undo (deshacer) en el juego
        return new LinkedHashSet<>();
    }

    // Reinicia y reasigna las posiciones bloqueadas del mapa según el set entregado
    public void setBlockedPositions(Set<HexPosition> blockedPositions) {
        this.blockedPositions = blockedPositions;
    }

    // Verifica si una posición ya está bloqueada al revisar si la posición está en el LinkedHashSet de posiciones bloqueadas
    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    // Cambiado a public para permitir su uso seguro desde servicios externos
    @Override
    public boolean isPositionInBounds(HexPosition position) {
        return Math.abs(position.getQ()) <= size &&
                Math.abs(position.getR()) <= size &&
                Math.abs(position.getS()) <= size;
    }

    /**
     * Verifica si una posición es válida para que el jugador la bloquee.
     * Lanza IllegalArgumentException con mensajes claros si el movimiento no es permitido.
     */
    @Override
    protected boolean isValidMove(HexPosition position) {
        if (!isPositionInBounds(position)) {
            throw new IllegalArgumentException("Coordenadas fuera del tablero.");
        }
        if (isBlocked(position)) {
            throw new IllegalArgumentException("La celda ya está bloqueada.");
        }
        if (isAtBorder(position)) {
            throw new IllegalArgumentException("No se puede bloquear una celda del borde.");
        }
        return true;
    }

    /**
     * Determina si una posición está en el borde del tablero.
     */
    private boolean isAtBorder(HexPosition position) {
        int q = position.getQ();
        int r = position.getR();
        int s = position.getS();
        return Math.abs(q) == size || Math.abs(r) == size || Math.abs(s) == size;
    }

    // Agrega la posición entregada a las posiciones bloqueadas, no verifica ninguna condición más porque en el contexto de uso ya se validará que la acción es posible antes de ejecutarla
    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    // Obtiene todas las posiciones con las que puede interactuar el jugador
    private List<HexPosition> getAllPossiblePlayerPositions() {
        List<HexPosition> _positions = new ArrayList<>();
        for (int q = -size + 1; q < size; q++) {
            for (int r = -size + 1; r < size; r++) {
                HexPosition _position = new HexPosition(q, r);

                if (isPositionInBounds(_position) && !isAtBorder(_position)) {
                    _positions.add(_position);
                }
            }
        }
        return _positions;
    }

    // Filtra todas las posiciones con las que puede interactuar el jugador según un filtro (Predicate) dado, y regresa una lista mutable de las posiciones que pasen ese filtro
    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        return getAllPossiblePlayerPositions().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    // Obtiene las posiciones adyacentes a una posición dada mientras estas estén en el rango accesible para el jugador y este no las haya bloqueado
    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        // Lista de vectores unitarios en las 6 posibles direcciones desde una misma casilla hexagonal
        HexPosition[] _directions = {
                new HexPosition(1, 0),
                new HexPosition(1, -1),
                new HexPosition(0, -1),
                new HexPosition(-1, 0),
                new HexPosition(-1, 1),
                new HexPosition(0, 1)
        };

        return Arrays.stream(_directions)
                .map((_direction) -> (HexPosition) position.add(_direction))
                .filter(this::isPositionInBounds)
                .filter(Predicate.not(this::isBlocked))
                .collect(Collectors.toList());
    }

    // Hook method override
    @Override
    protected void onMoveExecuted(HexPosition position) {
        // Ejemplos: logging, notificaciones, validaciones post-movimiento
        super.onMoveExecuted(position);
    }
}

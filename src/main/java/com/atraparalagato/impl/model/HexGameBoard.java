package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación de GameBoard para tablero hexagonal.
 */
public class HexGameBoard extends GameBoard<HexPosition> {

    public HexGameBoard(int size) {
        super(size);
    }

    @Override
    protected Set<HexPosition> initializeBlockedPositions() {
        return new HashSet<>();
    }

    @Override
    public boolean isPositionInBounds(HexPosition position) {
        return Math.abs(position.getQ()) <= size &&
               Math.abs(position.getR()) <= size &&
               Math.abs(position.getS()) <= size;
    }

    @Override
    protected boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) &&
               !isAtBorder(position) &&
               !isBlocked(position);
    }

    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }

    @Override
    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
        return getAllPossiblePositions().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public List<HexPosition> getAdjacentPositions(HexPosition position) {
        HexPosition[] directions = {
            new HexPosition(1, 0),
            new HexPosition(1, -1),
            new HexPosition(0, -1),
            new HexPosition(-1, 0),
            new HexPosition(-1, 1),
            new HexPosition(0, 1)
        };
        return Arrays.stream(directions)
                .map(position::add)
                .filter(this::isPositionInBounds)
                .filter(pos -> !isBlocked(pos))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBlocked(HexPosition position) {
        return blockedPositions.contains(position);
    }

    public boolean isAtBorder(HexPosition position) {
        return Math.abs(position.getQ()) == size ||
               Math.abs(position.getR()) == size ||
               Math.abs(position.getS()) == size;
    }

    private List<HexPosition> getAllPossiblePositions() {
        List<HexPosition> positions = new ArrayList<>();
        for (int q = -size + 1; q < size; q++) {
            for (int r = -size + 1; r < size; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (isPositionInBounds(pos) && !isAtBorder(pos)) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }

    public Set<HexPosition> getBlockedPositions() {
        return Collections.unmodifiableSet(blockedPositions);
    }
}

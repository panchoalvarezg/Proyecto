package com.atraparalagato.impl.model;

import java.util.*;
import java.util.function.Predicate;

public class HexGameBoard {
    private int width;
    private int height;
    private Set<HexPosition> blockedPositions;

    public HexGameBoard(int width, int height, Set<HexPosition> blockedPositions) {
        this.width = width;
        this.height = height;
        this.blockedPositions = blockedPositions != null ? new HashSet<>(blockedPositions) : new HashSet<>();
    }

    public boolean isPositionInBounds(HexPosition pos) {
        int x = pos.getX();
        int y = pos.getY();
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isValidMove(HexPosition from, HexPosition to) {
        return isPositionInBounds(to) && !blockedPositions.contains(to) && getAdjacentPositions(from).contains(to);
    }

    public boolean executeMove(HexPosition from, HexPosition to) {
        if (!isValidMove(from, to)) return false;
        // Aquí podrías actualizar estado si es necesario
        return true;
    }

    public List<HexPosition> getPositionsWhere(Predicate<HexPosition> predicate) {
        List<HexPosition> result = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                HexPosition pos = new HexPosition(x, y);
                if (predicate.test(pos)) result.add(pos);
            }
        }
        return result;
    }

    public List<HexPosition> getAdjacentPositions(HexPosition pos) {
        // Hex grid adjacency calculation (6 directions)
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1},{1,-1},{-1,1}};
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition p = new HexPosition(pos.getX() + dir[0], pos.getY() + dir[1]);
            if (isPositionInBounds(p) && !blockedPositions.contains(p)) adj.add(p);
        }
        return adj;
    }

    public boolean isBlocked(HexPosition pos) {
        return blockedPositions.contains(pos);
    }

    public void setBlocked(HexPosition pos, boolean blocked) {
        if (blocked) blockedPositions.add(pos);
        else blockedPositions.remove(pos);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Set<HexPosition> getBlockedPositions() { return Collections.unmodifiableSet(blockedPositions); }
}

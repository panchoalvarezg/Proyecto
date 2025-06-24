package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HexGameState extends GameState<HexPosition> {
    private HexPosition catPosition;
    private boolean playerWon;
    private int score;
    private final int boardSize;
    private final Set<HexPosition> blockedPositions = new HashSet<>();

    public HexGameState(String gameId, int boardSize) {
        super(gameId);
        this.boardSize = boardSize;
        // El gato inicia en la columna más a la izquierda y en el centro vertical del tablero
        this.catPosition = new HexPosition(0, boardSize / 2);
        this.playerWon = false;
        this.score = 0;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return status == GameStatus.IN_PROGRESS
            && !catPosition.equals(position)
            && !blockedPositions.contains(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        // Este método puede usarse para lógica adicional de movimiento si lo deseas
        return canExecuteMove(position);
    }

    @Override
    public void updateGameStatus() {
        // El jugador pierde si el gato sale del tablero
        if (!catPosition.isWithinBounds(boardSize)) {
            status = GameStatus.PLAYER_LOST;
            playerWon = false;
        } else if (getFreeNeighbors(catPosition).isEmpty()) {
            // El jugador gana si el gato no puede moverse
            status = GameStatus.PLAYER_WON;
            playerWon = true;
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
    }

    @Override
    public boolean isGameFinished() {
        return status != GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean hasPlayerWon() {
        return playerWon;
    }

    @Override
    public int calculateScore() {
        return score;
    }

    public Set<HexPosition> getBlockedPositions() {
        return blockedPositions;
    }

    public void blockCell(HexPosition pos) {
        blockedPositions.add(pos);
    }

    public int getBoardSize() {
        return boardSize;
    }

    public java.util.List<HexPosition> getFreeNeighbors(HexPosition cat) {
        int[][] dirs = {{1,0},{0,1},{-1,1},{-1,0},{0,-1},{1,-1}};
        java.util.List<HexPosition> result = new java.util.ArrayList<>();
        for (int[] d : dirs) {
            HexPosition neighbor = new HexPosition(cat.getQ() + d[0], cat.getR() + d[1]);
            if (neighbor.isWithinBounds(boardSize) && !blockedPositions.contains(neighbor)) {
                result.add(neighbor);
            }
        }
        return result;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> map = new HashMap<>();
        map.put("catQ", catPosition.getQ());
        map.put("catR", catPosition.getR());
        map.put("playerWon", playerWon);
        map.put("score", score);
        map.put("status", status.name());
        map.put("moveCount", moveCount);
        map.put("gameId", gameId);
        map.put("createdAt", createdAt);
        map.put("boardSize", boardSize);
        map.put("blockedCells",
            blockedPositions.stream()
                .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                .toList()
        );
        return map;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (!(serializedState instanceof Map)) return;
        Map<?, ?> map = (Map<?, ?>) serializedState;
        int q = (int) map.get("catQ");
        int r = (int) map.get("catR");
        this.catPosition = new HexPosition(q, r);
        this.playerWon = (boolean) map.get("playerWon");
        this.score = (int) map.get("score");
        this.status = GameStatus.valueOf((String) map.get("status"));
        Object blocked = map.get("blockedCells");
        if (blocked instanceof Iterable<?> iterable) {
            for (Object o : iterable) {
                if (o instanceof Map<?,?> posmap) {
                    Object oq = posmap.get("q");
                    Object or = posmap.get("r");
                    if (oq instanceof Integer iq && or instanceof Integer ir) {
                        blockedPositions.add(new HexPosition(iq, ir));
                    }
                }
            }
        }
    }
}

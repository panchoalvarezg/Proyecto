package com.atraparalagato.impl.model;

import java.io.Serializable;
import java.util.*;

public class HexGameState implements Serializable {
    private HexGameBoard board;
    private HexPosition catPosition;
    private boolean finished;
    private boolean playerWon;
    private int score;

    public HexGameState(HexGameBoard board, HexPosition catPosition) {
        this.board = board;
        this.catPosition = catPosition;
        this.finished = false;
        this.playerWon = false;
        this.score = 0;
        updateGameStatus();
    }

    public boolean canExecuteMove(HexPosition from, HexPosition to) {
        return !finished && board.isValidMove(from, to);
    }

    public void performMove(HexPosition from, HexPosition to) {
        if (canExecuteMove(from, to)) {
            catPosition = to;
            updateGameStatus();
        }
    }

    public void updateGameStatus() {
        // Victoria: gato en el borde
        if (isOnEdge(catPosition)) {
            finished = true;
            playerWon = false;
        } else if (isCatBlocked()) {
            finished = true;
            playerWon = true;
        } else {
            finished = false;
            playerWon = false;
        }
        score = calculateScore();
    }

    public HexPosition getCatPosition() { return catPosition; }
    public void setCatPosition(HexPosition pos) { this.catPosition = pos; updateGameStatus(); }

    public boolean isGameFinished() { return finished; }
    public boolean hasPlayerWon() { return playerWon; }

    public int calculateScore() {
        // Ejemplo: más puntos si el jugador bloquea rápido al gato
        return finished ? (playerWon ? 100 - getMoveCount() : 0) : 0;
    }

    private int getMoveCount() {
        // Implementa un contador real de movimientos si es necesario
        return 0;
    }

    private boolean isOnEdge(HexPosition pos) {
        int x = pos.getX();
        int y = pos.getY();
        return x == 0 || y == 0 || x == board.getWidth()-1 || y == board.getHeight()-1;
    }

    private boolean isCatBlocked() {
        return board.getAdjacentPositions(catPosition).isEmpty();
    }

    // Serialización básica
    public Map<String, Object> getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("catPosition", catPosition);
        state.put("finished", finished);
        state.put("playerWon", playerWon);
        state.put("score", score);
        return state;
    }

    public void restoreFromSerializable(Map<String, Object> state) {
        this.catPosition = (HexPosition) state.get("catPosition");
        this.finished = (Boolean) state.get("finished");
        this.playerWon = (Boolean) state.get("playerWon");
        this.score = (Integer) state.get("score");
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import java.util.*;

/**
 * Implementación avanzada de GameState para el juego hexagonal.
 */
public class HexGameState extends GameState<HexPosition> {

    private HexPosition catPosition;
    private final HexGameBoard gameBoard;
    private final int boardSize;

    public HexGameState(String gameId, int boardSize) {
        super(gameId);
        this.boardSize = boardSize;
        this.gameBoard = new HexGameBoard(boardSize);
        this.catPosition = new HexPosition(0, 0);
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return gameBoard.isValidMove(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        return gameBoard.isValidMove(position) && gameBoard.blockedPositions.add(position);
    }

    @Override
    protected void updateGameStatus() {
        if (isCatAtBorder()) {
            setStatus(GameStatus.PLAYER_LOST);
        } else if (isCatTrapped()) {
            setStatus(GameStatus.PLAYER_WON);
        }
        // Si no, el juego continúa (IN_PROGRESS)
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
        updateGameStatus();
    }

    @Override
    public boolean isGameFinished() {
        return getStatus() != GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean hasPlayerWon() {
        return getStatus() == GameStatus.PLAYER_WON;
    }

    @Override
    public int calculateScore() {
        if (hasPlayerWon()) {
            return Math.max(0, 1000 - getMoveCount() * 10 + boardSize * 50);
        } else {
            return Math.max(0, 100 - getMoveCount() * 5);
        }
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("gameId", getGameId());
        state.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blockedCells", gameBoard.getBlockedPositions());
        state.put("status", getStatus().toString());
        state.put("moveCount", getMoveCount());
        state.put("boardSize", boardSize);
        return state;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (serializedState instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> state = (Map<String, Object>) serializedState;
            @SuppressWarnings("unchecked")
            Map<String, Integer> catPos = (Map<String, Integer>) state.get("catPosition");
            if (catPos != null) {
                this.catPosition = new HexPosition(catPos.get("q"), catPos.get("r"));
            }
            String statusStr = (String) state.get("status");
            if (statusStr != null) {
                setStatus(GameStatus.valueOf(statusStr));
            }
        }
    }

    private boolean isCatAtBorder() {
        return Math.abs(catPosition.getQ()) == boardSize ||
               Math.abs(catPosition.getR()) == boardSize ||
               Math.abs(catPosition.getS()) == boardSize;
    }

    private boolean isCatTrapped() {
        return gameBoard.getAdjacentPositions(catPosition).stream()
                .allMatch(gameBoard::isBlocked);
    }

    public HexGameBoard getGameBoard() {
        return gameBoard;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public Set<HexPosition> getBlockedPositions() {
        return gameBoard.getBlockedPositions();
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import java.util.HashMap;
import java.util.Map;

public class HexGameState extends GameState<HexPosition> {
    private HexGameBoard gameBoard;
    private HexPosition catPosition;
    private int boardSize;
    private String difficulty;

    public HexGameState(String gameId, int boardSize, HexPosition initialCatPosition) {
        super(gameId);
        this.boardSize = boardSize;
        this.gameBoard = new HexGameBoard(boardSize);
        this.catPosition = initialCatPosition;
    }

    public HexGameBoard getGameBoard() {
        return gameBoard;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return !gameBoard.isBlocked(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        gameBoard.getBlockedPositions().add(position);
        return true;
    }

    @Override
    protected void updateGameStatus() {
        if (catPosition.isAtBorder(boardSize)) {
            setStatus(GameStatus.PLAYER_LOST);
        } else if (gameBoard.getAdjacentPositions(catPosition).stream().allMatch(gameBoard::isBlocked)) {
            setStatus(GameStatus.PLAYER_WON);
        }
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
        return status != GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean hasPlayerWon() {
        return status == GameStatus.PLAYER_WON;
    }

    @Override
    public int calculateScore() {
        return 100 - moveCount;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> state = new HashMap<>();
        state.put("gameId", gameId);
        state.put("boardSize", boardSize);
        state.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blocked", gameBoard.getBlockedPositions());
        state.put("status", status);
        state.put("moveCount", moveCount);
        state.put("difficulty", difficulty);
        return state;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        // Implementar si se desea soportar restauración desde JSON o similar
    }

    public void updateStatus(GameStatus status) {
        setStatus(status);
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameBoard;

import java.util.Objects;

public class HexGameState extends GameState<HexPosition> {

    private final HexGameBoard board;
    private HexPosition catPosition;
    private String difficulty;

    public HexGameState(String gameId, int boardSize, HexPosition initialCat) {
        super(gameId);
        this.board = new HexGameBoard(boardSize);
        this.catPosition = initialCat;
    }

    public HexGameState(String gameId, int boardSize) {
        this(gameId, boardSize, new HexPosition(0, 0));
    }

    public HexGameBoard getGameBoard() {
        return board;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void updateStatus(GameStatus status) {
        this.status = status;
    }

    public int getBoardSize() {
        return board.getSize();
    }

    public void setBoardSize(int size) {
        // No-op: board is final; implement resizing logic if needed
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return !board.isBlocked(position) && !position.equals(catPosition) && position.isWithinBounds(board.getSize());
    }

    @Override
    protected boolean performMove(HexPosition position) {
        board.makeMove(position);
        return true;
    }

    @Override
    protected void updateGameStatus() {
        if (catPosition.isAtBorder(board.getSize())) {
            this.status = GameStatus.PLAYER_LOST;
        } else if (board.getAdjacentPositions(catPosition).stream().allMatch(board::isBlocked)) {
            this.status = GameStatus.PLAYER_WON;
        } else {
            this.status = GameStatus.IN_PROGRESS;
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
        return hasPlayerWon() ? 100 - moveCount : 0;
    }

    @Override
    public Object getSerializableState() {
        return new SerializableHexGameState(this);
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (serializedState instanceof SerializableHexGameState serial) {
            this.catPosition = serial.catPosition;
            this.status = serial.status;
            this.moveCount = serial.moveCount;
            this.difficulty = serial.difficulty;
            board.setBlockedPositions(serial.blockedPositions);
        }
    }

    private static class SerializableHexGameState {
        public HexPosition catPosition;
        public GameStatus status;
        public int moveCount;
        public String difficulty;
        public java.util.LinkedHashSet<HexPosition> blockedPositions;

        public SerializableHexGameState(HexGameState state) {
            this.catPosition = state.catPosition;
            this.status = state.status;
            this.moveCount = state.moveCount;
            this.difficulty = state.difficulty;
            this.blockedPositions = new java.util.LinkedHashSet<>(state.board.getBlockedPositions());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexGameState that = (HexGameState) o;
        return Objects.equals(gameId, that.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }
}

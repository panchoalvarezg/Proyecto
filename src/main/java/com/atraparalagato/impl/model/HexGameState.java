package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

import java.util.LinkedHashMap;
import java.util.Map;

public class HexGameState extends GameState<HexPosition> {

    private HexGameBoard board;
    private HexPosition catPosition;

    public HexGameState(String gameId, int boardSize, HexPosition catStart) {
        super(gameId);
        this.board = new HexGameBoard(boardSize);
        this.catPosition = catStart;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return !board.isBlocked(position) && board.getAdjacentPositions(catPosition).contains(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        if (canExecuteMove(position)) {
            board.executeMove(position);
            return true;
        }
        return false;
    }

    @Override
    protected void updateGameStatus() {
        if (catPosition.isAtBorder(board.getSize())) {
            setStatus(GameStatus.PLAYER_LOST);
        } else {
            boolean surrounded = board.getAdjacentPositions(catPosition)
                    .stream().allMatch(board::isBlocked);
            if (surrounded) {
                setStatus(GameStatus.PLAYER_WON);
            }
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
        return getStatus() != GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean hasPlayerWon() {
        return getStatus() == GameStatus.PLAYER_WON;
    }

    @Override
    public int calculateScore() {
        return 100 - getMoveCount();
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("cat", catPosition);
        data.put("blocked", board.getBlockedPositions());
        data.put("size", board.getSize());
        data.put("moves", getMoveCount());
        data.put("status", getStatus().name());
        return data;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (serializedState instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) serializedState;
            Object catObj = map.get("cat");
            Object blockedObj = map.get("blocked");
            Object sizeObj = map.get("size");

            if (catObj instanceof HexPosition && blockedObj instanceof Iterable && sizeObj instanceof Number) {
                this.catPosition = (HexPosition) catObj;
                int size = ((Number) sizeObj).intValue();
                this.board = new HexGameBoard(size);

                for (Object b : (Iterable<?>) blockedObj) {
                    if (b instanceof HexPosition) {
                        board.executeMove((HexPosition) b);
                    }
                }
            }
        }
    }

    public HexGameBoard getGameBoard() {
        return board;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public void setBoardSize(int size) {
        this.board = new HexGameBoard(size);
    }

    public int getBoardSize() {
        return board.getSize();
    }

    public void updateStatus(GameStatus status) {
        setStatus(status);
    }

    public void setDifficulty(String difficulty) {
        // Optional extension if difficulty levels are needed
    }
}

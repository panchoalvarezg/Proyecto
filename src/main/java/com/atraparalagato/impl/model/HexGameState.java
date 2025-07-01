// HexGameState.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

public class HexGameState extends GameState<HexPosition> {
    private final HexGameBoard board;
    private HexPosition catPosition;

    public HexGameState(String gameId, int boardSize, HexPosition initialCatPosition) {
        super(gameId);
        this.board = new HexGameBoard(boardSize);
        this.catPosition = initialCatPosition;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return board.isValidMove(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        boolean moved = board.makeMove(position);
        if (moved) catPosition = position;
        return moved;
    }

    @Override
    protected void updateGameStatus() {
        if (catPosition.isWithinBounds(1)) {
            setStatus(GameStatus.PLAYER_LOST);
        } else if (board.getAdjacentPositions(catPosition).stream().allMatch(board::isBlocked)) {
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
        return 100 - getMoveCount();
    }

    @Override
    public Object getSerializableState() {
        return new Object[]{catPosition.getQ(), catPosition.getR(), status.name(), moveCount};
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (serializedState instanceof Object[] data && data.length == 4) {
            int q = (Integer) data[0];
            int r = (Integer) data[1];
            catPosition = new HexPosition(q, r);
            status = GameStatus.valueOf((String) data[2]);
            moveCount = (Integer) data[3];
        }
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

public class HexGameState extends GameState<HexPosition> {

    private final int boardSize;
    private final HexGameBoard board;
    private HexPosition catPosition;

    public HexGameState(String gameId, int boardSize, HexPosition startPos) {
        super(gameId);
        this.boardSize = boardSize;
        this.board = new HexGameBoard(boardSize);
        this.catPosition = startPos;
    }

    public HexGameState(String gameId, int boardSize) {
        this(gameId, boardSize, new HexPosition(0, 0));
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return board.isValidMove(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        board.makeMove(position);
        return true;
    }

    @Override
    protected void updateGameStatus() {
        if (catPosition.isAtBorder(boardSize)) {
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
        return getMoveCount() * 10;
    }

    @Override
    public Object getSerializableState() {
        return new Object() {
            public final String gameId = getGameId();
            public final int boardSize = board.getSize();
            public final HexPosition catPosition = getCatPosition();
            public final Object blocked = board.getBlockedPositions();
            public final GameStatus status = getStatus();
            public final int moves = getMoveCount();
        };
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        throw new UnsupportedOperationException("Deserialización no implementada.");
    }

    public HexGameBoard getGameBoard() {
        return board;
    }

    public int getBoardSize() {
        return boardSize;
    }
}

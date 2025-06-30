// HexGameState.java
package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;

public class HexGameState extends GameState<HexPosition> {
    private HexPosition catPosition;
    private GameBoard<HexPosition> gameBoard;
    private int moveCount;
    private final int boardSize;

    public HexGameState(String gameId, int size) {
        super(gameId);
        this.boardSize = size;
        this.gameBoard = new HexGameBoard(size);
        this.catPosition = new HexPosition(0, 0);
        this.moveCount = 0;
        this.setStatus(GameStatus.IN_PROGRESS);
    }

    public HexPosition getCatPosition() {
        return catPosition;
    }

    public void setCatPosition(HexPosition newPos) {
        this.catPosition = newPos;
        if (newPos.isAtBorder(boardSize)) {
            this.setStatus(GameStatus.CAT_ESCAPED);
        } else if (gameBoard.getBlockedPositions().contains(newPos)) {
            this.setStatus(GameStatus.CAT_CAUGHT);
        }
    }

    public GameBoard<HexPosition> getGameBoard() {
        return gameBoard;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int count) {
        this.moveCount = count;
    }

    public int getBoardSize() {
        return boardSize;
    }
}

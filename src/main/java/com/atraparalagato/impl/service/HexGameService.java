package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;

public class HexGameService extends GameService<HexGameState, HexPosition> {

    private final CatMovementStrategy<HexPosition> catStrategy;

    public HexGameService(CatMovementStrategy<HexPosition> catStrategy) {
        this.catStrategy = catStrategy;
    }

    @Override
    public HexGameState startNewGame(String gameId, int boardSize) {
        return new HexGameState(gameId, boardSize);
    }

    public boolean blockCell(HexGameState state, HexPosition pos) {
        HexGameBoard board = state.getGameBoard();
        if (board.isValidMove(pos)) {
            board.executeMove(pos);
            state.updateGameStatus();
            return true;
        }
        return false;
    }

    public HexPosition moveCat(HexGameState state) {
        HexPosition current = state.getCatPosition();
        HexGameBoard board = state.getGameBoard();
        HexPosition next = catStrategy.selectMove(board, current);
        if (!next.equals(current)) {
            state.setCatPosition(next);
        }
        return next;
    }

    public boolean isGameOver(HexGameState state) {
        return state.isGameFinished();
    }

    public boolean hasPlayerWon(HexGameState state) {
        return state.hasPlayerWon();
    }
}

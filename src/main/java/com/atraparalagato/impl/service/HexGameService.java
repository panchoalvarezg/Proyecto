package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.strategy.CatMovementStrategy;

import java.util.Set;

/**
 * Servicio principal para el juego hexagonal.
 */
public class HexGameService extends GameService<HexGameState, HexPosition> {

    private final CatMovementStrategy<HexPosition> catStrategy;

    public HexGameService(CatMovementStrategy<HexPosition> catStrategy) {
        this.catStrategy = catStrategy;
    }

    @Override
    public HexGameState startNewGame(String gameId, int boardSize) {
        return new HexGameState(gameId, boardSize);
    }

    @Override
    public boolean blockCell(HexGameState state, HexPosition pos) {
        if (state.getGameBoard().isValidMove(pos)) {
            state.getGameBoard().blockedPositions.add(pos);
            state.updateGameStatus();
            return true;
        }
        return false;
    }

    @Override
    public HexPosition moveCat(HexGameState state) {
        HexPosition current = state.getCatPosition();
        HexGameBoard board = state.getGameBoard();
        HexPosition next = catStrategy.selectMove(board, current, board::isAtBorder);
        if (!next.equals(current)) {
            state.setCatPosition(next);
        }
        return next;
    }

    @Override
    public boolean isGameOver(HexGameState state) {
        return state.isGameFinished();
    }

    @Override
    public boolean hasPlayerWon(HexGameState state) {
        return state.hasPlayerWon();
    }

    public Set<HexPosition> getBlockedPositions(HexGameState state) {
        return state.getBlockedPositions();
    }
}

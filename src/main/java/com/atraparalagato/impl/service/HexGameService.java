package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState.GameStatus;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.impl.strategy.AStarCatMovement;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameBoard;

import java.util.*;

public class HexGameService extends com.atraparalagato.base.service.GameService<HexPosition> {

    public HexGameService() {
        super(
            new HexGameBoard(5),
            new AStarCatMovement(new HexGameBoard(5)),
            new H2GameRepository(),
            () -> UUID.randomUUID().toString(),
            (size) -> new HexGameBoard(size),
            (gameId) -> new HexGameState(gameId, 5)
        );
    }

    public HexGameState createGame(int boardSize, String difficulty, Map<String, Object> options) {
        if (boardSize <= 2) boardSize = 3;
        String gameId = UUID.randomUUID().toString();

        HexGameState gameState = new HexGameState(gameId, boardSize);
        gameState.setCatPosition(new HexPosition(0, 0));
        gameState.setDifficulty(difficulty);
        gameState.setMoveCount(0);
        gameState.updateStatus(GameStatus.IN_PROGRESS);

        gameRepository.save(gameState);
        return gameState;
    }

    @SuppressWarnings("unchecked")
    public Optional<HexGameState> getGameState(String gameId) {
        return gameRepository.findById(gameId).map(gs -> (HexGameState) gs);
    }

    public Optional<HexGameState> executePlayerMove(String gameId, HexPosition position, String playerId) {
        Optional<HexGameState> optional = getGameState(gameId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Game ID no válido o partida inexistente.");
        }
        HexGameState gameState = optional.get();

        if (gameState.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("La partida no está en progreso.");
        }

        HexGameBoard board = gameState.getGameBoard();
        HexPosition cat = gameState.getCatPosition();

        if (!board.isPositionInBounds(position)) {
            throw new IllegalArgumentException("Posición fuera de los límites del tablero.");
        }

        if (cat.equals(position)) {
            throw new IllegalArgumentException("No puedes bloquear la posición donde está el gato.");
        }
        if (board.isBlocked(position)) {
            throw new IllegalArgumentException("La celda ya está bloqueada.");
        }

        board.isValidMove(position);

        board.getBlockedPositions().add(position);

        AStarCatMovement movementStrategy = new AStarCatMovement(board);
        Optional<HexPosition> maybeNextPos = movementStrategy.getNextMove(cat);

        if (maybeNextPos.isEmpty()) {
            gameState.setCatPosition(cat);
        } else {
            HexPosition nextCat = maybeNextPos.get();
            gameState.setCatPosition(nextCat);
        }

        gameState.setMoveCount(gameState.getMoveCount() + 1);
        gameRepository.save(gameState);

        return Optional.of(gameState);
    }

    @Override
    public boolean isValidMove(String gameId, HexPosition position) {
        Optional<HexGameState> optional = getGameState(gameId);
        if (optional.isEmpty()) return false;
        HexGameState gameState = optional.get();
        HexGameBoard board = gameState.getGameBoard();
        HexPosition cat = gameState.getCatPosition();

        if (cat.equals(position)) return false;
        if (board.isBlocked(position)) return false;
        int q = position.getQ();
        int r = position.getR();
        int s = -q - r;
        int size = board.getSize();
        if (Math.abs(q) > size || Math.abs(r) > size || Math.abs(s) > size) return false;
        return true;
    }

    @Override
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        throw new UnsupportedOperationException("No implementado aún");
    }

    @Override
    public Object getGameStatistics(String gameId) {
        throw new UnsupportedOperationException("No implementado aún");
    }

    @Override
    protected HexPosition getTargetPosition(GameState<HexPosition> gameState) {
        HexGameState hexGameState = (HexGameState) gameState;
        HexPosition cat = hexGameState.getCatPosition();
        int size = hexGameState.getBoardSize();

        if (cat.isAtBorder(size)) return cat;

        int q = cat.getQ();
        int r = cat.getR();
        int s = -q - r;

        int qDist = size - Math.abs(q);
        int rDist = size - Math.abs(r);
        int sDist = size - Math.abs(s);

        if (qDist <= rDist && qDist <= sDist) {
            return new HexPosition(q > 0 ? size : -size, r);
        } else if (rDist <= qDist && rDist <= sDist) {
            return new HexPosition(q, r > 0 ? size : -size);
        } else {
            return new HexPosition(q, -q - (s > 0 ? size : -size));
        }
    }

    @Override
    protected void initializeGame(GameState<HexPosition> gameState, GameBoard<HexPosition> board) {
        // Lógica de inicialización opcional
    }
}

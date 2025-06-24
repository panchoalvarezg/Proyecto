package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState.GameStatus;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.impl.strategy.AStarCatMovement;

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
        gameState.setDifficulty(difficulty);
        gameRepository.save(gameState);
        return gameState;
    }

    @SuppressWarnings("unchecked")
    public Optional<HexGameState> getGameState(String gameId) {
        return gameRepository.findById(gameId).map(gs -> (HexGameState) gs);
    }

    /**
     * Ejecuta la jugada del jugador:
     * - Bloquea la celda si es válida.
     * - Mueve el gato automáticamente usando la estrategia AStar.
     * - Actualiza el estado (gana/pierde/continúa).
     * - Guarda el estado y lo retorna.
     */
    public Optional<HexGameState> executePlayerMove(String gameId, HexPosition position, String playerId) {
        Optional<HexGameState> optional = getGameState(gameId);
        if (optional.isEmpty()) return Optional.empty();
        HexGameState gameState = optional.get();

        if (gameState.getStatus() != GameStatus.IN_PROGRESS) {
            return Optional.of(gameState);
        }

        HexGameBoard board = gameState.getGameBoard();
        HexPosition cat = gameState.getCatPosition();

        // Validar movimiento: no se puede bloquear donde está el gato o una celda bloqueada
        if (cat.equals(position) || board.isBlocked(position)) {
            return Optional.of(gameState);
        }

        // Bloquear la celda (agrega al set de bloqueadas)
        board.getBlockedPositions().add(position);

        // Mover el gato automáticamente usando la estrategia AStarCatMovement
        AStarCatMovement movementStrategy = new AStarCatMovement(board);
        Optional<HexPosition> maybeNextPos = movementStrategy.getNextMove(cat);

        if (maybeNextPos.isEmpty()) {
            // El gato no puede moverse: el jugador ganó
            gameState.setCatPosition(cat); // Esto fuerza updateGameStatus()
        } else {
            HexPosition nextCat = maybeNextPos.get();
            gameState.setCatPosition(nextCat); // Esto actualiza el estado internamente si corresponde
        }

        gameState.setMoveCount(gameState.getMoveCount() + 1);
        gameRepository.save(gameState);

        return Optional.of(gameState);
    }

    @Override
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        throw new UnsupportedOperationException("No implementado aún");
    }

    // Otros métodos sin implementar...
}

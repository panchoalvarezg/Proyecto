package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameState.GameStatus;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.impl.strategy.AStarCatMovement;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexGameBoard;

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

    public Optional<HexGameState> executePlayerMove(String gameId, HexPosition position, String playerId) {
        Optional<HexGameState> optional = getGameState(gameId);
        if (optional.isEmpty()) return Optional.empty();
        HexGameState gameState = optional.get();

        if (gameState.getStatus() != GameStatus.IN_PROGRESS) {
            return Optional.of(gameState);
        }

        HexGameBoard board = gameState.getGameBoard();
        HexPosition cat = gameState.getCatPosition();

        // Validar movimiento
        if (cat.equals(position) || board.getBlockedPositions().contains(position)) {
            return Optional.of(gameState);
        }

        // --- CORRECCIÓN: Método para bloquear la celda ---
        // Si tu HexGameBoard tiene addBlockedPosition, usa ese. Si no, reemplaza por el correcto.
        board.addBlockedPosition(position);

        // --- CORRECCIÓN: Lógica de movimiento del gato usando AStarCatMovement (o tu estrategia) ---
        // Si tu estrategia usa sólo la posición:
        AStarCatMovement movementStrategy = new AStarCatMovement(board);
        Optional<HexPosition> maybeNextPos = movementStrategy.getNextMove(cat);

        if (maybeNextPos.isEmpty()) {
            // --- CORRECCIÓN: Usa método público para cambiar el estado ---
            gameState.finishGame(GameStatus.PLAYER_WON); // O el método público equivalente
        } else {
            HexPosition nextCat = maybeNextPos.get();
            gameState.setCatPosition(nextCat);
            if (isBorder(nextCat, gameState.getBoardSize())) {
                gameState.finishGame(GameStatus.PLAYER_LOST); // O el método público equivalente
            }
        }

        gameState.setMoveCount(gameState.getMoveCount() + 1);
        gameRepository.save(gameState);

        return Optional.of(gameState);
    }

    private boolean isBorder(HexPosition pos, int boardSize) {
        int q = pos.getQ();
        int r = pos.getR();
        int s = -q - r;
        return Math.abs(q) == boardSize || Math.abs(r) == boardSize || Math.abs(s) == boardSize;
    }

    // Métodos sin implementar...
}

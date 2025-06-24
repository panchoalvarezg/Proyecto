package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HexGameService {

    // Mapa de juegos activos por su gameId
    private final Map<String, GameState<HexPosition>> games = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Crea un nuevo juego y lo registra en el mapa.
     * @param boardSize Tamaño del tablero.
     * @return El estado inicial del juego.
     */
    public GameState<HexPosition> startNewGame(int boardSize) {
        String gameId = UUID.randomUUID().toString();
        GameState<HexPosition> gameState = new HexGameState(gameId, boardSize);
        games.put(gameId, gameState);
        return gameState;
    }

    /**
     * Ejecuta el movimiento del jugador en el juego correspondiente.
     * @param gameId ID del juego
     * @param position Posición a bloquear por el jugador
     * @return Estado actualizado del juego, si existe el juego.
     */
    public Optional<GameState<HexPosition>> executePlayerMove(String gameId, HexPosition position) {
        GameState<HexPosition> gameState = games.get(gameId);
        if (gameState == null) {
            return Optional.empty();
        }
        if (gameState instanceof HexGameState hexGameState) {
            // 1. Bloquea la celda
            hexGameState.blockCell(position);

            // 2. Mueve el gato a un vecino libre (puedes mejorar esto con BFS)
            HexPosition cat = hexGameState.getCatPosition();
            List<HexPosition> neighbors = hexGameState.getFreeNeighbors(cat);

            if (!neighbors.isEmpty()) {
                // Mueve el gato a la primera celda libre (puedes elegir el camino más corto si lo deseas)
                HexPosition nextCat = neighbors.get(0);
                hexGameState.setCatPosition(nextCat);
            }
            // 3. Actualiza el estado del juego
            hexGameState.updateGameStatus();
        }
        return Optional.of(gameState);
    }

    /**
     * Permite consultar el estado actual de un juego por su ID.
     * @param gameId ID del juego
     * @return Optional con el estado del juego si existe.
     */
    public Optional<GameState<HexPosition>> getGameState(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }
}

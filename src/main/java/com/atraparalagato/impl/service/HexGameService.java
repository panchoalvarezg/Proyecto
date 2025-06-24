package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar los juegos de Hex.
 */
@Service
public class HexGameService {

    // Mapa de juegos activos por su gameId
    private final Map<String, GameState<HexPosition>> games = new ConcurrentHashMap<>();

    /**
     * Crea un nuevo juego y lo registra en el mapa.
     * @param boardSize Tamaño del tablero.
     * @return El estado inicial del juego.
     */
    public GameState<HexPosition> startNewGame(int boardSize) {
        // Posición inicial del gato: centro del tablero
        HexPosition initialCatPosition = new HexPosition(boardSize / 2, boardSize / 2);
        String gameId = UUID.randomUUID().toString();
        GameState<HexPosition> gameState = new HexGameState(gameId, initialCatPosition);
        games.put(gameId, gameState);
        return gameState;
    }

    /**
     * Ejecuta el movimiento del jugador en el juego correspondiente.
     * @param gameId ID del juego
     * @param position Posición a bloquear o mover (según la lógica)
     * @return Estado actualizado del juego, si existe el juego.
     */
    public Optional<GameState<HexPosition>> executePlayerMove(String gameId, HexPosition position) {
        GameState<HexPosition> gameState = games.get(gameId);
        if (gameState == null) {
            return Optional.empty();
        }
        // Aquí deberías actualizar el estado según tus reglas (bloquear una celda, mover el gato, etc.)
        gameState.executeMove(position);
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

    // Puedes agregar más métodos si lo necesitas (por ejemplo, eliminar juegos terminados).
}

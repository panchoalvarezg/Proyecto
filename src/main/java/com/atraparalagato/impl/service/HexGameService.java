package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio principal para manejar la lógica de los juegos de Hex.
 * Aquí se registran los juegos activos, se crean nuevos juegos,
 * se ejecutan movimientos y se puede consultar el estado actual del juego.
 */
public class HexGameService {

    // Almacena los juegos activos por su gameId
    private final Map<String, GameState<HexPosition>> games = new ConcurrentHashMap<>();

    /**
     * Crea un nuevo juego y lo registra en el mapa.
     * @param boardSize Tamaño del tablero (puedes ignorarlo o usarlo según tu implementación del estado)
     * @return El estado inicial del juego.
     */
    public GameState<HexPosition> startNewGame(int boardSize) {
        // Aquí deberías usar tu implementación de GameState, por ejemplo HexGameState
        // El constructor puede requerir boardSize y posición inicial del gato
        HexPosition initialCatPosition = new HexPosition(boardSize / 2, boardSize / 2); // Centro del tablero
        GameState<HexPosition> gameState = new com.atraparalagato.impl.model.HexGameState(UUID.randomUUID().toString(), initialCatPosition);
        // Opcional: podrías guardar el boardSize en el GameState si lo necesitas

        games.put(gameState.getGameId(), gameState);
        return gameState;
    }

    /**
     * Ejecuta el movimiento del jugador en el juego correspondiente.
     * @param gameId ID del juego
     * @param position Posición a bloquear
     * @return Estado actualizado del juego, si existe el juego.
     */
    public Optional<GameState<HexPosition>> executePlayerMove(String gameId, HexPosition position) {
        GameState<HexPosition> gameState = games.get(gameId);
        if (gameState == null) {
            return Optional.empty();
        }
        // Aquí deberías actualizar el estado según tus reglas (bloquear una celda, mover el gato, etc.)
        // Si tienes un método para bloquear celdas, llamalo aquí.
        // Ejemplo genérico:
        boolean moveSuccess = gameState.executeMove(position);
        // Puedes implementar lógica adicional para verificar si el juego terminó, etc.
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

    // Si necesitas más métodos (por ejemplo, para eliminar juegos terminados, estadísticas, etc.), agrégalos aquí.
}

package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;

import java.util.Optional;

/**
 * Servicio que orquesta la lógica del juego para el tablero hexagonal.
 */
public class HexGameService {

    private final DataRepository<HexGameState, String> repository;
    private final CatMovementStrategy<HexPosition> catStrategy;
    private HexGameState currentState;

    public HexGameService(DataRepository<HexGameState, String> repository,
                          CatMovementStrategy<HexPosition> catStrategy,
                          int boardSize,
                          HexPosition catStart,
                          String gameId) {
        this.repository = repository;
        this.catStrategy = catStrategy;
        HexGameBoard board = new HexGameBoard(boardSize);
        this.currentState = new HexGameState(gameId, board, catStart);
    }

    /**
     * Ejecuta un movimiento del jugador y luego mueve el gato si procede.
     * @param pos posición a bloquear
     * @return true si el movimiento fue exitoso, false si no se pudo ejecutar
     */
    public boolean executeMove(HexPosition pos) {
        boolean result = currentState.executeMove(pos);
        if (result) catMove();
        return result;
    }

    /**
     * Realiza el movimiento automático del gato usando su estrategia.
     */
    public void catMove() {
        Optional<HexPosition> best = catStrategy.findBestMove(
                currentState.getCatPosition(),
                null
        );
        best.ifPresent(newPos -> {
            currentState.setCatPosition(newPos);
            // Aquí puedes actualizar estado/juego terminado si hace falta
        });
    }

    /**
     * Sugiere el mejor movimiento para el jugador (usando la estrategia del gato).
     * @return posición sugerida o vacía si no hay sugerencia
     */
    public Optional<HexPosition> getSuggestedMove() {
        return catStrategy.findBestMove(currentState.getCatPosition(), null);
    }

    /**
     * Devuelve la posición actual del gato.
     */
    public HexPosition getTargetPosition() {
        return currentState.getCatPosition();
    }
}

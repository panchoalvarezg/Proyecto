package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.base.model.Position;
import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.base.repository.DataRepository;

import java.util.Map;

/**
 * Paradigma: Programación Orientada a Objetos y Buenas Prácticas
 */
public class HexGameService extends GameService {
    private final GameBoard board;
    private final GameState state;
    private final CatMovementStrategy catStrategy;
    private final DataRepository<GameState> repository;

    public HexGameService(GameBoard board, GameState state, CatMovementStrategy catStrategy, DataRepository<GameState> repository) {
        this.board = board;
        this.state = state;
        this.catStrategy = catStrategy;
        this.repository = repository;
    }

    @Override
    public void initializeGame(Map<String, Object> params) {
        // POO: inicialización
    }

    @Override
    public boolean isValidMove(Position from, Position to) {
        // POO: validación del movimiento
        return board.isValidMove(from, to) && !state.isGameFinished();
    }

    @Override
    public Position getSuggestedMove(Position catPosition) {
        // Orquestación: usa la estrategia
        return catStrategy.selectBestMove(catPosition, board);
    }

    @Override
    public Position getTargetPosition() {
        // POO: por ejemplo, devuelve el borde más cercano
        return null;
    }

    @Override
    public Map<String, Object> getGameStatistics() {
        // POO: recopila datos de estado
        return state.getSerializableState();
    }
}

package com.atraparalagato.impl.service;

import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.strategy.AStarCatMovement;
import com.atraparalagato.base.strategy.CatMovementStrategy;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HexGameService implements GameService<HexGameState, HexPosition> {

    private final ConcurrentHashMap<String, HexGameState> games = new ConcurrentHashMap<>();
    private final CatMovementStrategy<HexPosition> catMovement = new AStarCatMovement();

    @Override
    public HexGameState createGame(int boardSize, String difficulty) {
        String gameId = UUID.randomUUID().toString();
        HexPosition initialCat = new HexPosition(0, 0);
        HexGameState gameState = new HexGameState(gameId, boardSize, initialCat);
        gameState.setDifficulty(difficulty);
        games.put(gameId, gameState);
        return gameState;
    }

    @Override
    public Optional<HexGameState> getGame(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    @Override
    public boolean blockPosition(String gameId, HexPosition position) {
        HexGameState state = games.get(gameId);
        if (state == null || !state.getGameBoard().isValidMove(position)) {
            return false;
        }
        return state.executeMove(position);
    }

    @Override
    public boolean moveCat(String gameId) {
        HexGameState state = games.get(gameId);
        if (state == null || state.isGameFinished()) {
            return false;
        }
        Optional<HexPosition> next = catMovement.computeNextPosition(state);
        next.ifPresent(state::setCatPosition);
        return next.isPresent();
    }

    @Override
    public boolean isGameFinished(String gameId) {
        HexGameState state = games.get(gameId);
        return state != null && state.isGameFinished();
    }
}

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.Position;

import java.util.HashMap;
import java.util.Map;

/**
 * Paradigma: Programación Orientada a Objetos
 */
public class HexGameState extends GameState {
    private HexPosition catPosition;
    private boolean finished;
    private boolean playerWon;
    private int score;

    public HexGameState(HexPosition initialCatPosition) {
        this.catPosition = initialCatPosition;
        this.finished = false;
        this.playerWon = false;
        this.score = 0;
    }

    @Override
    public boolean canExecuteMove(Position from, Position to) {
        // POO: lógica de validación
        return !finished;
    }

    @Override
    public void performMove(Position from, Position to) {
        // POO: lógica de movimiento
        this.catPosition = (HexPosition) to;
        updateGameStatus();
    }

    @Override
    public void updateGameStatus() {
        // POO: lógica para victoria/derrota y score
        if (isAtEdge(catPosition)) {
            finished = true;
            playerWon = false;
        } else if (/* lógica de bloqueo */ false) {
            finished = true;
            playerWon = true;
        }
    }

    public boolean isAtEdge(HexPosition pos) {
        int size = 11; // TODO: parametrizar
        return pos.getQ() == 0 || pos.getQ() == size-1 || pos.getR() == 0 || pos.getR() == size-1;
    }

    @Override
    public Position getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(Position catPosition) {
        this.catPosition = (HexPosition) catPosition;
    }

    @Override
    public boolean isGameFinished() {
        return finished;
    }

    @Override
    public boolean hasPlayerWon() {
        return playerWon;
    }

    @Override
    public int calculateScore() {
        // POO: lógica de puntuación
        return score;
    }

    @Override
    public Map<String, Object> getSerializableState() {
        // POO: serialización del estado
        Map<String, Object> state = new HashMap<>();
        state.put("catPositionQ", catPosition.getQ());
        state.put("catPositionR", catPosition.getR());
        state.put("finished", finished);
        state.put("playerWon", playerWon);
        state.put("score", score);
        return state;
    }

    @Override
    public void restoreFromSerializable(Map<String, Object> state) {
        // POO: restaurar estado
        this.catPosition = new HexPosition(
                (int) state.get("catPositionQ"),
                (int) state.get("catPositionR")
        );
        this.finished = (boolean) state.get("finished");
        this.playerWon = (boolean) state.get("playerWon");
        this.score = (int) state.get("score");
    }
}

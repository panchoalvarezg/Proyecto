package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;

import java.util.HashMap;
import java.util.Map;

public class HexGameState extends GameState<HexPosition> {
    private HexPosition catPosition;
    private boolean playerWon;
    private int score;

    public HexGameState(String gameId, HexPosition initialCatPosition) {
        super(gameId);
        this.catPosition = initialCatPosition;
        this.playerWon = false;
        this.score = 0;
    }

    @Override
    protected boolean canExecuteMove(HexPosition position) {
        return status == GameStatus.IN_PROGRESS && !catPosition.equals(position);
    }

    @Override
    protected boolean performMove(HexPosition position) {
        if (!canExecuteMove(position)) return false;
        setCatPosition(position);
        return true;
    }

    @Override
    protected void updateGameStatus() {
        // Ejemplo: lógica básica, puedes mejorarla
        if (!catPosition.isWithinBounds(11)) { // Suponiendo un tablero de 11x11
            status = GameStatus.PLAYER_WON;
            playerWon = true;
        } else if (/* condición de perder */ false) {
            status = GameStatus.PLAYER_LOST;
            playerWon = false;
        } else {
            status = GameStatus.IN_PROGRESS;
        }
    }

    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }

    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
    }

    @Override
    public boolean isGameFinished() {
        return status != GameStatus.IN_PROGRESS;
    }

    @Override
    public boolean hasPlayerWon() {
        return playerWon;
    }

    @Override
    public int calculateScore() {
        return score;
    }

    @Override
    public Object getSerializableState() {
        Map<String, Object> map = new HashMap<>();
        map.put("catQ", catPosition.getQ());
        map.put("catR", catPosition.getR());
        map.put("playerWon", playerWon);
        map.put("score", score);
        map.put("status", status.name());
        map.put("moveCount", moveCount);
        map.put("gameId", gameId);
        map.put("createdAt", createdAt);
        return map;
    }

    @Override
    public void restoreFromSerializable(Object serializedState) {
        if (!(serializedState instanceof Map)) return;
        Map<?, ?> map = (Map<?, ?>) serializedState;
        int q = (int) map.get("catQ");
        int r = (int) map.get("catR");
        this.catPosition = new HexPosition(q, r);
        this.playerWon = (boolean) map.get("playerWon");
        this.score = (int) map.get("score");
        this.status = GameStatus.valueOf((String) map.get("status"));
    }
}

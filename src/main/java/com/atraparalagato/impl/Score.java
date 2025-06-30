package com.example.proyecto.impl;

import jakarta.persistence.*;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameId;
    private String playerName;
    private int movesCount;
    private boolean playerWon;
    private int boardSize;
    private long gameDurationSeconds;

    public Score() {}

    public Score(String gameId, String playerName, int movesCount, boolean playerWon, int boardSize, long gameDurationSeconds) {
        this.gameId = gameId;
        this.playerName = playerName;
        this.movesCount = movesCount;
        this.playerWon = playerWon;
        this.boardSize = boardSize;
        this.gameDurationSeconds = gameDurationSeconds;
    }

    public Long getId() { return id; }
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getMovesCount() { return movesCount; }
    public void setMovesCount(int movesCount) { this.movesCount = movesCount; }
    public boolean isPlayerWon() { return playerWon; }
    public void setPlayerWon(boolean playerWon) { this.playerWon = playerWon; }
    public int getBoardSize() { return boardSize; }
    public void setBoardSize(int boardSize) { this.boardSize = boardSize; }
    public long getGameDurationSeconds() { return gameDurationSeconds; }
    public void setGameDurationSeconds(long gameDurationSeconds) { this.gameDurationSeconds = gameDurationSeconds; }
}

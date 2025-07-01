package com.atraparalagato.impl.model;

import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class GameScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gameid", nullable = false)
    private String gameId; // NUEVO: campo obligatorio GameId

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "score")
    private Integer score;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}

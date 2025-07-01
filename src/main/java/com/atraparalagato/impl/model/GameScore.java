package com.atraparalagato.impl.model;

import jakarta.persistence.*;

@Entity
@Table(name = "GAMES")
public class GameScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PLAYER_NAME")
    private String playerName;

    @Column(name = "SCORE")
    private Integer score;

    // Otros campos opcionales, como fecha, boardSize, etc.

    // Getters y setters
    public Long getId() { return id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}

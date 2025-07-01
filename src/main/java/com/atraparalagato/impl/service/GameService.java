package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.GameScore;
import com.atraparalagato.impl.repository.GameScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameScoreRepository gameScoreRepository;

    @Autowired
    public GameService(GameScoreRepository gameScoreRepository) {
        this.gameScoreRepository = gameScoreRepository;
    }

    // Guarda el puntaje
    public void saveScore(String gameId, String playerName, Integer score) {
        GameScore gameScore = new GameScore();
        gameScore.setGameId(gameId);
        gameScore.setPlayerName(playerName);
        gameScore.setScore(score);
        gameScoreRepository.save(gameScore);
    }

    // Devuelve todos los puntajes guardados
    public List<GameScore> getAllScores() {
        return gameScoreRepository.findAll();
    }
}

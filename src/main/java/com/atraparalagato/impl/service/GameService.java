package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.GameScore;
import com.atraparalagato.impl.repository.GameScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameScoreRepository gameScoreRepository;

    @Autowired
    public GameService(GameScoreRepository gameScoreRepository) {
        this.gameScoreRepository = gameScoreRepository;
    }

    public void saveScore(String playerName, Integer score) {
        GameScore gameScore = new GameScore();
        gameScore.setPlayerName(playerName);
        gameScore.setScore(score);
        gameScoreRepository.save(gameScore);
    }
}

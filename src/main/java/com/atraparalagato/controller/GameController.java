package com.atraparalagato.controller;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.HexGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    private final HexGameService hexGameService;

    @Autowired
    public GameController(HexGameService hexGameService) {
        this.hexGameService = hexGameService;
    }

    @GetMapping("/start")
    public ResponseEntity<Map<String, Object>> startGame(@RequestParam(defaultValue = "11") int boardSize) {
        GameState<HexPosition> gameState = hexGameService.startNewGame(boardSize);
        return ResponseEntity.ok(gameStateToMap(gameState));
    }

    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockPosition(
            @RequestParam String gameId,
            @RequestParam int q,
            @RequestParam int r) {

        Optional<GameState<HexPosition>> gameStateOpt = hexGameService.executePlayerMove(gameId, new HexPosition(q, r));
        if (gameStateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GameState<HexPosition> gameState = gameStateOpt.get();
        return ResponseEntity.ok(gameStateToMap(gameState));
    }

    @GetMapping("/state/{gameId}")
    public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String gameId) {
        Optional<GameState<HexPosition>> gameStateOpt = hexGameService.getGameState(gameId);
        if (gameStateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        GameState<HexPosition> gameState = gameStateOpt.get();
        return ResponseEntity.ok(gameStateToMap(gameState));
    }

    // Utilidad para construir la respuesta JSON
    private Map<String, Object> gameStateToMap(GameState<HexPosition> gameState) {
        Map<String, Object> response = new HashMap<>();
        HexPosition cat = gameState.getCatPosition();
        response.put("gameId", gameState.getGameId());
        response.put("cat", Map.of("q", cat.getQ(), "r", cat.getR()));
        response.put("movesCount", gameState.getMoveCount());
        response.put("status", gameState.getStatus() != null ? gameState.getStatus().toString() : "IN_PROGRESS");
        response.put("implementation", "impl");

        if (gameState instanceof HexGameState hexGameState) {
            response.put("boardSize", hexGameState.getBoardSize());
            response.put("blockedCells",
                hexGameState.getBlockedPositions()
                    .stream()
                    .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                    .collect(Collectors.toList())
            );
        } else {
            response.put("boardSize", 11);
            response.put("blockedCells", new ArrayList<>());
        }

        return response;
    }
}

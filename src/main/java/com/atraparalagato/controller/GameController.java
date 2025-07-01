package com.atraparalagato.controller;

import com.atraparalagato.example.service.ExampleGameService;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.GameService;
import com.atraparalagato.impl.model.GameScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Value("${game.use-example-implementation:true}")
    private boolean useExampleImplementation;

    private final ExampleGameService exampleGameService;
    private final GameService gameService;

    private final Map<String, HexGameState> studentGames = new HashMap<>();

    @Autowired
    public GameController(GameService gameService) {
        this.exampleGameService = new ExampleGameService();
        this.gameService = gameService;
    }

    /**
     * Inicia un nuevo juego.
     */
    @GetMapping("/start")
    public ResponseEntity<Map<String, Object>> startGame(@RequestParam(defaultValue = "5") int boardSize) {
        try {
            if (useExampleImplementation) {
                return startGameWithExample(boardSize);
            } else {
                return startGameWithStudentImplementation(boardSize);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al iniciar el juego: " + e.getMessage()));
        }
    }

    /**
     * Ejecuta un movimiento del jugador.
     */
    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockPosition(
            @RequestParam String gameId,
            @RequestParam int q,
            @RequestParam int r) {
        try {
            HexPosition position = new HexPosition(q, r);

            if (useExampleImplementation) {
                return blockPositionWithExample(gameId, position);
            } else {
                return blockPositionWithStudentImplementation(gameId, position);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al ejecutar movimiento: " + e.getMessage()));
        }
    }

    /**
     * Obtiene el estado actual del juego.
     */
    @GetMapping("/state/{gameId}")
    public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String gameId) {
        try {
            if (useExampleImplementation) {
                return getGameStateWithExample(gameId);
            } else {
                return getGameStateWithStudentImplementation(gameId);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener estado del juego: " + e.getMessage()));
        }
    }

    /**
     * Obtiene estadísticas del juego.
     */
    @GetMapping("/statistics/{gameId}")
    public ResponseEntity<Map<String, Object>> getGameStatistics(@PathVariable String gameId) {
        try {
            if (useExampleImplementation) {
                Map<String, Object> stats = exampleGameService.getGameStatistics(gameId);
                return ResponseEntity.ok(stats);
            } else {
                // Puedes implementar estadísticas reales si lo deseas
                return ResponseEntity.ok(Map.of("error", "Student implementation not available yet"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * Obtiene sugerencia de movimiento.
     */
    @GetMapping("/suggestion/{gameId}")
    public ResponseEntity<Map<String, Object>> getSuggestion(@PathVariable String gameId) {
        try {
            if (useExampleImplementation) {
                Optional<HexPosition> suggestion = exampleGameService.getSuggestedMove(gameId);
                if (suggestion.isPresent()) {
                    HexPosition pos = suggestion.get();
                    return ResponseEntity.ok(Map.of(
                            "suggestion", Map.of("q", pos.getQ(), "r", pos.getR()),
                            "message", "Sugerencia: bloquear posición adyacente al gato"
                    ));
                } else {
                    return ResponseEntity.ok(Map.of("message", "No hay sugerencias disponibles"));
                }
            } else {
                return ResponseEntity.ok(Map.of("error", "Student implementation not available yet"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener sugerencia: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para guardar el nombre y el puntaje del jugador (ahora también requiere gameId).
     * POST /api/game/save-score?gameId=...&playerName=...&score=...
     */
    @PostMapping("/save-score")
    public ResponseEntity<?> saveScore(
            @RequestParam String gameId,
            @RequestParam String playerName,
            @RequestParam Integer score
    ) {
        try {
            gameService.saveScore(gameId, playerName, score);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al guardar la puntuación: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener todas las puntuaciones guardadas.
     * GET /api/game/scores
     */
    @GetMapping("/scores")
    public ResponseEntity<List<GameScore>> getScores() {
        try {
            List<GameScore> scores = gameService.getAllScores();
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene información sobre qué implementación se está usando.
     */
    @GetMapping("/implementation-info")
    public ResponseEntity<Map<String, Object>> getImplementationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("useExampleImplementation", useExampleImplementation);
        info.put("currentImplementation", useExampleImplementation ? "example" : "impl");
        info.put("description", useExampleImplementation ?
                "Usando implementaciones de ejemplo (básicas)" :
                "Usando implementaciones de estudiantes");

        return ResponseEntity.ok(info);
    }

    // Métodos privados para implementación de ejemplo

    private ResponseEntity<Map<String, Object>> startGameWithExample(int boardSize) {
        var gameState = exampleGameService.startNewGame(boardSize);

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        response.put("catPosition", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("boardSize", boardSize);
        response.put("implementation", "example");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> blockPositionWithExample(String gameId, HexPosition position) {
        var gameStateOpt = exampleGameService.executePlayerMove(gameId, position);

        if (gameStateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var gameState = gameStateOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        response.put("catPosition", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("implementation", "example");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> getGameStateWithExample(String gameId) {
        var gameStateOpt = exampleGameService.getGameState(gameId);

        if (gameStateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var gameState = gameStateOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        response.put("catPosition", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("implementation", "example");

        return ResponseEntity.ok(response);
    }

    // Métodos privados para implementación de estudiantes

    private ResponseEntity<Map<String, Object>> startGameWithStudentImplementation(int boardSize) {
        HexGameBoard board = new HexGameBoard(boardSize);
        int center = 0;
        HexPosition cat = new HexPosition(center, center);
        String gameId = UUID.randomUUID().toString();
        HexGameState state = new HexGameState(gameId, board, cat);

        studentGames.put(gameId, state);

        Map<String, Object> response = (Map<String, Object>) state.getSerializableState();
        response.put("implementation", "impl");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> blockPositionWithStudentImplementation(String gameId, HexPosition position) {
        HexGameState state = studentGames.get(gameId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }

        boolean success = state.executeMove(position);

        Map<String, Object> response = (Map<String, Object>) state.getSerializableState();
        response.put("implementation", "impl");
        response.put("moveSuccess", success);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> getGameStateWithStudentImplementation(String gameId) {
        HexGameState state = studentGames.get(gameId);
        if (state == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = (Map<String, Object>) state.getSerializableState();
        response.put("implementation", "impl");
        return ResponseEntity.ok(response);
    }
}

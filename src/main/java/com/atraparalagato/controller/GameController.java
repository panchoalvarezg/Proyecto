package com.atraparalagato.controller;

import com.atraparalagato.example.model.ExampleGameState;
import com.atraparalagato.example.service.ExampleGameService;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.HexGameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del juego que alterna entre implementaciones de ejemplo y de estudiantes.
 * Usa la propiedad 'game.use-example-implementation' para determinar qué implementación usar.
 */
@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Value("${game.use-example-implementation:true}")
    private boolean useExampleImplementation;

    private final ExampleGameService exampleGameService;
    private final HexGameService hexGameService;

    public GameController() {
        this.exampleGameService = new ExampleGameService();
        this.hexGameService = new HexGameService();
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
     * Mejora: valida primero la posición antes de estado del juego.
     */
    @PostMapping("/block")
    public ResponseEntity<Map<String, Object>> blockPosition(
            @RequestParam String gameId,
            @RequestParam int q,
            @RequestParam int r) {
        System.out.println("[/api/game/block] Intentando bloquear posición: gameId=" + gameId + ", q=" + q + ", r=" + r);

        try {
            int boardSize;
            if (useExampleImplementation) {
                Optional<ExampleGameState> gameStateOpt = exampleGameService.getGameState(gameId);
                if (gameStateOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Partida no encontrada."));
                }
                boardSize = gameStateOpt.get().getGameBoard().getSize();
            } else {
                Optional<HexGameState> gameStateOpt = hexGameService.getGameState(gameId);
                if (gameStateOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Partida no encontrada."));
                }
                boardSize = gameStateOpt.get().getGameBoard().getBoardSize();
            }
            int s = -q - r;
            if (Math.abs(q) >= boardSize || Math.abs(r) >= boardSize || Math.abs(s) >= boardSize) {
                System.out.println("[/api/game/block] Celda inválida: fuera del tablero.");
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("error", "Celda inválida: fuera del tablero."));
            }

            // 2. Validar estado del juego
            boolean inProgress;
            if (useExampleImplementation) {
                Optional<ExampleGameState> gameStateOpt = exampleGameService.getGameState(gameId);
                if (gameStateOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Partida no encontrada."));
                }
                inProgress = "IN_PROGRESS".equalsIgnoreCase(gameStateOpt.get().getStatus().toString());
            } else {
                Optional<HexGameState> gameStateOpt = hexGameService.getGameState(gameId);
                if (gameStateOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("error", "Partida no encontrada."));
                }
                inProgress = "IN_PROGRESS".equalsIgnoreCase(gameStateOpt.get().getStatus().toString());
            }
            if (!inProgress) {
                System.out.println("[/api/game/block] La partida no está en progreso.");
                return ResponseEntity.badRequest().body(Map.of("error", "La partida no está en progreso."));
            }

            // 3. Procesar el movimiento normalmente
            HexPosition position = new HexPosition(q, r);
            System.out.println("[/api/game/block] HexPosition creada: " + position);

            Map<String, Object> response;
            if (useExampleImplementation) {
                Optional<ExampleGameState> result = exampleGameService.executePlayerMove(gameId, position);
                if (result.isEmpty()) {
                    System.out.println("[/api/game/block] Movimiento inválido o partida no encontrada para gameId=" + gameId);
                    return ResponseEntity.badRequest().body(Map.of("error", "Movimiento inválido o partida no encontrada."));
                }
                ExampleGameState state = result.get();

                response = new HashMap<>();
                response.put("gameId", state.getGameId());
                response.put("status", state.getStatus().toString());
                response.put("catPosition", Map.of("q", state.getCatPosition().getQ(), "r", state.getCatPosition().getR()));
                response.put("blockedCells", state.getGameBoard().getBlockedPositions());
                response.put("movesCount", state.getMoveCount());
                response.put("implementation", "example");
            } else {
                Optional<HexGameState> result = hexGameService.executePlayerMove(gameId, position, null);
                if (result.isEmpty()) {
                    System.out.println("[/api/game/block] Movimiento inválido o partida no encontrada para gameId=" + gameId);
                    return ResponseEntity.badRequest().body(Map.of("error", "Movimiento inválido o partida no encontrada."));
                }
                HexGameState state = result.get();

                response = new HashMap<>();
                response.put("gameId", state.getGameId());
                response.put("status", state.getStatus().toString());
                response.put("catPosition", Map.of("q", state.getCatPosition().getQ(), "r", state.getCatPosition().getR()));
                response.put("blockedCells", state.getGameBoard().getBlockedPositions());
                response.put("movesCount", state.getMoveCount());
                response.put("implementation", "impl");
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            System.out.println("[/api/game/block] Movimiento inválido (IllegalArgumentException): " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println("[/api/game/block] Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Error interno al ejecutar movimiento: " + e.getMessage()));
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
                // Ejemplo: puedes usar hexGameService.getGameStatistics(gameId) si implementas el método
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
                // Ejemplo: puedes usar hexGameService.getIntelligentSuggestion(gameId, dificultad) si implementas el método
                return ResponseEntity.ok(Map.of("error", "Student implementation not available yet"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener sugerencia: " + e.getMessage()));
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
        HexGameState gameState = hexGameService.createGame(boardSize, "EASY", Map.of());

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        response.put("catPosition", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("boardSize", boardSize);
        response.put("implementation", "impl");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> getGameStateWithStudentImplementation(String gameId) {
        var gameStateOpt = hexGameService.getGameState(gameId);
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
        response.put("implementation", "impl");

        return ResponseEntity.ok(response);
    }
}

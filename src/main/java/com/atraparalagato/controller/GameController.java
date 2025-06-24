
     */
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
        response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
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
        response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("implementation", "example");

        return ResponseEntity.ok(response);
    }

    // Métodos privados para implementación de estudiantes (real)

    private ResponseEntity<Map<String, Object>> startGameWithStudentImplementation(int boardSize) {
        var gameState = hexGameService.startNewGame(boardSize);

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        // Asegúrate que getCatPosition() nunca sea null
        response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("boardSize", boardSize);
        response.put("implementation", "impl");

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> blockPositionWithStudentImplementation(String gameId, HexPosition position) {
        var gameStateOpt = hexGameService.executePlayerMove(gameId, position);

        if (gameStateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var gameState = gameStateOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("gameId", gameState.getGameId());
        response.put("status", gameState.getStatus().toString());
        response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
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
        response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
        response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
        response.put("movesCount", gameState.getMoveCount());
        response.put("implementation", "impl");

        return ResponseEntity.ok(response);
    }
}

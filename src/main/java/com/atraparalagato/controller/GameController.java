private ResponseEntity<Map<String, Object>> startGameWithExample(int boardSize) {
    var gameState = exampleGameService.startNewGame(boardSize);

    Map<String, Object> response = new HashMap<>();
    response.put("gameId", gameState.getGameId());
    response.put("status", gameState.getStatus().toString());
    response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
    response.put("movesCount", gameState.getMoveCount());
    response.put("boardSize", boardSize);
    response.put("implementation", "example");
    // Agrega las celdas bloqueadas
    response.put("blockedCells",
        gameState.getBlockedPositions()
                 .stream()
                 .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                 .collect(Collectors.toList())
    );
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
    response.put("movesCount", gameState.getMoveCount());
    response.put("implementation", "example");
    // Agrega las celdas bloqueadas
    response.put("blockedCells",
        gameState.getBlockedPositions()
                 .stream()
                 .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                 .collect(Collectors.toList())
    );
    return ResponseEntity.ok(response);
}

private ResponseEntity<Map<String, Object>> startGameWithStudentImplementation(int boardSize) {
    GameState<HexPosition> gameState = hexGameService.startNewGame(boardSize);

    Map<String, Object> response = new HashMap<>();
    response.put("gameId", gameState.getGameId());
    response.put("status", gameState.getStatus().toString());
    response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
    response.put("movesCount", gameState.getMoveCount());
    response.put("boardSize", boardSize);
    response.put("implementation", "impl");
    // Agrega las celdas bloqueadas
    response.put("blockedCells",
        gameState.getBlockedPositions()
                 .stream()
                 .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                 .collect(Collectors.toList())
    );
    return ResponseEntity.ok(response);
}

private ResponseEntity<Map<String, Object>> blockPositionWithStudentImplementation(String gameId, HexPosition position) {
    Optional<GameState<HexPosition>> gameStateOpt = hexGameService.executePlayerMove(gameId, position);

    if (gameStateOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    GameState<HexPosition> gameState = gameStateOpt.get();

    Map<String, Object> response = new HashMap<>();
    response.put("gameId", gameState.getGameId());
    response.put("status", gameState.getStatus().toString());
    response.put("cat", Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
    response.put("movesCount", gameState.getMoveCount());
    response.put("implementation", "impl");
    // Agrega las celdas bloqueadas
    response.put("blockedCells",
        gameState.getBlockedPositions()
                 .stream()
                 .map(pos -> Map.of("q", pos.getQ(), "r", pos.getR()))
                 .collect(Collectors.toList())
    );
    return ResponseEntity.ok(response);
}

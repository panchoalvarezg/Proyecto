package com.atraparalagato.api;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.HexGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final HexGameService gameService;

    @Autowired
    public GameController(HexGameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public GameState<HexPosition> startNewGame(@RequestParam(defaultValue = "7") int boardSize) {
        // Llama a la implementación real
        return gameService.startNewGame(boardSize);
    }

    // Puedes agregar otros endpoints aquí para movimientos, estadísticas, etc.
}

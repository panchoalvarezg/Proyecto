package com.atraparalagato.impl.service;

import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class HexGameService {

    private final HexGameRepository repository;

    public HexGameService(HexGameRepository repository) {
        this.repository = repository;
    }

    // ... otros métodos como createGame, etc ...

    public HexGameState executePlayerMove(String gameId, int q, int r) {
        // 1. Buscar el juego por ID
        Optional<HexGameState> optState = repository.findById(gameId);
        if (optState.isEmpty()) {
            throw new IllegalArgumentException("Juego no encontrado");
        }
        HexGameState state = optState.get();

        // 2. Verificar que el juego esté en progreso
        if (!"IN_PROGRESS".equals(state.getStatus())) {
            throw new IllegalStateException("El juego ya está finalizado");
        }

        HexGameBoard board = state.getGameBoard();
        HexPosition cat = state.getCatPosition();

        // 3. Validar que no se bloquee la posición del gato ni una celda ya bloqueada
        if ((cat.getQ() == q && cat.getR() == r) || board.isBlocked(q, r)) {
            throw new IllegalArgumentException("Celda no válida para bloquear");
        }

        // 4. Bloquear la celda
        board.blockCell(q, r);

        // 5. Mover el gato automáticamente
        HexPosition nextCatPos = findNextCatPosition(cat, board, state.getBoardSize());
        if (nextCatPos == null) {
            // El gato no puede moverse: gana el jugador
            state.setStatus("PLAYER_WON");
        } else {
            state.setCatPosition(nextCatPos);
            // Si el gato llega al borde, pierdes
            if (isBorder(nextCatPos, state.getBoardSize())) {
                state.setStatus("PLAYER_LOST");
            }
        }

        // 6. Actualizar el contador de movimientos
        state.setMoveCount(state.getMoveCount() + 1);

        // 7. Guardar el estado actualizado
        repository.save(state);

        // 8. Retornar el nuevo estado
        return state;
    }

    // Chequea si la posición está en el borde del tablero
    private boolean isBorder(HexPosition pos, int boardSize) {
        int q = pos.getQ();
        int r = pos.getR();
        int s = -q - r;
        return Math.abs(q) == boardSize || Math.abs(r) == boardSize || Math.abs(s) == boardSize;
    }

    // Devuelve la siguiente posición del gato, o null si no puede moverse
    private HexPosition findNextCatPosition(HexPosition cat, HexGameBoard board, int boardSize) {
        // Direcciones hexagonales (6)
        int[][] dirs = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        for (int[] dir : dirs) {
            int nq = cat.getQ() + dir[0];
            int nr = cat.getR() + dir[1];
            // No salir del tablero y no ir a una celda bloqueada
            if (isInBoard(nq, nr, boardSize) && !board.isBlocked(nq, nr)) {
                return new HexPosition(nq, nr);
            }
        }
        // Si no hay movimientos posibles
        return null;
    }

    // Chequea si la celda está dentro de los límites del tablero
    private boolean isInBoard(int q, int r, int boardSize) {
        int s = -q - r;
        return Math.abs(q) <= boardSize && Math.abs(r) <= boardSize && Math.abs(s) <= boardSize;
    }
}

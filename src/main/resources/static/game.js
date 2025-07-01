class Game {
    constructor() {
        this.gameId = null;
        this.boardSize = 5;
        this.board = document.getElementById('board');
        this.statusElement = document.getElementById('game-status');
        this.movesElement = document.getElementById('moves-count');
        this.startButton = document.getElementById('start-game');
        this.playerNameInput = document.getElementById('player-name');
        this.initializeEventListeners();
    }

    initializeEventListeners() {
        this.startButton.addEventListener('click', () => this.startNewGame());
    }

    createApiCall(url, options = {}) {
        return async () => {
            try {
                const response = await fetch(url, {
                    headers: { 'Accept': 'application/json' },
                    ...options
                });
                return await response.json();
            } catch (error) {
                console.error('API Error:', error);
                throw error;
            }
        };
    }

    async startNewGame() {
        try {
            this.removeGameOverDialogs();
            const apiCall = this.createApiCall(`/api/game/start?boardSize=${this.boardSize}`, {
                method: 'GET'
            });
            const gameState = await apiCall();
            this.gameId = gameState.gameId;
            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);
        } catch (error) {
            this.handleError('Error al iniciar el juego', error);
        }
    }

    removeGameOverDialogs() {
        const existingDialogs = document.querySelectorAll('.game-over');
        existingDialogs.forEach(dialog => dialog.remove());
    }

    async makeMove(q, r) {
        if (!this.gameId) return;
        try {
            const apiCall = this.createApiCall(
                `/api/game/block?gameId=${this.gameId}&q=${q}&r=${r}`,
                { method: 'POST' }
            );
            const gameState = await apiCall();
            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);

            // NUEVO: Si el gato está en el borde, el jugador pierde
            if (this.isCatAtBorder(gameState.catPosition)) {
                this.updateStatus('PLAYER_LOST');
                this.showGameOver('PLAYER_LOST');
                return;
            }

            // También mostramos el game over si el backend ya lo indicó
            const gameEndStates = ['PLAYER_LOST', 'PLAYER_WON'];
            const isGameOver = gameEndStates.includes(gameState.status);
            if (isGameOver) {
                this.showGameOver(gameState.status);
            }
        } catch (error) {
            this.handleError('Error al realizar el movimiento', error);
        }
    }

    // NUEVO: Determina si el gato está en el borde jugable (radio visual)
    isCatAtBorder(catPosition) {
        if (!catPosition) return false;
        const q = catPosition.q;
        const r = catPosition.r;
        const s = -q - r;
        const radius = this.boardSize - 1;
        const max = Math.max(Math.abs(q), Math.abs(r), Math.abs(s));
        return max === radius;
    }

    updateMovesCount(count) {
        this.movesElement.textContent = count;
    }

    renderBoard(gameState) {
        this.board.innerHTML = '';
        const boardConfig = {
            hexSize: 25,
            centerX: 375,
            centerY: 375,
            cellWidth: 40,
            cellHeight: 46
        };
        const cells = this.generateCellPositions(boardConfig)
            .map(cell => this.createHexCell(cell, gameState, boardConfig));
        cells.forEach(cell => this.board.appendChild(cell));
    }

    generateCellPositions(config) {
        const positions = [];
        const radius = this.boardSize - 1;
        for (let q = -radius; q <= radius; q++) {
            for (let r = -radius; r <= radius; r++) {
                const s = -q - r;
                if (Math.max(Math.abs(q), Math.abs(r), Math.abs(s)) <= radius) {
                    const x = config.centerX + config.hexSize * (3/2 * q);
                    const y = config.centerY + config.hexSize * (Math.sqrt(3)/2 * q + Math.sqrt(3) * r);
                    const isBorder = Math.max(Math.abs(q), Math.abs(r), Math.abs(s)) === radius;
                    const type = isBorder ? 'border' : 'playable';
                    positions.push({ q, r, x, y, type });
                }
            }
        }
        return positions;
    }

    createHexCell(position, gameState, config) {
        const cell = document.createElement('div');
        if (position.type === 'border') {
            cell.className = 'hex-cell border-cell';
            cell.style.opacity = '0.3';
        } else {
            cell.className = 'hex-cell';
        }
        cell.style.left = `${position.x - config.cellWidth/2}px`;
        cell.style.top = `${position.y - config.cellHeight/2}px`;
        cell.setAttribute('data-q', position.q);
        cell.setAttribute('data-r', position.r);
        cell.setAttribute('data-type', position.type);

        const isCatPosition = this.isCatAt(position.q, position.r, gameState);
        const isBlocked = this.isCellBlocked(position.q, position.r, gameState);

        if (isCatPosition) {
            cell.classList.add('cat');
        } else if (isBlocked) {
            cell.classList.add('blocked');
        } else {
            const moveHandler = this.createMoveHandler(position.q, position.r);
            cell.addEventListener('click', moveHandler);
        }
        return cell;
    }

    createMoveHandler(q, r) {
        return () => this.makeMove(q, r);
    }

    isCatAt(q, r, gameState) {
        return q === gameState.catPosition.q && r === gameState.catPosition.r;
    }

    isCellBlocked(q, r, gameState) {
        return gameState.blockedCells.some(pos => pos.q === q && pos.r === r);
    }

    updateStatus(status) {
        const statusMessages = {
            'IN_PROGRESS': 'En progreso',
            'PLAYER_LOST': '¡El gato escapó!',
            'PLAYER_WON': '¡Atrapaste al gato!'
        };
        this.statusElement.textContent = statusMessages[status] || 'Estado desconocido';
    }

    showGameOver(status) {
        this.removeGameOverDialogs();
        const messages = {
            'PLAYER_LOST': '¡El gato escapó! Mejor suerte la próxima vez.',
            'PLAYER_WON': '¡Felicidades! ¡Atrapaste al gato!'
        };
        const message = messages[status] || 'Juego terminado';
        const gameOver = this.createGameOverDialog(message, status);
        document.body.appendChild(gameOver);
    }

    createGameOverDialog(message, status) {
        const gameOver = document.createElement('div');
        gameOver.className = 'game-over';
        const playerName = this.playerNameInput.value.trim() || 'Anónimo';
        gameOver.innerHTML = `
            <h2>${message}</h2>
            <p>Jugador: ${playerName}</p>
            <p>Movimientos: ${this.movesElement.textContent}</p>
            <div>
                <button onclick="game.saveScore('${this.gameId}', document.getElementById('player-name').value)">Guardar Puntuación</button>
                <button onclick="game.closeGameOverDialog()">Cerrar</button>
                <button onclick="game.startNewGame()">Nuevo Juego</button>
            </div>
        `;
        return gameOver;
    }

    closeGameOverDialog() {
        this.removeGameOverDialogs();
    }

    handleError(message, error) {
        console.error(message, error);
        alert(message);
    }

    // Cambia el método para pasar el gameId en la petición (con gameId en el query)
    async saveScore(gameId, playerName) {
        const score = calcularPuntajeActual();
        try {
            const response = await fetch(`/api/game/save-score?gameId=${encodeURIComponent(gameId)}&playerName=${encodeURIComponent(playerName)}&score=${score}`, {
                method: 'POST',
                headers: { 'Accept': 'application/json' }
            });
            if (response.ok) {
                alert('¡Puntuación guardada exitosamente!');
            } else {
                throw new Error('Error al guardar puntuación');
            }
        } catch (error) {
            alert('Error al guardar la puntuación');
        }
    }
}

/**
 * Ejemplo de función para calcular el puntaje actual.
 * Debes reemplazarla por la lógica real de tu juego.
 * @returns {number}
 */
function calcularPuntajeActual() {
    // TODO: Reemplaza con el cálculo real del puntaje del juego
    return Math.floor(Math.random() * 1000);
}

const initializeGame = () => {
    window.game = new Game();
    return window.game;
};

window.addEventListener('load', initializeGame);

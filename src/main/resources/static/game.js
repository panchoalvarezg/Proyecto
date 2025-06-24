// SOLID Principles Applied:
// S - Single Responsibility: Each class/function has one clear purpose
// O - Open/Closed: Game class is open for extension, closed for modification
// L - Liskov Substitution: Methods can be overridden without breaking functionality
// I - Interface Segregation: Clean, focused interfaces
// D - Dependency Inversion: Game depends on abstractions, not concrete implementations

// Higher-Order Functions and Functional Programming concepts applied throughout

class Game {
    constructor() {
        this.gameId = null;
        this.boardSize = 5;
        this.board = document.getElementById('board');
        this.statusElement = document.getElementById('game-status');
        this.movesElement = document.getElementById('moves-count');
        this.startButton = document.getElementById('start-game');
        this.playerNameInput = document.getElementById('player-name');
        
        // Dependency Injection - Game depends on DOM abstractions
        this.initializeEventListeners();
    }
    
    // Single Responsibility: Initialize event listeners only
    initializeEventListeners() {
        this.startButton.addEventListener('click', () => this.startNewGame());
    }
    
    // Higher-Order Function: Returns a function that handles API calls
    createApiCall(url, options = {}) {
        return async () => {
            try {
                const response = await fetch(url, {
                    headers: { 'Accept': 'application/json' },
                    ...options
                });
                // Error handling: Throw if response is not ok
                if (!response.ok) {
                    const text = await response.text();
                    throw new Error(`API Error: ${response.status} - ${text}`);
                }
                // Try/catch JSON parsing for clarity
                try {
                    return await response.json();
                } catch (jsonError) {
                    throw new Error('Respuesta JSON inválida del servidor');
                }
            } catch (error) {
                console.error('API Error:', error);
                throw error;
            }
        };
    }
    
    // Single Responsibility: Start new game only
    async startNewGame() {
        try {
            this.removeGameOverDialogs();

            // Debug: log startNewGame
            console.log("Iniciando nuevo juego...");
            const apiCall = this.createApiCall(`/api/game/start?boardSize=${this.boardSize}`, {
                method: 'GET'
            });
            
            const gameState = await apiCall();
            // Debug: log backend response
            console.log("Respuesta backend:", gameState);

            // Validar campos esperados
            if (!gameState || !gameState.gameId || !gameState.cat || typeof gameState.movesCount === "undefined" || !gameState.status) {
                throw new Error('Respuesta inesperada del backend');
            }

            this.gameId = gameState.gameId;
            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);
        } catch (error) {
            this.handleError('Error al iniciar el juego', error);
        }
    }
    
    // Single Responsibility: Remove game over dialogs
    removeGameOverDialogs() {
        const existingDialogs = document.querySelectorAll('.game-over');
        existingDialogs.forEach(dialog => dialog.remove());
    }
    
    // Single Responsibility: Handle player moves only
    async makeMove(q, r) {
        if (!this.gameId) return;
        
        try {
            const apiCall = this.createApiCall(
                `/api/game/block?gameId=${this.gameId}&q=${q}&r=${r}`, 
                { method: 'POST' }
            );
            
            const gameState = await apiCall();
            // Debug: log backend move response
            console.log("Respuesta movimiento:", gameState);

            this.renderBoard(gameState);
            this.updateStatus(gameState.status);
            this.updateMovesCount(gameState.movesCount || 0);
            
            // Functional approach: Use filter to check game end conditions
            const gameEndStates = ['PLAYER_LOST', 'PLAYER_WON'];
            const isGameOver = gameEndStates.some(state => state === gameState.status);
            
            if (isGameOver) {
                this.showGameOver(gameState.status);
            }
        } catch (error) {
            this.handleError('Error al realizar el movimiento', error);
        }
    }
    
    // Single Responsibility: Update moves count display
    updateMovesCount(count) {
        this.movesElement.textContent = count;
    }
    
    // Single Responsibility: Render board only
    // Modular Programming: Separated concerns for board rendering
    renderBoard(gameState) {
        this.board.innerHTML = '';
        
        // Configuration object - Modular approach with improved spacing
        const boardConfig = {
            hexSize: 25, // Increased spacing between hexagons
            centerX: 375, // Adjusted for new container size
            centerY: 375,
            cellWidth: 40,
            cellHeight: 46
        };
        
        // Functional Programming: Generate all cells (both playable and border)
        const cells = this.generateCellPositions(boardConfig)
            .map(cell => this.createHexCell(cell, gameState, boardConfig));
        
        // Functional approach: forEach for side effects (DOM manipulation)
        cells.forEach(cell => this.board.appendChild(cell));
    }
    
    // Pure Function: Generate cell positions without side effects
    generateCellPositions(config) {
        const positions = [];
        for (let q = -this.boardSize; q <= this.boardSize; q++) {
            for (let r = -this.boardSize; r <= this.boardSize; r++) {
                const s = -q - r;
                if (Math.abs(s) <= this.boardSize) {
                    const x = config.centerX + config.hexSize * (3/2 * q);
                    const y = config.centerY + config.hexSize * (Math.sqrt(3)/2 * q + Math.sqrt(3) * r);
                    const isBorder = Math.abs(q) === this.boardSize || 
                                   Math.abs(r) === this.boardSize || 
                                   Math.abs(s) === this.boardSize;
                    const type = isBorder ? 'border' : 'playable';
                    positions.push({ q, r, x, y, type });
                }
            }
        }
        return positions;
    }
    
    // Pure Function: Check if position is valid for playing (not border)
    isValidHexPosition(q, r) {
        const s = -q - r;
        return Math.abs(q) < this.boardSize && Math.abs(r) < this.boardSize && Math.abs(s) < this.boardSize;
    }
    
    // Factory Method Pattern: Create hex cells
    createHexCell(position, gameState, config) {
        const cell = document.createElement('div');
        if (position.type === 'border') {
            cell.className = 'hex-cell border-cell';
        } else {
            cell.className = 'hex-cell';
        }
        cell.style.left = `${position.x - config.cellWidth/2}px`;
        cell.style.top = `${position.y - config.cellHeight/2}px`;
        cell.setAttribute('data-q', position.q);
        cell.setAttribute('data-r', position.r);
        cell.setAttribute('data-type', position.type);
        
        // Defensive: Use correct cat position field and block field
        const isCatPosition = gameState.cat && position.q === gameState.cat.q && position.r === gameState.cat.r;
        const isBlocked = Array.isArray(gameState.blockedCells) && gameState.blockedCells.some(pos => pos.q === position.q && pos.r === position.r);
        
        if (isCatPosition) {
            cell.classList.add('cat');
        } else if (isBlocked) {
            cell.classList.add('blocked');
        } else if (position.type === 'playable') {
            // Only playable cells can be clicked
            const moveHandler = this.createMoveHandler(position.q, position.r);
            cell.addEventListener('click', moveHandler);
        }
        
        if (position.type === 'border') {
            cell.style.opacity = '0.3';
            cell.style.pointerEvents = 'none';
        }
        
        return cell;
    }
    
    // Higher-Order Function: Returns event handler function
    createMoveHandler(q, r) {
        return () => this.makeMove(q, r);
    }
    
    // Single Responsibility: Update status display only
    updateStatus(status) {
        const statusMessages = {
            'IN_PROGRESS': 'En progreso',
            'PLAYER_LOST': '¡El gato escapó!',
            'PLAYER_WON': '¡Atrapaste al gato!'
        };
        this.statusElement.textContent = statusMessages[status] || 'Estado desconocido';
    }
    
    // Single Responsibility: Show game over dialog only
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
    
    // Factory Method: Create game over dialog with improved functionality
    createGameOverDialog(message, status) {
        const gameOver = document.createElement('div');
        gameOver.className = 'game-over';
        const playerName = this.playerNameInput.value.trim() || 'Anónimo';
        gameOver.innerHTML = `
            <h2>${message}</h2>
            <p>Jugador: ${playerName}</p>
            <p>Movimientos: ${this.movesElement.textContent}</p>
            <div>
                <button onclick="saveScore('${this.gameId}', '${playerName}')">Guardar Puntuación</button>
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
        // Mejorada: muestra detalle de error si existe
        console.error(message, error);
        let msg = message;
        if (error && error.message) {
            msg += "\n" + error.message;
        }
        alert(msg);
    }
}

// Higher-Order Functions for High Score Management

const createScoreSaver = (gameId, playerName) => async () => {
    try {
        const response = await fetch(`/api/game/save-score?gameId=${gameId}&playerName=${encodeURIComponent(playerName)}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
        });
        if (response.ok) {
            alert('¡Puntuación guardada exitosamente!');
            return await response.json();
        } else {
            throw new Error('Error al guardar puntuación');
        }
    } catch (error) {
        console.error('Error saving score:', error);
        alert('Error al guardar la puntuación');
    }
};

// Global function for saving scores
async function saveScore(gameId, playerName) {
    const scoreSaver = createScoreSaver(gameId, playerName);
    await scoreSaver();
}

const createScoreFetcher = (endpoint) => async () => {
    try {
        const response = await fetch(endpoint, {
            headers: { 'Accept': 'application/json' }
        });
        if (!response.ok) {
            throw new Error(`Error fetching scores: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching scores:', error);
        return [];
    }
};

const scoreDisplayFunctions = {
    top: createScoreFetcher('/api/game/high-scores?limit=10'),
    winning: createScoreFetcher('/api/game/winning-scores'),
    recent: createScoreFetcher('/api/game/high-scores?limit=20')
};

const formatScore = (score) => {
    const winIcon = score.playerWon ? '🏆' : '❌';
    const calculatedScore = score.playerWon ? 
        (1000 - score.movesCount * 10 + score.boardSize * 50 + Math.max(0, 300 - score.gameDurationSeconds)) : 
        (100 - score.movesCount * 10);
    
    return {
        playerName: score.playerName,
        details: `${winIcon} ${score.movesCount} movimientos - Tablero ${score.boardSize}x${score.boardSize}`,
        score: Math.max(0, calculatedScore)
    };
};

const createScoreRenderer = (containerId) => (scores) => {
    const container = document.getElementById(containerId);
    if (!container) return;
    const scoreElements = scores
        .map(formatScore)
        .map(formattedScore => `
            <li>
                <div class="score-info">
                    <div class="player-name">${formattedScore.playerName}</div>
                    <div class="game-details">${formattedScore.details}</div>
                </div>
                <div class="score-value">${formattedScore.score}</div>
            </li>
        `);
    container.innerHTML = scoreElements.join('');
};

async function showHighScores() {
    const section = document.getElementById('high-score-section');
    section.style.display = 'block';
    await showScoreTab('top');
}

function hideHighScores() {
    const section = document.getElementById('high-score-section');
    section.style.display = 'none';
}

async function showScoreTab(tabType) {
    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    event?.target?.classList.add('active') || 
        document.querySelector(`[onclick="showScoreTab('${tabType}')"]`)?.classList.add('active');
    const scoreFetcher = scoreDisplayFunctions[tabType];
    const scoreRenderer = createScoreRenderer('score-list');
    if (scoreFetcher) {
        const scores = await scoreFetcher();
        scoreRenderer(scores);
    }
}

// Initialization
const initializeGame = () => {
    window.game = new Game();
    return window.game;
};

window.addEventListener('load', initializeGame); 

package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.strategy.AStarCatMovement;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementación esqueleto de GameState para tableros hexagonales.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar:
 * - Estado del juego más sofisticado que ExampleGameState
 * - Sistema de puntuación avanzado
 * - Lógica de victoria/derrota más compleja
 * - Serialización eficiente
 * - Manejo de eventos y callbacks
 */
public class HexGameState extends GameState<HexPosition> {
	
	private HexPosition catPosition;
	private HexGameBoard gameBoard;
	private int boardSize;
	private Difficulties difficulty;

	public enum Difficulties {
		EASY,
		HARD
	}
	
	// Constructor de HexGameState
	public HexGameState(String gameId, int boardSize) {
		super(gameId);
		this.boardSize = boardSize;
		this.catPosition = new HexPosition(0, 0);
		this.gameBoard = new HexGameBoard(boardSize);
	}
	
	@Override
	protected boolean canExecuteMove(HexPosition position) {
		return gameBoard.isValidMove(position) &&
			!catPosition.equals(position) &&
			getStatus() == GameStatus.IN_PROGRESS;
	}
	
	@Override
	protected boolean performMove(HexPosition position) {
		return gameBoard.makeMove(position);
	}
	
	private boolean isCatAtBorder() {
		return catPosition.isAtBorder(boardSize);
	}
	
	private boolean isCatTrapped() {
		AStarCatMovement AStarCat = new AStarCatMovement(gameBoard);
		return !AStarCat.hasPathToGoal(catPosition);
	}
	
	@Override
	protected void updateGameStatus() {
		if (isCatAtBorder()) {
			setStatus(GameStatus.PLAYER_LOST);
		} else if (isCatTrapped()) {
			setStatus(GameStatus.PLAYER_WON);
		}
	}
	
	@Override
	public HexPosition getCatPosition() {
		return catPosition;
	}
	
	@Override
	public void setCatPosition(HexPosition position) {
		this.catPosition = position;
		updateGameStatus();
	}
	
	@Override
	public boolean isGameFinished() {
		return getStatus() != GameStatus.IN_PROGRESS;
	}
	
	@Override
	public boolean hasPlayerWon() {
		return getStatus() == GameStatus.PLAYER_WON;
	}
	
	@Override
	public int calculateScore() {
		throw new UnsupportedOperationException("Los estudiantes deben implementar calculateScore");
	}
	
	/**
	 * Serializa el estado del juego en un JSONObject puro (no en String).
	 * El repositorio debe llamar a .toString() sobre el JSONObject antes de guardar en base de datos.
	 */
	@Override
	public Object getSerializableState() {
		JSONObject JSON_Object = new JSONObject();

		JSON_Object.put("gameId", getGameId());
		JSON_Object.put("status", getStatus());
		JSON_Object.put("catPosition", new JSONArray(List.of(catPosition.getQ(), catPosition.getR())));

		JSONArray blockedPositionsArray = new JSONArray(gameBoard.getBlockedPositions().stream()
			.map((_position) -> new JSONArray(List.of(_position.getQ(), _position.getR())))
			.collect(Collectors.toList()));
		JSON_Object.put("blockedPositions", blockedPositionsArray);
		
		JSON_Object.put("moveCount", getMoveCount());
		JSON_Object.put("boardSize", boardSize);
		// JSON_Object.put("score", [getScore()]);
		// JSON_Object.put("difficulty", [getDifficulty()]);
		// JSON_Object.put("elapsedTime", [getTimeElapsed()]);
		
		return JSON_Object; // <-- ¡Nunca retornes JSON_Object.toString() aquí!
	}
	
	@Override
	public void restoreFromSerializable(Object serializedState) {
		if (serializedState instanceof JSONObject) {
			try {
				JSONObject JSONState = (JSONObject) serializedState;

				setStatus(GameStatus.valueOf(JSONState.getString("status")));

				JSONArray newCatPositionArray = JSONState.getJSONArray("catPosition");
				catPosition = new HexPosition(newCatPositionArray.getInt(0), newCatPositionArray.getInt(1));

				JSONArray newBlockedPositionsArray = JSONState.getJSONArray("blockedPositions");
				LinkedHashSet<HexPosition> newBlockedPositionsSet = new LinkedHashSet<>();

				for (int i = 0; i < newBlockedPositionsArray.length(); i++) {
					JSONArray positionArray = newBlockedPositionsArray.getJSONArray(i);
					HexPosition position = new HexPosition(positionArray.getInt(0), positionArray.getInt(1));
					newBlockedPositionsSet.add(position);
				}
				
				gameBoard.setBlockedPositions(newBlockedPositionsSet);

				setMoveCount(JSONState.getInt("moveCount"));
				setBoardSize(JSONState.getInt("boardSize"));
			} catch (JSONException error) {
				error.printStackTrace();
				throw new RuntimeException("Error al deserializar estado del juego desde JSON", error);
			}
		} else {
			throw new JSONException("No se recibió un JSONObject para deserializar");
		}
	}
	
	public Map<String, Object> getAdvancedStatistics() {
		throw new UnsupportedOperationException("Método adicional para implementar");
	}
	
	public HexGameBoard getGameBoard() {
		return gameBoard;
	}

	public Set<HexPosition> getBlockedPositions() {
		return gameBoard.getBlockedPositions();
	}
	
	public void setMoveCount(int newMoveCount) {
		moveCount = newMoveCount;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int newBoardSize) {
		boardSize = newBoardSize;
	}
	
	public void setDifficulty(String newDifficulty) {
		difficulty = Difficulties.valueOf(newDifficulty);
	}
}

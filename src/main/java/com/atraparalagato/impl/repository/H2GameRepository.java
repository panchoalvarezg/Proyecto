package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementación de DataRepository usando base de datos H2.
 */
public class H2GameRepository extends DataRepository<GameState<HexPosition>, String> {

    // Deben coincidir con application.properties
    private static final String JDBC_URL = "jdbc:h2:file:./data/atrapar-al-gato-db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private Connection connection;

    public H2GameRepository() {
        try {
            this.connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al conectar a la base de datos H2", e);
        }
    }

    @Override
    protected void initialize() {
        String query = """
            CREATE TABLE IF NOT EXISTS Games (
            gameId VARCHAR(255) PRIMARY KEY NOT NULL,
            data VARCHAR(4096) NOT NULL
            )
            """;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al inicializar la base de datos H2", e);
        }
    }

    @Override
    public GameState<HexPosition> save(GameState<HexPosition> entity) {
        if (!(entity instanceof HexGameState hexState)) {
            throw new IllegalArgumentException("Estado no es HexGameState");
        }

        String selectQuery = "SELECT COUNT(gameId) FROM Games WHERE gameId = ?";

        try {
            PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
            selectStmt.setString(1, hexState.getGameId());
            ResultSet resultSet = selectStmt.executeQuery();

            // Nos aseguramos que serializableState retorna un JSONObject
            JSONObject serializedEntity = (JSONObject) hexState.getSerializableState();
            String jsonString = serializedEntity.toString();

            beforeSave(entity);

            if (resultSet.next() && resultSet.getInt(1) != 0) {
                String updateQuery = "UPDATE Games SET data = ? WHERE gameId = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setString(1, jsonString);
                updateStmt.setString(2, hexState.getGameId());
                updateStmt.executeUpdate();
                updateStmt.close();
            } else {
                String insertQuery = "INSERT INTO Games (gameId, data) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, hexState.getGameId());
                insertStmt.setString(2, jsonString);
                insertStmt.executeUpdate();
                insertStmt.close();
            }

            afterSave(entity);

            selectStmt.close();
            resultSet.close();

            return hexState;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar estado del juego", e);
        }
    }

    @Override
    public Optional<GameState<HexPosition>> findById(String id) {
        if (id == null) return Optional.empty();

        String query = "SELECT data FROM Games WHERE gameId = ? LIMIT 1";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String dataJSONString = resultSet.getString(1);
                stmt.close();
                resultSet.close();
                return Optional.of(deserializeGameState(dataJSONString, id));
            } else {
                stmt.close();
                resultSet.close();
                return Optional.empty();
            }
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar juego por id", e);
        }
    }

    private HexGameState deserializeGameState(String serializedData, String gameId) {
        try {
            JSONObject json = new JSONObject(serializedData);
            HexGameState state = new HexGameState(gameId, json.getInt("boardSize"));

            JSONArray catPos = json.getJSONArray("catPosition");
            state.setCatPosition(new HexPosition(catPos.getInt(0), catPos.getInt(1)));

            JSONArray blockedArray = json.getJSONArray("blockedPositions");
            LinkedHashSet<HexPosition> blocked = new LinkedHashSet<>();
            for (int i = 0; i < blockedArray.length(); i++) {
                JSONArray pos = blockedArray.getJSONArray(i);
                blocked.add(new HexPosition(pos.getInt(0), pos.getInt(1)));
            }
            state.getGameBoard().setBlockedPositions(blocked);

            state.setMoveCount(json.getInt("moveCount"));
            state.setBoardSize(json.getInt("boardSize"));

            return state;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al deserializar estado del juego", e);
        }
    }

    @Override
    public List<GameState<HexPosition>> findAll() {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAll");
    }

    @Override
    public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findWhere");
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> condition, Function<GameState<HexPosition>, R> transformer) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAndTransform");
    }

    @Override
    public long countWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar countWhere");
    }

    @Override
    public boolean deleteById(String id) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar deleteById");
    }

    @Override
    public long deleteWhere(Predicate<GameState<HexPosition>> condition) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar deleteWhere");
    }

    @Override
    public boolean existsById(String id) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar existsById");
    }

    @Override
    public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> operation) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar executeInTransaction");
    }

    @Override
    public List<GameState<HexPosition>> findWithPagination(int page, int size) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findWithPagination");
    }

    @Override
    public List<GameState<HexPosition>> findAllSorted(Function<GameState<HexPosition>, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar findAllSorted");
    }

    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        throw new UnsupportedOperationException("Los estudiantes deben implementar executeCustomQuery");
    }

    @Override
    protected void cleanup() {
        throw new UnsupportedOperationException("Los estudiantes deben implementar cleanup");
    }
}

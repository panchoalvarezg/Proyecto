package com.atraparalagato.impl.repository;

import com.atraparalagato.impl.model.HexGameState;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Repositorio en base de datos H2 para estados de juego.
 */
public class H2GameRepository {
    private final String url = "jdbc:h2:mem:gamedb;DB_CLOSE_DELAY=-1";
    private final String user = "sa";
    private final String password = "";

    public H2GameRepository() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS game_state (" +
                    "id VARCHAR(100) PRIMARY KEY, " +
                    "state BLOB)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(String id, HexGameState state) {
        executeInTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "MERGE INTO game_state (id, state) VALUES (?, ?)")) {
                ps.setString(1, id);
                ps.setBytes(2, serializeState(state));
                ps.executeUpdate();
            }
        });
    }

    public Optional<HexGameState> findById(String id) {
        return executeInTransaction(conn -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT state FROM game_state WHERE id = ?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(deserializeState(rs.getBytes(1)));
                    }
                }
            }
            return Optional.empty();
        });
    }

    public List<HexGameState> findAll() {
        return executeInTransaction(conn -> {
            List<HexGameState> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT state FROM game_state");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(deserializeState(rs.getBytes(1)));
                }
            }
            return list;
        });
    }

    public List<HexGameState> findWhere(Predicate<HexGameState> filter) {
        List<HexGameState> all = findAll();
        List<HexGameState> filtered = new ArrayList<>();
        for (HexGameState state : all) if (filter.test(state)) filtered.add(state);
        return filtered;
    }

    public <R> List<R> findAndTransform(Predicate<HexGameState> filter, Function<HexGameState, R> transform) {
        List<R> result = new ArrayList<>();
        for (HexGameState state : findWhere(filter)) {
            result.add(transform.apply(state));
        }
        return result;
    }

    public <T> T executeInTransaction(Function<Connection, T> action) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            try {
                T result = action.apply(conn);
                conn.commit();
                return result;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serializeState(HexGameState state) {
        try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
             java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(bos)) {
            out.writeObject(state);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HexGameState deserializeState(byte[] bytes) {
        try (java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(bytes);
             java.io.ObjectInputStream in = new java.io.ObjectInputStream(bis)) {
            return (HexGameState) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

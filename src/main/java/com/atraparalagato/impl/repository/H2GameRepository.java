package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Paradigma: Programación Funcional
 */
public class H2GameRepository<T> implements DataRepository<T> {
    private final Map<String, T> db = new HashMap<>(); // Simulación; reemplazar con H2 real

    @Override
    public void save(T entity) {
        // Supón que T tiene getId(); en la práctica usa JDBC/H2
        db.put(UUID.randomUUID().toString(), entity);
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public List<T> findWhere(Predicate<T> condition) {
        // Funcional
        return db.values().stream().filter(condition).collect(Collectors.toList());
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<T> condition, Function<T, R> transformer) {
        // Funcional
        return db.values().stream().filter(condition).map(transformer).collect(Collectors.toList());
    }

    @Override
    public void executeInTransaction(Runnable action) {
        // Funcional: acepta callback/acción
        action.run();
    }
}

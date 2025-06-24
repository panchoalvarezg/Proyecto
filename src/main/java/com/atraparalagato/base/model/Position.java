package com.atraparalagato.base.model;

/**
 * Clase base abstracta para representar posiciones en el tablero.
 * 
 * SOLID Principles:
 * - Single Responsibility: Representa únicamente una posición
 * - Open/Closed: Permite diferentes tipos de coordenadas (cartesianas, hexagonales, etc.)
 * 
 * Conceptos a implementar:
 * - OOP: Herencia, encapsulación, polimorfismo
 * - Modularización: Abstracción de diferentes sistemas de coordenadas
 * - Programación Funcional: Métodos que pueden ser usados en streams
 */
public abstract class Position {
    
    /**
     * Calcula la distancia entre esta posición y otra.
     * Los estudiantes deben implementar según el tipo de coordenadas.
     * Ejemplos: distancia euclidiana, Manhattan, hexagonal, etc.
     */
    public abstract double distanceTo(Position other);
    
    /**
     * Suma esta posición con otra, retornando una nueva posición.
     * Útil para calcular movimientos y desplazamientos.
     * Debe mantener inmutabilidad.
     */
    public abstract Position add(Position other);
    
    /**
     * Resta otra posición de esta, retornando una nueva posición.
     * Útil para calcular vectores de dirección.
     */
    public abstract Position subtract(Position other);
    
    /**
     * Verifica si esta posición es adyacente a otra.
     * La definición de "adyacente" depende del tipo de tablero.
     */
    public abstract boolean isAdjacentTo(Position other);
    
    /**
     * Verifica si esta posición está dentro de los límites dados.
     * Los estudiantes deben definir qué significa "dentro de límites".
     */
    public abstract boolean isWithinBounds(int maxSize);
    
    /**
     * Obtiene una representación única de la posición para hashing.
     * Importante para usar en Sets y Maps.
     */
    @Override
    public abstract int hashCode();
    
    /**
     * Compara esta posición con otra para igualdad.
     * Fundamental para colecciones y comparaciones.
     */
    @Override
    public abstract boolean equals(Object obj);
    
    /**
     * Representación en string de la posición.
     * Útil para debugging y logging.
     */
    @Override
    public abstract String toString();
} 

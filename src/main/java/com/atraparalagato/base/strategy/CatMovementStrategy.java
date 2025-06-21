package com.atraparalagato.base.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.Position;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia abstracta para el movimiento del gato.
 * 
 * Design Patterns:
 * - Strategy Pattern: Permite diferentes algoritmos de movimiento
 * - Template Method: Define el flujo general de decisión
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo maneja la lógica de movimiento del gato
 * - Open/Closed: Abierta para nuevos algoritmos, cerrada para modificación
 * - Dependency Inversion: Depende de abstracciones, no implementaciones concretas
 * 
 * Conceptos a implementar:
 * - Programación Funcional: Uso de Function, Predicate, Optional
 * - Algoritmos: A*, BFS, DFS, Greedy, etc.
 * - Modularización: Separación de heurísticas y algoritmos
 */
public abstract class CatMovementStrategy<T extends Position> {
    
    protected final GameBoard<T> board;
    
    protected CatMovementStrategy(GameBoard<T> board) {
        this.board = board;
    }
    
    /**
     * Template Method: Define el flujo general para encontrar el mejor movimiento.
     * Los estudiantes no deben modificar este método.
     */
    public final Optional<T> findBestMove(T currentPosition, T targetPosition) {
        List<T> possibleMoves = getPossibleMoves(currentPosition);
        
        if (possibleMoves.isEmpty()) {
            return Optional.empty();
        }
        
        return selectBestMove(possibleMoves, currentPosition, targetPosition);
    }
    
    /**
     * Obtiene todos los movimientos posibles desde una posición.
     * Debe considerar las reglas del tablero y posiciones bloqueadas.
     */
    protected abstract List<T> getPossibleMoves(T currentPosition);
    
    /**
     * Selecciona el mejor movimiento de una lista de movimientos posibles.
     * Aquí es donde los estudiantes implementan su algoritmo específico.
     */
    protected abstract Optional<T> selectBestMove(List<T> possibleMoves, 
                                                 T currentPosition, 
                                                 T targetPosition);
    
    /**
     * Función heurística para evaluar qué tan buena es una posición.
     * Los estudiantes pueden implementar diferentes heurísticas.
     * Ejemplo de programación funcional: Function<T, Double>
     */
    protected abstract Function<T, Double> getHeuristicFunction(T targetPosition);
    
    /**
     * Predicado para determinar si una posición es un objetivo válido.
     * Ejemplo: posiciones en el borde del tablero para escapar.
     */
    protected abstract Predicate<T> getGoalPredicate();
    
    /**
     * Calcula el costo de moverse de una posición a otra.
     * Útil para algoritmos como A* que consideran costos.
     */
    protected abstract double getMoveCost(T from, T to);
    
    /**
     * Verifica si existe un camino desde la posición actual hasta el objetivo.
     * Los estudiantes pueden implementar diferentes algoritmos de pathfinding.
     */
    public abstract boolean hasPathToGoal(T currentPosition);
    
    /**
     * Obtiene el camino completo desde la posición actual hasta el objetivo.
     * Útil para debugging y visualización.
     * Retorna una lista vacía si no hay camino.
     */
    public abstract List<T> getFullPath(T currentPosition, T targetPosition);
    
    /**
     * Hook method: Se ejecuta antes de calcular el movimiento.
     * Permite preparación o logging específico del algoritmo.
     */
    protected void beforeMovementCalculation(T currentPosition) {
        // Default: no operation
    }
    
    /**
     * Hook method: Se ejecuta después de calcular el movimiento.
     * Permite cleanup o logging específico del algoritmo.
     */
    protected void afterMovementCalculation(Optional<T> selectedMove) {
        // Default: no operation
    }
} 
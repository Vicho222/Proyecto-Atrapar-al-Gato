package com.atraparalagato.example.service;

import com.atraparalagato.example.model.ExampleGameBoard;
import com.atraparalagato.example.model.ExampleGameState;
import com.atraparalagato.example.repository.InMemoryGameRepository;
import com.atraparalagato.example.strategy.SimpleCatMovement;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;

/**
 * Implementación de ejemplo de servicio de juego.
 * 
 * NOTA PARA ESTUDIANTES:
 * Esta es una implementación BÁSICA de ejemplo. Los estudiantes deben:
 * 1. Estudiar esta implementación para entender la orquestación
 * 2. Crear su propia implementación más sofisticada usando GameService
 * 3. Implementar lógica de negocio más compleja
 * 4. Agregar validaciones y manejo de errores más robusto
 * 
 * Esta implementación coordina todos los componentes del juego de forma simple.
 */
public class ExampleGameService {
    
    private final InMemoryGameRepository gameRepository;
    
    public ExampleGameService() {
        this.gameRepository = new InMemoryGameRepository();
    }
    
    /**
     * Inicia un nuevo juego.
     */
    public ExampleGameState startNewGame(int boardSize) {
        String gameId = UUID.randomUUID().toString();
        ExampleGameState gameState = new ExampleGameState(gameId, boardSize);
        
        // Configurar callbacks básicos
        gameState.setOnStateChanged(this::onGameStateChanged);
        gameState.setOnGameEnded(this::onGameEnded);
        
        // Guardar el estado inicial
        gameRepository.save(gameState);
        
        System.out.println("🎮 Nuevo juego iniciado: " + gameId);
        return gameState;
    }
    
    /**
     * Ejecuta un movimiento del jugador.
     */
    public Optional<ExampleGameState> executePlayerMove(String gameId, HexPosition position) {
        Optional<ExampleGameState> gameStateOpt = gameRepository.findById(gameId);
        
        if (gameStateOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ExampleGameState gameState = gameStateOpt.get();
        
        // Validar movimiento
        if (!isValidMove(gameState, position)) {
            return Optional.of(gameState);
        }
        
        // Ejecutar movimiento del jugador
        if (!gameState.executeMove(position)) {
            return Optional.of(gameState);
        }
        
        System.out.println("👤 Jugador bloqueó posición: " + position + " (Movimiento #" + gameState.getMoveCount() + ")");
        
        // Mover el gato después del movimiento del jugador
        executeCatMove(gameState);
        
        // Guardar estado actualizado
        gameRepository.save(gameState);
        
        return Optional.of(gameState);
    }
    
    /**
     * Obtiene el estado de un juego.
     */
    public Optional<ExampleGameState> getGameState(String gameId) {
        return gameRepository.findById(gameId);
    }
    
    /**
     * Obtiene estadísticas de un juego.
     */
    public Map<String, Object> getGameStatistics(String gameId) {
        Optional<ExampleGameState> gameStateOpt = gameRepository.findById(gameId);
        
        if (gameStateOpt.isEmpty()) {
            return Map.of("error", "Game not found");
        }
        
        ExampleGameState gameState = gameStateOpt.get();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("gameId", gameState.getGameId());
        stats.put("status", gameState.getStatus().toString());
        stats.put("moveCount", gameState.getMoveCount());
        stats.put("isFinished", gameState.isGameFinished());
        stats.put("playerWon", gameState.hasPlayerWon());
        stats.put("score", gameState.calculateScore());
        stats.put("createdAt", gameState.getCreatedAt());
        stats.put("boardSize", gameState.getBoardSize());
        stats.put("catPosition", Map.of(
            "q", gameState.getCatPosition().getQ(),
            "r", gameState.getCatPosition().getR()
        ));
        
        // Estadísticas del tablero
        stats.put("boardStats", gameState.getGameBoard().getBoardStatistics());
        
        return stats;
    }
    
    /**
     * Valida si un movimiento es legal.
     */
    public boolean isValidMove(ExampleGameState gameState, HexPosition position) {
        // Validaciones básicas
        if (gameState.isGameFinished()) {
            return false; // No se pueden hacer movimientos en juegos terminados
        }
        
        // Verificar que no sea la posición del gato
        if (position.equals(gameState.getCatPosition())) {
            return false;
        }
        
        // Verificar que sea un movimiento válido en el tablero
        ExampleGameBoard board = gameState.getGameBoard();
        return board.isPositionInBounds(position) && !board.isBlocked(position);
    }
    
    /**
     * Obtiene una sugerencia de movimiento para el jugador.
     */
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        Optional<ExampleGameState> gameStateOpt = gameRepository.findById(gameId);
        
        if (gameStateOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ExampleGameState gameState = gameStateOpt.get();
        HexPosition catPosition = gameState.getCatPosition();
        ExampleGameBoard board = gameState.getGameBoard();
        
        // Sugerencia simple: bloquear una posición adyacente al gato
        List<HexPosition> adjacentToCat = board.getAdjacentPositions(catPosition);
        
        return adjacentToCat.stream()
                .filter(pos -> !board.isBlocked(pos))
                .findFirst();
    }
    
    /**
     * Ejecuta el movimiento del gato.
     */
    private void executeCatMove(ExampleGameState gameState) {
        HexPosition currentPosition = gameState.getCatPosition();
        ExampleGameBoard board = gameState.getGameBoard();
        
        // Crear estrategia de movimiento simple
        SimpleCatMovement strategy = new SimpleCatMovement(board);
        
        // Objetivo: cualquier posición en el borde del tablero
        HexPosition targetPosition = new HexPosition(gameState.getBoardSize(), 0);
        
        Optional<HexPosition> nextMove = strategy.findBestMove(currentPosition, targetPosition);
        
        if (nextMove.isPresent()) {
            gameState.setCatPosition(nextMove.get());
            System.out.println("🐱 Gato se movió a: " + nextMove.get());
        } else {
            System.out.println("🐱 ¡Gato no puede moverse! Está atrapado.");
        }
    }
    
    /**
     * Obtiene estadísticas globales del repositorio.
     */
    public Map<String, Object> getGlobalStatistics() {
        return gameRepository.getRepositoryStatistics();
    }
    
    /**
     * Limpia juegos antiguos del repositorio.
     */
    public long cleanupOldGames(long maxAgeHours) {
        long maxAgeMillis = maxAgeHours * 60 * 60 * 1000;
        return gameRepository.cleanupOldGames(maxAgeMillis);
    }
    
    /**
     * Obtiene todos los juegos.
     */
    public List<ExampleGameState> getAllGames() {
        return gameRepository.findAll();
    }
    
    /**
     * Obtiene juegos terminados.
     */
    public List<ExampleGameState> getFinishedGames() {
        return gameRepository.findWhere(ExampleGameState::isGameFinished);
    }
    
    /**
     * Obtiene juegos ganados.
     */
    public List<ExampleGameState> getWonGames() {
        return gameRepository.findWhere(ExampleGameState::hasPlayerWon);
    }
    
    // Callbacks para eventos del juego
    private void onGameStateChanged(com.atraparalagato.base.model.GameState<HexPosition> gameState) {
        System.out.println("📊 Estado del juego actualizado: " + gameState.getStatus());
    }
    
    private void onGameEnded(com.atraparalagato.base.model.GameState<HexPosition> gameState) {
        String result = gameState.hasPlayerWon() ? "¡VICTORIA!" : "Derrota";
        System.out.println("🏁 Juego terminado: " + result + " - Puntuación: " + gameState.calculateScore());
    }
} 
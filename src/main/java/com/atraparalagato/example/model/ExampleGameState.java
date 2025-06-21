package com.atraparalagato.example.model;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de ejemplo de GameState para tableros hexagonales.
 * 
 * NOTA PARA ESTUDIANTES:
 * Esta es una implementación BÁSICA de ejemplo. Los estudiantes deben:
 * 1. Estudiar esta implementación para entender los conceptos
 * 2. Crear su propia implementación más sofisticada
 * 3. Agregar funcionalidades adicionales según sus necesidades
 * 
 * Esta implementación es intencionalmente simple para fines educativos.
 */
public class ExampleGameState extends GameState<HexPosition> {
    
    private HexPosition catPosition;
    private final ExampleGameBoard gameBoard;
    private final int boardSize;
    
    public ExampleGameState(String gameId, int boardSize) {
        super(gameId);
        this.boardSize = boardSize;
        this.gameBoard = new ExampleGameBoard(boardSize);
        this.catPosition = new HexPosition(0, 0); // Gato empieza en el centro
    }
    
    @Override
    protected boolean canExecuteMove(HexPosition position) {
        // Validación básica: posición válida y no bloqueada
        return gameBoard.isValidMove(position);
    }
    
    @Override
    protected boolean performMove(HexPosition position) {
        // Ejecutar el movimiento en el tablero
        return gameBoard.makeMove(position);
    }
    
    @Override
    protected void updateGameStatus() {
        // Lógica simple para determinar el estado del juego
        if (isCatAtBorder()) {
            setStatus(GameStatus.PLAYER_LOST); // El gato escapó
        } else if (isCatTrapped()) {
            setStatus(GameStatus.PLAYER_WON); // El gato está atrapado
        }
        // Si no, el juego continúa (IN_PROGRESS)
    }
    
    @Override
    public HexPosition getCatPosition() {
        return catPosition;
    }
    
    @Override
    public void setCatPosition(HexPosition position) {
        this.catPosition = position;
        // IMPORTANTE: Verificar estado del juego después de mover el gato
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
        // Sistema de puntuación básico
        if (hasPlayerWon()) {
            // Puntuación base - penalización por movimientos + bonus por tamaño del tablero
            return Math.max(0, 1000 - getMoveCount() * 10 + boardSize * 50);
        } else {
            // Puntuación mínima si no ganó
            return Math.max(0, 100 - getMoveCount() * 5);
        }
    }
    
    @Override
    public Object getSerializableState() {
        // Crear un mapa con el estado serializable
        Map<String, Object> state = new HashMap<>();
        state.put("gameId", getGameId());
        state.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blockedCells", gameBoard.getBlockedPositions());
        state.put("status", getStatus().toString());
        state.put("moveCount", getMoveCount());
        state.put("boardSize", boardSize);
        return state;
    }
    
    @Override
    public void restoreFromSerializable(Object serializedState) {
        // Implementación básica de restauración
        if (serializedState instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> state = (Map<String, Object>) serializedState;
            
            // Restaurar posición del gato
            @SuppressWarnings("unchecked")
            Map<String, Integer> catPos = (Map<String, Integer>) state.get("catPosition");
            if (catPos != null) {
                this.catPosition = new HexPosition(catPos.get("q"), catPos.get("r"));
            }
            
            // Restaurar estado del juego
            String statusStr = (String) state.get("status");
            if (statusStr != null) {
                setStatus(GameStatus.valueOf(statusStr));
            }
        }
    }
    
    // Métodos auxiliares privados
    private boolean isCatAtBorder() {
        // Verificar si el gato está en el borde del tablero
        // CORREGIDO: El gato escapa cuando llega exactamente al borde
        return Math.abs(catPosition.getQ()) == boardSize ||
               Math.abs(catPosition.getR()) == boardSize ||
               Math.abs(catPosition.getS()) == boardSize;
    }
    
    private boolean isCatTrapped() {
        // Verificar si el gato está completamente rodeado
        // Implementación muy básica: verificar si todas las posiciones adyacentes están bloqueadas
        return gameBoard.getAdjacentPositions(catPosition).stream()
                .allMatch(gameBoard::isBlocked);
    }
    
    // Getter para el tablero (útil para el servicio)
    public ExampleGameBoard getGameBoard() {
        return gameBoard;
    }
    
    public int getBoardSize() {
        return boardSize;
    }
} 
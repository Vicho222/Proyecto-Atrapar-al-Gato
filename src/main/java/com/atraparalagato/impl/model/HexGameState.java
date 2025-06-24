package com.atraparalagato.impl.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.atraparalagato.base.model.GameState;

/**
 * Implementación esqueleto de GameState para tableros hexagonales.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar: - Estado del juego más sofisticado que
 * ExampleGameState - Sistema de puntuación avanzado - Lógica de
 * victoria/derrota más compleja - Serialización eficiente - Manejo de eventos y
 * callbacks
 */
public class HexGameState extends GameState<HexPosition> {

	public enum LEVEL_OF_DIFFICULTY {
		EASY, HARD
	};

	public final static int DEFAULT_BOARD_SIZE = 11;
	private HexPosition catPosition;
	private HexGameBoard gameBoard;
	private int boardSize;
	private LocalDateTime finishedAt;
	private String playerId;
	private LocalDateTime pausedAt;
	
	private int points;
	/**
	 * LLeva la cuenta de los movimientos que son inválidos.
	 */
	private int invalidMovements;

	/**
	 * La máxima cantidad de movimientos permitidos para ganar el juego.
	 * 
	 * Un valor 0 indica que no hay límite
	 */
	private int maxMovements;

	private LEVEL_OF_DIFFICULTY levelOfDifficulty = LEVEL_OF_DIFFICULTY.EASY;

	// TODO: Los estudiantes pueden agregar más campos según necesiten
	// Ejemplos: tiempo de juego, dificultad, power-ups, etc.

	/**
	 * Constructor del juego que no tiene límite de movimientos.
	 * 
	 * @param gameId    Identificador del juego
	 * @param boardSize Tamaño del tablero
	 */
	public HexGameState(String gameId) {
		this(gameId, DEFAULT_BOARD_SIZE, 0);
	}

	/**
	 * Constructor del juego que no tiene límite de movimientos.
	 * 
	 * @param gameId    Identificador del juego
	 * @param boardSize Tamaño del tablero
	 */
	public HexGameState(String gameId, int boardSize) {
		this(gameId, boardSize, 0);
	}

	/**
	 * Constructor del juego que tiene límite de movimientos.
	 * 
	 * @param gameId    Identificador del juego
	 * @param boardSize Tamaño del tablero
	 */
	public HexGameState(String gameId, int boardSize, int maxMovements) {
		super(gameId);
		this.boardSize = boardSize;

		// TODO: Inicializar el tablero y posición inicial del gato
		// Pista: Usar HexGameBoard y posicionar el gato en el centro

		/*
		 * E.OSORIO
		 * 
		 * Se crea el tablero del tamaño indicado
		 */
		this.gameBoard = new HexGameBoard(boardSize);

		this.catPosition = new HexPosition(boardSize, boardSize);

		this.maxMovements = maxMovements;

		this.invalidMovements = 0;

		this.status = GameStatus.IN_PROGRESS;

		// throw new UnsupportedOperationException("Los estudiantes deben implementar el
		// constructor");
	}

	@Override
	public boolean canExecuteMove(HexPosition position) {
		// TODO: Implementar validación de movimientos más sofisticada
		// Considerar:
		// 1. Validación básica del tablero
		// 2. Reglas específicas del juego
		// 3. Estado actual del juego
		// 4. Posibles restricciones adicionales

		/*
		 * E.OSORIO Si el juego está en progreso y el movimiento es válido.
		 */
		boolean isPaused = GameStatus.PAUSED.equals(getStatus());
		return !isGameFinished() && !isPaused && gameBoard.isValidMove(position);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// canExecuteMove");
	}

	@Override
	protected boolean performMove(HexPosition position) {
		// TODO: Ejecutar el movimiento en el tablero
		// Debe actualizar el estado del tablero y verificar consecuencias
		// Retornar true si el movimiento fue exitoso
		boolean result = gameBoard.makeMove(position);
		if (result) {
			this.moveCount++;
			
		} else {
			this.invalidMovements++;
		}
		this.updateGameStatus();
		return result;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// performMove");
	}

	@Override
	protected void updateGameStatus() {
		// TODO: Implementar lógica de determinación de estado del juego
		// Debe verificar:
		// 1. Si el gato llegó al borde (PLAYER_LOST)
		// 2. Si el gato está atrapado (PLAYER_WON)
		// 3. Si hay empate o condiciones especiales
		// 4. Actualizar el estado usando setStatus()

		if (isCatAtBorder()) {
			setStatus(GameStatus.PLAYER_LOST);
			finishedAt = LocalDateTime.now();
			return;
		} else if (isCatTrapped()) {
			setStatus(GameStatus.PLAYER_WON);
			finishedAt = LocalDateTime.now();
			return;
		}

		/*
		 * E.OSORIO
		 * 
		 * Es empate si se ha llegado al límite de movimientos.
		 */
		if (maxMovements != 0 && moveCount >= maxMovements) {
			setStatus(GameStatus.DRAW);
			finishedAt = LocalDateTime.now();
		}
		
		this.points =  calculateScore();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// updateGameStatus");
	}

	@Override
	public HexPosition getCatPosition() {
		// TODO: Retornar la posición actual del gato
		return this.catPosition;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getCatPosition");
	}

	@Override
	public void setCatPosition(HexPosition position) {
		// TODO: Establecer la nueva posición del gato
		// IMPORTANTE: Debe llamar a updateGameStatus() después de mover el gato
		// para verificar si el juego terminó

		this.catPosition = position;
		updateGameStatus();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// setCatPosition");
	}

	@Override
	public boolean isGameFinished() {
		// TODO: Verificar si el juego ha terminado
		// Puede basarse en getStatus() o implementar lógica adicional

		/*
		 * E.OSOSRIO
		 * 
		 * Finaliza cuando ha ganado el gato o el jugador.
		 */
		return !getStatus().equals(GameStatus.IN_PROGRESS) && getStatus().equals(GameStatus.PAUSED);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// isGameFinished");
	}

	@Override
	public boolean hasPlayerWon() {
		// TODO: Verificar si el jugador ganó
		// Determinar las condiciones específicas de victoria

		/*
		 * E.OSORIO
		 */
		return getStatus().equals(GameStatus.PLAYER_WON);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// hasPlayerWon");
	}

	@Override
	public int calculateScore() {
		// TODO: Implementar sistema de puntuación más sofisticado que ExampleGameState
		// Considerar factores como:
		// 1. Número de movimientos (menos es mejor)
		// 2. Tiempo transcurrido
		// 3. Tamaño del tablero (más difícil = más puntos)
		// 4. Bonificaciones especiales
		// 5. Penalizaciones por movimientos inválidos

		int basePointsByDifficulty = levelOfDifficulty.equals(LEVEL_OF_DIFFICULTY.EASY) ? 200 : 50;
		// Sistema de puntuación básico
		if (hasPlayerWon()) {
			/*
			 * Puntuación
			 * 
			 * base + maxMovimientos - cantidad Movimientos (maxMovimientos es 0 penaliza en
			 * caso contario suma)+ bonus por tamaño del tablero - penalización por
			 * movimientos inválidos
			 */
			
			return Math.max(0, basePointsByDifficulty + 1000 + (maxMovements - getMoveCount()) * 10 + boardSize * 50 - invalidMovements * 2);
		}
		// Puntuación mínima si no ha ganado o perdió
		return Math.max(0, basePointsByDifficulty + 100 + (maxMovements - getMoveCount()) * 5 - invalidMovements * 2);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// calculateScore");
	}

	@Override
	public Object getSerializableState() {
		// TODO: Crear representación serializable del estado
		// Debe incluir toda la información necesaria para restaurar el juego
		// Considerar usar Map, JSON, o clase personalizada
		// Incluir: gameId, catPosition, blockedCells, status, moveCount, etc.

        Map<String, Object> state = new HashMap<>();
        state.put("gameId", getGameId());
        state.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blockedCells", gameBoard.getBlockedPositions());
        state.put("status", getStatus().toString());
        state.put("movesCount", getMoveCount());
        state.put("invalidMovements", getInvalidMovements());
        state.put("maxMovements", getMaxMovements());
        state.put("boardSize", boardSize);
        state.put("playerWon", getStatus().equals(GameStatus.PLAYER_WON));
        state.put("playerName", getPlayerId() == null ? "NN": getPlayerId());
        
        long gameDurationSeconds = 0;
        if(getFinishedAt() != null)
        {
        	gameDurationSeconds = Duration.between(getCreatedAt(), getFinishedAt()).getSeconds();
        }
        state.put("gameDurationSeconds", gameDurationSeconds);
		return state; //sonUtils.toJson(this);

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getSerializableState");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromSerializable(Object serializedState) {
		// TODO: Restaurar el estado desde una representación serializada
		// Debe ser compatible con getSerializableState()
		// Manejar errores y validar la integridad de los datos

		if (serializedState instanceof Map) {
			Map<String, Object> state = (Map<String, Object>) serializedState;

			String id = (String) state.get("gameId");
			if (id == null || !id.equals(this.gameId))
				/*
				 * E.OSORIO.
				 * 
				 * No está grabado el ID o no es el mismo Juego
				 */
				return;
			// Restaurar posición del gato
			Map<String, Integer> catPos = (Map<String, Integer>) state.get("catPosition");
			if (catPos != null) {
				this.catPosition = new HexPosition(catPos.get("q"), catPos.get("r"));
			}

			// Restaurar estado del juego
			String statusStr = (String) state.get("status");
			if (statusStr != null) {
				if(GameStatus.valueOf(statusStr) != null)
					setStatus(GameStatus.valueOf(statusStr));
			}

			String moveCountStr = (String) state.get("moveCount");
			if (moveCountStr != null) {
				try {
					moveCount = Integer.parseInt(moveCountStr);
				} catch (Exception e) {
					setMaxMovements(0);
				}
			}
			String invalidMovementsStr = (String) state.get("invalidMovements");
			if (invalidMovementsStr != null) {
				try {
					setInvalidMovements(Integer.parseInt(invalidMovementsStr));
				} catch (Exception e) {
					setInvalidMovements(0);
				}
			}
			String maxMovements = (String) state.get("maxMovements");
			if (maxMovements != null) {
				try {
					setMaxMovements(Integer.parseInt(maxMovements));
				} catch (Exception e) {
					setMaxMovements(0);
				}
			}
			Set<HexPosition> blockedCells =  (Set<HexPosition>) state.get("blockedCells");
			if (blockedCells != null) {
				setBloquedPositions(blockedCells);
			}

		}
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// restoreFromSerializable");
	}

	// Métodos auxiliares que los estudiantes pueden implementar

	/**
	 * TODO: Verificar si el gato está en el borde del tablero. Los estudiantes
	 * deben definir qué constituye "el borde".
	 */
	private boolean isCatAtBorder() {
		return this.gameBoard.isAtBorder(catPosition);
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Verificar si el gato está completamente atrapado. Debe verificar si
	 * todas las posiciones adyacentes están bloqueadas.
	 */
	private boolean isCatTrapped() {
		return gameBoard.getAdjacentPositions(catPosition).stream().allMatch(gameBoard::isBlocked);
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Calcular estadísticas avanzadas del juego. Puede incluir métricas como
	 * eficiencia, estrategia, etc.
	 */
	public Map<String, Object> getAdvancedStatistics() {
		Map<String, Object> state = new HashMap<>();
        state.put("gameId", getGameId());
        state.put("catPosition", Map.of("q", catPosition.getQ(), "r", catPosition.getR()));
        state.put("blockedCells", gameBoard.getBlockedPositions());
        state.put("status", getStatus().toString());
        state.put("moveCount", getMoveCount());
        state.put("boardSize", boardSize);
		return state;
		//throw new UnsupportedOperationException("Método adicional para implementar");
	}

	// Getters adicionales que pueden ser útiles

	public HexGameBoard getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(HexGameBoard gameBoard) {
		this.gameBoard = gameBoard;
		this.boardSize = gameBoard.getSize();
	}

	public int getBoardSize() {
		return boardSize;
	}

	public int getInvalidMovements() {
		return invalidMovements;
	}

	public void setInvalidMovements(int invalidMovements) {
		this.invalidMovements = invalidMovements;
		this.updateGameStatus();
	}

	public int getMaxMovements() {
		return maxMovements;
	}

	public void setMaxMovements(int maxMovements) {
		this.maxMovements = maxMovements;
	}

	public void setMoveCount(int moveCount) {
		super.moveCount = moveCount;
		this.updateGameStatus();
	}
	// TODO: Los estudiantes pueden agregar más métodos según necesiten
	// Ejemplos: getDifficulty(), getTimeElapsed(), getPowerUps(), etc.

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
		this.updateGameStatus();
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public LocalDateTime getPausedAt() {
		return pausedAt;
	}

	public void setPausedAt(LocalDateTime pausedAt) {
		this.pausedAt = pausedAt;
	}

	public void togglePause() {
		// No corresponde aplicar esta operación
		if (!getStatus().equals(GameStatus.IN_PROGRESS) && !getStatus().equals(GameStatus.PAUSED))
			return;

		if (getStatus().equals(GameStatus.IN_PROGRESS))
			setStatus(GameStatus.PAUSED);
		else
			setStatus(GameStatus.IN_PROGRESS);

		this.pausedAt = LocalDateTime.now();
		
		notifyStateChanged();
	}
	

	public LEVEL_OF_DIFFICULTY getLevelOfDifficulty() {
		return levelOfDifficulty;
	}

	public void setLevelOfDifficulty(LEVEL_OF_DIFFICULTY levelOfDifficulty) {
		this.levelOfDifficulty = levelOfDifficulty;
		this.updateGameStatus();
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	
	/**
	 * Método auxiliar para establecer el conjunto de puntos bloqueados.
	 * @param bloquedPosition Conjunto de puntos bloqueados.
	 */
	public void setBloquedPositions(Set<HexPosition> bloquedPosition) {
		this.gameBoard.setBloquedPositions(bloquedPosition);
	}
	
}
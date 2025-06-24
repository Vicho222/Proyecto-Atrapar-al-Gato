package com.atraparalagato.impl.service;

import java.time.Duration;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameState.GameStatus;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.service.GameService;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.example.strategy.BFSCatMovement;
import com.atraparalagato.impl.factories.GameIdGenerator;
import com.atraparalagato.impl.factories.GameStateFactory;
import com.atraparalagato.impl.factories.HexBoardFactory;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexGameState.LEVEL_OF_DIFFICULTY;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.strategy.AStarCatMovement;

/**
 * Implementación esqueleto de GameService para el juego hexagonal.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar: - Orquestación de todos los componentes del juego -
 * Lógica de negocio compleja - Manejo de eventos y callbacks - Validaciones
 * avanzadas - Integración con repositorio y estrategias
 */
@Service
public class HexGameService extends GameService<HexPosition> {

	private static Logger LOG = Logger.getLogger(HexGameService.class.getName());
	/**
	 * Estados en los que puede estar el HexGameStatus para poder aplicar el
	 * togglePause
	 */
	private static final List<GameStatus> PAUSED_VALID_STATUS = List.of(GameStatus.IN_PROGRESS, GameStatus.PAUSED);

	// TODO: Los estudiantes deben inyectar dependencias
	// Ejemplos: repository, movementStrategy, validator, etc.

	public HexGameService(HexBoardFactory boardFactory, CatMovementStrategy<HexPosition> movementStrategy,
			DataRepository<GameState<HexPosition>, String> repository, GameIdGenerator idGenerator,
			GameStateFactory stateFactory) {
		// TODO: Los estudiantes deben inyectar las dependencias requeridas

		super(boardFactory.apply(10), // gameBoard - TODO: Crear HexGameBoard
				movementStrategy, // movementStrategy - TODO: Crear estrategia de movimiento
				repository, // gameRepository - TODO: Crear repositorio
				(Supplier<String>) idGenerator, // gameIdGenerator - TODO: Crear generador de IDs
				(Function<Integer, GameBoard<HexPosition>>) boardFactory, // boardFactory - TODO: Crear factory de
																			// tableros
				(Function<String, GameState<HexPosition>>) stateFactory // gameStateFactory - TODO: Crear factory de
																		// estados
		);
		// TODO: Inicializar dependencias y configuración
		// Pista: Usar el patrón Factory para crear componentes
		// throw new UnsupportedOperationException("Los estudiantes deben implementar el
		// constructor");
	}

	/**
	 * TODO: Crear un nuevo juego con configuración personalizada. Debe ser más
	 * sofisticado que ExampleGameService.
	 */
	public HexGameState createGame(int boardSize, String difficulty, Map<String, Object> options) {
		// TODO: Implementar creación de juego avanzada
		// Considerar:
		// 1. Validar parámetros de entrada
		// 2. Crear tablero según dificultad
		// 3. Configurar estrategia del gato según dificultad
		// 4. Inicializar estado del juego
		// 5. Guardar en repositorio
		// 6. Configurar callbacks y eventos

		String gameId = generateGameId();
		GameBoard<HexPosition> board = createGameBoard(boardSize);
		GameState<HexPosition> gameState = createGameState(gameId);
		HexGameState hexGameState = (HexGameState) gameState;

		if (LEVEL_OF_DIFFICULTY.valueOf(difficulty) != null)
			hexGameState.setLevelOfDifficulty(LEVEL_OF_DIFFICULTY.valueOf(difficulty));

		initializeGame(gameState, board);
		configureGameCallbacks(gameState);

		GameState<HexPosition> savedState = persistGameState(gameState);
		onGameStarted(savedState);

		return (HexGameState) savedState;

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// createGame");
	}

	public Optional<String> obtainGameStatus(String gameId) {
		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return Optional.empty();
		}
		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();

		return Optional.of(gameState.getStatus().name());
	}

	/**
	 * TODO: Ejecutar movimiento del jugador con validaciones avanzadas.
	 */
	public Optional<HexGameState> executePlayerMove(String gameId, HexPosition position, String playerId) {
		// TODO: Implementar movimiento del jugador
		// Considerar:
		// 1. Validar que el juego existe y está activo
		// 2. Validar que el jugador puede hacer el movimiento
		// 3. Validar la posición según reglas del juego
		// 4. Ejecutar el movimiento
		// 5. Mover el gato usando estrategia apropiada
		// 6. Actualizar estado del juego
		// 7. Guardar cambios en repositorio
		// 8. Notificar eventos

		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return Optional.empty();
		}
		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();

		// Primero se valida que el juego es del mismo jugador o que no se ha definido
		// jugador.
		if (gameState.getPlayerId() != null && !gameState.getPlayerId().equals(playerId))
			// No puede jugar porque el juego no le pertenece.
			return Optional.empty();

		// Se intenta hacer el movimiento
		if (!gameState.executeMove(position)) {
			// No es posible realizar el movimiento
			return Optional.of(gameState);
		}

		// Se actualiza el jugador
		gameState.setPlayerId(playerId);
		// Se realizó el movimiento, ahora toca mover el gato
		executeCatMove(gameState);

		// Almacenamos el nuevo estado
		HexGameState updatedState = (HexGameState) persistGameState(gameState);

		// Llamamos al callback
		onMoveExecuted(updatedState, position);

		return Optional.of(updatedState);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// executePlayerMove");
	}

	/**
	 * TODO: Obtener estado del juego con información enriquecida.
	 */
	public Optional<Map<String, Object>> getEnrichedGameState(String gameId) {
		// TODO: Obtener estado enriquecido del juego
		// Incluir:
		// 1. Estado básico del juego
		// 2. Estadísticas avanzadas
		// 3. Sugerencias de movimiento
		// 4. Análisis de la partida
		// 5. Información del tablero

		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return Optional.empty();
		}

		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();
		Map<String, Object> info = new HashMap<>();

		info.put("Score", gameState.calculateScore());
		info.put("GameId", gameState.getGameId());
		info.put("BoardSize", gameState.getBoardSize());
		info.put("InvalidMovements", gameState.getInvalidMovements());
		info.put("MaxMovements", gameState.getMaxMovements());
		info.put("CatPosition", gameState.getCatPosition());
		info.put("CreatedAt", gameState.getCreatedAt());
		info.put("MoveCount", gameState.getMoveCount());

		Map<String, Object> advanced = gameState.getAdvancedStatistics();
		if (advanced == null)
			return Optional.of(info);

		info.put("Advanced", advanced);

		return Optional.of(info);

//		throw new UnsupportedOperationException("Los estudiantes deben implementar getEnrichedGameState");
	}

	/**
	 * TODO: Obtener sugerencia inteligente de movimiento.
	 */
	public Optional<HexPosition> getIntelligentSuggestion(String gameId, String difficulty) {
		// TODO: Generar sugerencia inteligente
		// Considerar:
		// 1. Analizar estado actual del tablero
		// 2. Predecir movimientos futuros del gato
		// 3. Evaluar múltiples opciones
		// 4. Retornar la mejor sugerencia según dificultad
		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return Optional.empty();
		}
		String myDifficult = difficulty;
		HexGameState gameState = (HexGameState) gameStateOpt.get();
		if(difficulty == null)
		{
			myDifficult = gameState.getLevelOfDifficulty().name();
		}
		
		CatMovementStrategy<HexPosition>strategy = createMovementStrategy(myDifficult, gameState.getGameBoard());
		
		HexPosition targetPosition = getTargetPosition(gameState);
		
		if(targetPosition == null)
			return Optional.empty();
		
		HexPosition catPosition = gameState.getCatPosition();
		
		Optional<HexPosition> result = strategy.findBestMove(catPosition, targetPosition);
		
		return result;
		//throw new UnsupportedOperationException("Los estudiantes deben implementar getIntelligentSuggestion");
	}

	/**
	 * TODO: Analizar la partida y generar reporte.
	 */
	public Map<String, Object> analyzeGame(String gameId) {
		// TODO: Generar análisis completo de la partida
		// Incluir:
		// 1. Eficiencia de movimientos
		// 2. Estrategia utilizada
		// 3. Momentos clave de la partida
		// 4. Sugerencias de mejora
		// 5. Comparación con partidas similares
		throw new UnsupportedOperationException("Los estudiantes deben implementar analyzeGame");
	}

	/**
	 * TODO: Obtener estadísticas globales del jugador.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getPlayerStatistics(String playerId) {
		// TODO: Calcular estadísticas del jugador
		// Incluir:
		// 1. Número de partidas jugadas
		// 2. Porcentaje de victorias
		// 3. Puntuación promedio
		// 4. Tiempo promedio por partida
		// 5. Progresión en el tiempo

		// Se define el mapper.
		Function<Object, GameRecord> mapper = row -> {
			GameState<HexPosition> m = (GameState<HexPosition>) row;
			HexGameState gameState = (HexGameState) m;
			return new GameRecord(gameState.getGameId(), gameState.getPlayerId(), gameState.getStatus().name(),
					gameState.getMoveCount(), gameState.calculateScore(), gameState.getCreatedAt(),
					gameState.getFinishedAt()

			);
		};

		String query = String.format("SELECT * FROM GAMESSTATES WHERE PLAYER = '%s'", playerId);
		List<GameRecord> results = gameRepository.executeCustomQuery(query, mapper);

		if (results == null || results.isEmpty())
			return new HashMap<>();

		int nroGames = results.size();
		double percentWon = results.stream().filter(r -> r.getStatus().equalsIgnoreCase(GameStatus.PLAYER_WON.name()))
				.count() / nroGames;
		double mediaScore = results.stream().filter(r -> r.getStatus().equalsIgnoreCase(GameStatus.PLAYER_WON.name()))
				.mapToLong(r -> r.getPoints()).sum() / nroGames;
		double mediaTime = results.stream()
				.mapToLong(r -> Duration.between(r.getCreatedAt(), r.getFinishedAt()).toMillis()).sum() / nroGames;

		Map<String, Long> wonByWeek = results.stream().filter(p -> p.getStatus().equals(GameStatus.PLAYER_WON.name()))
				.collect(Collectors.groupingBy(p -> {
					WeekFields wf = WeekFields.of(Locale.getDefault());
					int year = p.getCreatedAt().getYear();
					int week = p.getCreatedAt().get(wf.weekOfWeekBasedYear());
					return year + "-W" + String.format("%02d", week);
				}, Collectors.counting()));

		Map<String, Object> result = new HashMap<>();
		result.put("Número de partidas jugadas", nroGames);
		result.put("Porcentaje de victorias", percentWon);
		result.put("Puntuación promedio", mediaScore);
		result.put("Tiempo promedio por partida", mediaTime);
		result.put("Progresión en el tiempo", wonByWeek);

		return result;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getPlayerStatistics");
	}

	/**
	 * TODO: Configurar dificultad del juego.
	 */
	public void setGameDifficulty(String gameId, String difficulty) {
		// TODO: Cambiar dificultad del juego
		// Afectar:
		// 1. Estrategia de movimiento del gato
		// 2. Tiempo límite por movimiento
		// 3. Ayudas disponibles
		// 4. Sistema de puntuación

		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return;
		}
		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();
		LEVEL_OF_DIFFICULTY levleOfDifficulty = LEVEL_OF_DIFFICULTY.valueOf(difficulty);
		if (levleOfDifficulty == null) {
			return;
		}
		gameState.setLevelOfDifficulty(levleOfDifficulty);
		gameRepository.save(gameState);
		onGameStateChanged(gameState);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// setGameDifficulty");
	}

	/**
	 * TODO: Pausar/reanudar juego.
	 */
	public boolean toggleGamePause(String gameId) {
		// TODO: Manejar pausa del juego
		// Considerar:
		// 1. Guardar timestamp de pausa
		// 2. Actualizar estado del juego
		// 3. Notificar cambio de estado
		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora falso, porque no existe el juego
			return false;
		}
		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();

		if (!PAUSED_VALID_STATUS.contains(gameState.getStatus()))
			// No se puede aplicar porque ya finalizó
			return false;

		gameState.togglePause();
		gameRepository.save(gameState);
		onGameStateChanged(gameState);
		return true;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// toggleGamePause");
	}

	/**
	 * TODO: Deshacer último movimiento.
	 */
	public Optional<HexGameState> undoLastMove(String gameId) {
		// TODO: Implementar funcionalidad de deshacer
		// Considerar:
		// 1. Mantener historial de movimientos
		// 2. Restaurar estado anterior
		// 3. Ajustar puntuación
		// 4. Validar que se puede deshacer
		throw new UnsupportedOperationException("Los estudiantes deben implementar undoLastMove");
	}

	/**
	 * TODO: Obtener ranking de mejores puntuaciones.
	 */
	public List<Map<String, Object>> getLeaderboard(int limit) {
		// TODO: Generar tabla de líderes
		// Incluir:
		// 1. Mejores puntuaciones
		// 2. Información del jugador
		// 3. Fecha de la partida
		// 4. Detalles de la partida

		String query = "SELECT * FROM GAMESSTATES ORDER BY POINTS LIMITS " + limit;
		Function<Object, HexGameState> mapper = row -> {
			return (HexGameState) row;
		};

		List<HexGameState> results = gameRepository.executeCustomQuery(query, mapper);
		return results.stream().map(r -> Map.of("gameId", r.getGameId(), "Puntuación", r.getPoints(), "Jugador",
				r.getPlayerId(), "Fecha Partida", r.getCreatedAt(), "Detalle Juego",
				Map.of("Tamaño Tablero", r.getBoardSize(), "Posición Gato", r.getCatPosition(), "Estado del Juego",
						r.getStatus(), "Dificultad", r.getLevelOfDifficulty(), "Fecha fin Juego", r.getFinishedAt())))
				.toList();

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getLeaderboard");
	}

	// Métodos auxiliares que los estudiantes pueden implementar

	/**
	 * TODO: Validar movimiento según reglas avanzadas.
	 */
	private boolean isValidAdvancedMove(HexGameState gameState, HexPosition position, String playerId) {
		throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Ejecutar movimiento del gato usando estrategia apropiada.
	 */
	@SuppressWarnings("unchecked")
	private void executeCatMove(HexGameState gameState, String difficulty) {
		var strategy = createMovementStrategy(difficulty, gameState.getGameBoard());
		Optional<HexPosition> movement = strategy.findBestMove(gameState.getCatPosition(),
				getTargetPosition(gameState));
		if (movement.isEmpty())
			return;

		gameState.executeMove(movement.get());
//		throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Calcular puntuación avanzada.
	 */
	private int calculateAdvancedScore(HexGameState gameState, Map<String, Object> factors) {

		int score = 0;
		for (Entry<String, Object> entry : factors.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof Integer iValue) {
				// Procesa todo aquello que es entero.
				if (key.equalsIgnoreCase("boardSize")) {
					score += gameState.getBoardSize() * iValue;
				}
				if (key.equalsIgnoreCase("moveCount")) {
					// Se ve la cantidad de movimientos que quedan y si es que ha ganado o no
					if (gameState.hasPlayerWon())
						score += (gameState.getMaxMovements() - gameState.getMoveCount()) * iValue;
				}
				if (key.equalsIgnoreCase("durationTime")) {
					// Entre menor es el tiempo que demoró en ganar, mayor puntaje
					if (gameState.hasPlayerWon())
						score += 1 / Duration.between(gameState.getCreatedAt(), gameState.getFinishedAt()).toMillis()
								* iValue * 1000;
				}
			}
		}
		return score;
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Notificar eventos del juego.
	 */
	private void notifyGameEvent(String gameId, String eventType, Map<String, Object> eventData) {
		//throw new UnsupportedOperationException("Método auxiliar para implementar");
		
		LOG.info(String.format("Juego: %s Evento: %s Datos:%s", gameId, eventType, eventData));
	}

	/**
	 * TODO: Crear factory de estrategias según dificultad.
	 */
	private CatMovementStrategy<HexPosition> createMovementStrategy(String difficulty, HexGameBoard board) {
		if (difficulty != null && difficulty.toUpperCase().contains(LEVEL_OF_DIFFICULTY.EASY.name()))
			return new AStarCatMovement(board);

		// Por defecto facil
		return new BFSCatMovement(board);

		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	// Métodos abstractos requeridos por GameService

	@Override
	protected void initializeGame(GameState<HexPosition> gameState, GameBoard<HexPosition> gameBoard) {
		// TODO: Inicializar el juego con estado y tablero

		HexGameState hexGameState = (HexGameState) gameState;
		hexGameState.setCatPosition(new HexPosition(0, 0));

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// initializeGame");
	}

	@Override
	public boolean isValidMove(String gameId, HexPosition position) {
		Optional<GameState<HexPosition>> optional = gameRepository.findById(gameId);
		if (optional.isEmpty())
			return false;

		HexGameState gameState = (HexGameState) optional.get();
		boolean isInvalid = gameState.isGameFinished() || gameState.getGameBoard().isAtBorder(position)
				|| gameState.getGameBoard().isBlocked(position) || gameState.getCatPosition().equals(position);
		return !isInvalid;
//		throw new UnsupportedOperationException("Los estudiantes deben implementar isValidMove");
	}

	@Override
	public Optional<HexPosition> getSuggestedMove(String gameId) {
		// TODO: Obtener sugerencia de movimiento
		Optional<GameState<HexPosition>> optional = gameRepository.findById(gameId);
		if (optional.isEmpty())
			return Optional.empty();

		HexGameState gameState = (HexGameState) optional.get();
		HexPosition catPosition = gameState.getCatPosition();
		HexPosition targetPosition = getTargetPosition(gameState);

		return movementStrategy.findBestMove(catPosition, targetPosition);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getSuggestedMove");
	}

	/**
	 * Para calcular el target, obtiene todos los bordes y luego busca el que tiene
	 * menor distancia.
	 */
	@Override
	protected HexPosition getTargetPosition(GameState<HexPosition> gameState) {
		// TODO: Obtener posición objetivo para el gato
		if (!HexGameState.class.isInstance(gameState))
			return null;

		HexGameState hexGameState = (HexGameState) gameState;
		HexGameBoard board = hexGameState.getGameBoard();

		int size = hexGameState.getBoardSize();
		Predicate<HexPosition> isOnBorder = position -> Math.abs(position.getQ()) == size
				|| Math.abs(position.getR()) == size || Math.abs(position.getS()) == size;

		List<HexPosition> targetPositions = board.getPositionsWhere(isOnBorder);
		if (targetPositions == null || targetPositions.isEmpty())
			return null;

		HexPosition catPosition = hexGameState.getCatPosition();

		// Se considera objetivo, el borde más cercano.
		return targetPositions.stream().min(Comparator.comparing(pos -> catPosition.distanceTo(pos))).orElse(null);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// getTargetPosition");
	}

	@Override
	public Object getGameStatistics(String gameId) {
		// TODO: Obtener estadísticas del juego
		
		Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			// Se retora un Optiona vacío
			return null;
		}
		// El juego existe
		HexGameState gameState = (HexGameState) gameStateOpt.get();
		return gameState.getAdvancedStatistics();
		
		//throw new UnsupportedOperationException("Los estudiantes deben implementar getGameStatistics");
	}

	// Event handlers - Hook methods para extensibilidad
	protected void onGameStarted(GameState<HexPosition> gameState) {
		// Default: no operation
		LOG.info(gameState.getGameId() + " ha sido iniciado !!!");
	}

	protected void onMoveExecuted(GameState<HexPosition> gameState, HexPosition position) {
		// Default: no operation
		LOG.info(gameState.getGameId() + " ha realizado movimiento de jugador a " + position.toString() + " !!!");
	}

	protected void onCatMoved(GameState<HexPosition> gameState, HexPosition newPosition) {
		// Default: no operation
		LOG.info(gameState.getGameId() + " ha realizado movimiento del gato a " + newPosition.toString() + " !!!");
	}

	protected void onGameStateChanged(GameState<HexPosition> gameState) {
		// Default: no operation
		LOG.info(gameState.getGameId() + " ha realizado cambiado de estado " + gameState.toString() + " !!!");
	}

	protected void onGameEnded(GameState<HexPosition> gameState) {
		// Default: no operation
		LOG.info(gameState.getGameId() + " ha realizado finalizado !!!");
	}
}
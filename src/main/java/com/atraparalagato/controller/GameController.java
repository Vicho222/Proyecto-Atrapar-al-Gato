package com.atraparalagato.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.example.service.ExampleGameService;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.service.HexGameService;

/**
 * Controlador del juego que alterna entre implementaciones de ejemplo y de
 * estudiantes.
 * 
 * Usa la propiedad 'game.use-example-implementation' para determinar qué
 * implementación usar: - true: Usa las implementaciones del paquete 'example'
 * (básicas, para guía) - false: Usa las implementaciones del paquete 'impl' (de
 * los estudiantes)
 */
@RestController // Hace que Springboot lo incie solo (Clase que atiende las llamadas REST)
@RequestMapping("/api/game") // Prefijo de todas las llamadas REST
@CrossOrigin(origins = "*") // Se pueden conectar desde cualquier computador
public class GameController {

	// Saca el valor desde el archivo application.properties
	@Value("${game.use-example-implementation:true}")
	private boolean useExampleImplementation;

	private final ExampleGameService exampleGameService;
	private final HexGameService hexGameService;

	public GameController(HexGameService hexGameService) {
		this.exampleGameService = new ExampleGameService();
		this.hexGameService = hexGameService;
	}

	/**
	 * Inicia un nuevo juego.
	 */
	@GetMapping("/start")
	public ResponseEntity<Map<String, Object>> startGame(@RequestParam(defaultValue = "5") int boardSize) {
		try {
			if (useExampleImplementation) {
				return startGameWithExample(boardSize);
			} else {
				return startGameWithStudentImplementation(boardSize);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al iniciar el juego: " + e.getMessage()));
		}
	}

	/**
	 * Ejecuta un movimiento del jugador.
	 */
	@PostMapping("/block")
	public ResponseEntity<Map<String, Object>> blockPosition(@RequestParam String gameId, @RequestParam int q,
			@RequestParam int r) {
		try {
			HexPosition position = new HexPosition(q, r);

			if (useExampleImplementation) {
				return blockPositionWithExample(gameId, position);
			} else {
				return blockPositionWithStudentImplementation(gameId, position);
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al ejecutar movimiento: " + e.getMessage()));
		}
	}

	/**
	 * Ejecuta un togglePause del juego.
	 */
	@PostMapping("/togglepause")
	public ResponseEntity<Map<String, Object>> togglePause(@RequestParam String gameId) {
		try {

			if (useExampleImplementation) {
				return ResponseEntity.ok(Map.of("error", "Error pause no implementado en el ejmplo."));
			} else {
				return ResponseEntity.ok(Map.of("message", hexGameService.toggleGamePause(gameId)));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al ejecutar movimiento: " + e.getMessage()));
		}
	}

	/**
	 * Obtiene el estado actual del juego.
	 */
	@GetMapping("/state/{gameId}")
	public ResponseEntity<Map<String, Object>> getGameState(@PathVariable String gameId) {
		try {
			if (useExampleImplementation) {
				return getGameStateWithExample(gameId);
			} else {
				return getGameStateWithStudentImplementation(gameId);
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al obtener estado del juego: " + e.getMessage()));
		}
	}

	/**
	 * Obtiene estadísticas del juego.
	 */
	@GetMapping("/statistics/{gameId}")
	public ResponseEntity<Map<String, Object>> getGameStatistics(@PathVariable String gameId) {
		try {
			if (useExampleImplementation) {
				Map<String, Object> stats = exampleGameService.getGameStatistics(gameId);
				return ResponseEntity.ok(stats);
			} else {
				Object result = hexGameService.getGameStatistics(gameId);
				if (result == null)
					return ResponseEntity.ok(Map.of("message", "No se encuentra el juego:" + gameId));
				return ResponseEntity.ok(Map.of("message", result));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al obtener estadísticas: " + e.getMessage()));
		}
	}

	/**
	 * Obtiene sugerencia de movimiento.
	 */
	@GetMapping("/suggestion/{gameId}")
	public ResponseEntity<Map<String, Object>> getSuggestion(@PathVariable String gameId) {
		try {
			if (useExampleImplementation) {
				Optional<HexPosition> suggestion = exampleGameService.getSuggestedMove(gameId);
				if (suggestion.isPresent()) {
					HexPosition pos = suggestion.get();
					return ResponseEntity.ok(Map.of("suggestion", Map.of("q", pos.getQ(), "r", pos.getR()), "message",
							"Sugerencia: bloquear posición adyacente al gato"));
				} else {
					return ResponseEntity.ok(Map.of("message", "No hay sugerencias disponibles"));
				}
			} else {
				Optional<HexPosition> result = hexGameService.getIntelligentSuggestion(gameId, null);
				return ResponseEntity.ok(Map.of("message", result.isEmpty() ? "Sin Sugerencia!!" : result.get()));
			}
		} catch (Exception e) {
			return ResponseEntity.internalServerError()
					.body(Map.of("error", "Error al obtener sugerencia: " + e.getMessage()));
		}
	}

	/**
	 * Obtiene información sobre qué implementación se está usando.
	 */
	@GetMapping("/implementation-info")
	public ResponseEntity<Map<String, Object>> getImplementationInfo() {
		Map<String, Object> info = new HashMap<>();
		info.put("useExampleImplementation", useExampleImplementation);
		info.put("currentImplementation", useExampleImplementation ? "example" : "impl");
		info.put("description", useExampleImplementation ? "Usando implementaciones de ejemplo (básicas)"
				: "Usando implementaciones de estudiantes");

		return ResponseEntity.ok(info);
	}

	@GetMapping("/high-scores")
	public ResponseEntity<List<Map<String, Object>>> getHighScores(@RequestParam int limit) {
	    try {
	        if (useExampleImplementation) {
	            Map<String, Object> errorMap = Map.of(
	                "error", "Example implementation not available yet",
	                "message", "Los profesores deben completar sus implementaciones en el paquete 'example'",
	                "implementation", "example"
	            );
	            return ResponseEntity.ok(List.of(errorMap));
	        } else {
	            List<Map<String, Object>> result = hexGameService.getLeaderboard(limit);
	            return ResponseEntity.ok(result);
	        }
	    } catch (Exception e) {
	        Map<String, Object> error = Map.of(
	            "error", "Error al obtener sugerencia: " + e.getMessage()
	        );
	        return ResponseEntity.internalServerError().body(List.of(error));
	    }

	}
	
	@GetMapping("/winning-scores")
	public ResponseEntity<List<Map<String, Object>>> getWinningScores() {
	    try {
	        if (useExampleImplementation) {
	            Map<String, Object> errorMap = Map.of(
	                "error", "Example implementation not available yet",
	                "message", "Los profesores deben completar sus implementaciones en el paquete 'example'",
	                "implementation", "example"
	            );
	            return ResponseEntity.ok(List.of(errorMap));
	        } else {
	        	List<Map<String, Object>> result = hexGameService.getWonGames();
	            return ResponseEntity.ok(result);
	        }
	    } catch (Exception e) {
	        Map<String, Object> error = Map.of(
	            "error", "Error al obtener sugerencia: " + e.getMessage()
	        );
	        return ResponseEntity.internalServerError().body(List.of(error));
	    }

	}

	@SuppressWarnings("unchecked")
	@PostMapping("/save-score")
	public ResponseEntity<Map<String, Object>> saveScore(@RequestParam String gameId,  @RequestParam String playerName) {
	    try {
	        if (useExampleImplementation) {
	            Map<String, Object> errorMap = Map.of(
	                "error", "Example implementation not available yet",
	                "message", "Los profesores deben completar sus implementaciones en el paquete 'example'",
	                "implementation", "example"
	            );
	            return ResponseEntity.ok(errorMap);
	        } else {
	            var result = hexGameService.getGame(gameId);
	            if(result.isEmpty()) {
		            Map<String, Object> errorMap = Map.of(
			                "error", "Juego no encontrado",
			                "message", "El juego con el didentificador " + gameId + " no existe en la Base de Datos",
			                "implementation", "impl"
			            );
			            return ResponseEntity.ok(errorMap);
	            }
	            var hexGameState = (HexGameState)result.get();
	            hexGameState.setPlayerId(playerName);
	            hexGameState = hexGameService.saveGameState(hexGameState);
	            return ResponseEntity.ok((Map<String, Object>)hexGameState.getSerializableState());
	        }
	    } catch (Exception e) {
	        Map<String, Object> error = Map.of(
	            "error", "Error al obtener sugerencia: " + e.getMessage()
	        );
	        return ResponseEntity.internalServerError().body(error);
	    }
	}

	// Métodos privados para implementación de ejemplo

	private ResponseEntity<Map<String, Object>> startGameWithExample(int boardSize) {
		var gameState = exampleGameService.startNewGame(boardSize);

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("boardSize", boardSize);
		response.put("implementation", "example");

		return ResponseEntity.ok(response);
	}

	private ResponseEntity<Map<String, Object>> blockPositionWithExample(String gameId, HexPosition position) {
		var gameStateOpt = exampleGameService.executePlayerMove(gameId, position);

		if (gameStateOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		var gameState = gameStateOpt.get();

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("implementation", "example");

		return ResponseEntity.ok(response);
	}

	private ResponseEntity<Map<String, Object>> getGameStateWithExample(String gameId) {
		var gameStateOpt = exampleGameService.getGameState(gameId);

		if (gameStateOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		var gameState = gameStateOpt.get();

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("implementation", "example");

		return ResponseEntity.ok(response);
	}

	// Métodos privados para implementación de estudiantes (placeholder)

	private ResponseEntity<Map<String, Object>> startGameWithStudentImplementation(int boardSize) {
		// TODO: Los estudiantes deben implementar esto usando sus propias clases

		var gameState = (HexGameState) hexGameService.startNewGame(boardSize);

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("implementation", "impl");

		return ResponseEntity.ok(response);
//        return ResponseEntity.ok(Map.of(
//            "error", "Student implementation not available yet",
//            "message", "Los estudiantes deben completar sus implementaciones en el paquete 'impl'",
//            "implementation", "impl"
//        ));
	}

	private ResponseEntity<Map<String, Object>> blockPositionWithStudentImplementation(String gameId,
			HexPosition position) {
		// TODO: Los estudiantes deben implementar esto usando sus propias clases

		Optional<GameState<HexPosition>> result = hexGameService.executePlayerMove(gameId, position);

		if (result.isEmpty())
			return ResponseEntity.ok(Map.of("error", "No se ha encontrado el juego", "message",
					"El identificador es inválido.", "implementation", "impl"));

		var gameState = (HexGameState) result.get();

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("implementation", "impl");

		return ResponseEntity.ok(response);

//        return ResponseEntity.ok(Map.of(
//            "error", "Student implementation not available yet",
//            "message", "Los estudiantes deben completar sus implementaciones en el paquete 'impl'",
//            "implementation", "impl"
//        ));
	}

	private ResponseEntity<Map<String, Object>> getGameStateWithStudentImplementation(String gameId) {
		// TODO: Los estudiantes deben implementar esto usando sus propias clases
		Optional<HexGameState> result = hexGameService.obtainGameStatus(gameId);

		if (result.isEmpty())
			return ResponseEntity.ok(Map.of("error", "No se ha encontrado el juego", "message",
					"El identificador es inválido.", "implementation", "impl"));

		var gameState = (HexGameState) result.get();

		Map<String, Object> response = new HashMap<>();
		response.put("gameId", gameState.getGameId());
		response.put("status", gameState.getStatus().toString());
		response.put("catPosition",
				Map.of("q", gameState.getCatPosition().getQ(), "r", gameState.getCatPosition().getR()));
		response.put("blockedCells", gameState.getGameBoard().getBlockedPositions());
		response.put("movesCount", gameState.getMoveCount());
		response.put("implementation", "impl");
		return ResponseEntity.ok(response);
//        return ResponseEntity.ok(Map.of(
//            "error", "Student implementation not available yet",
//            "message", "Los estudiantes deben completar sus implementaciones en el paquete 'impl'",
//            "implementation", "impl"
//        ));
	}
}
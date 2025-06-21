package com.atraparalagato.impl.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.atraparalagato.base.model.GameBoard;

/**
 * Implementación esqueleto de GameBoard para tableros hexagonales.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar: - Modularización: Separación de lógica de tablero
 * hexagonal - OOP: Herencia y polimorfismo - Programación Funcional: Uso de
 * Predicate y streams
 */
public class HexGameBoard extends GameBoard<HexPosition> {

	public HexGameBoard(int size) {
		super(size);
	}

	@Override
	protected Set<HexPosition> initializeBlockedPositions() {
		// TODO: Los estudiantes deben decidir qué estructura de datos usar
		// Opciones: HashSet, TreeSet, LinkedHashSet, etc.
		// Considerar rendimiento vs orden vs duplicados
		return new HashSet<>();
	}

	@Override
	protected boolean isPositionInBounds(HexPosition position) {
		// TODO: Implementar validación de límites para tablero hexagonal
		// Pista: Usar coordenadas axiales q, r, s
		// Condición: |q| <= size && |r| <= size && |s| <= size

		return Math.abs(position.getQ()) <= size && Math.abs(position.getR()) <= size
				&& Math.abs(position.getS()) <= size;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected boolean isValidMove(HexPosition position) {
		// TODO: Combinar validación de límites y estado actual
		// Debe verificar:
		// 1. Que la posición esté dentro de los límites
		// 2. Que la posición no esté ya bloqueada
		// 3. Cualquier regla adicional del juego
		return isPositionInBounds(position) && !isBlocked(position);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected void executeMove(HexPosition position) {
		// TODO: Actualizar el estado interno del tablero
		// Agregar la posición a las posiciones bloqueadas
		// Considerar si necesita validación adicional

		/*
		 * E.OSORIO
		 * Se agrega la posición a la lista de bloqueados si es valido el movimiento.
		 */
		if (isValidMove(position))
			blockedPositions.add(position);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) {
		// TODO: Implementar usando programación funcional
		// Generar todas las posiciones posibles del tablero
		// Filtrar usando el Predicate
		// Retornar como List
		//
		// Ejemplo de uso de streams:
		// return getAllPossiblePositions().stream()
		// .filter(condition)
		// .collect(Collectors.toList());

		return getAllPossiblePositions().stream().filter(condition).collect(Collectors.toList());
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	public List<HexPosition> getAdjacentPositions(HexPosition position) {
		// TODO: Obtener las 6 posiciones adyacentes en un tablero hexagonal
		// Direcciones hexagonales: (+1,0), (+1,-1), (0,-1), (-1,0), (-1,+1), (0,+1)
		// Filtrar las que estén dentro de los límites del tablero
		//
		// Pista: Crear array de direcciones y usar streams para mapear
		HexPosition[] directions = { new HexPosition(1, 0), // Este (Derecha)
				new HexPosition(1, -1), // Noreste (Derecha, Arriba)
				new HexPosition(0, -1), // Noroeste (Arriba)
				new HexPosition(-1, 0), // Oeste (Izquierda)
				new HexPosition(-1, 1), // Suroeste (Izquierda, Abajo)
				new HexPosition(0, 1) // Sureste (Abajo)
		};

		return Arrays.stream(directions).map(dir -> (HexPosition) position.add(dir)) // Se mapea a la position actual
																						// más cada una de las
																						// direcciones
				.filter(this::isPositionInBounds) // Se valida que las posiciones creadas estén dentro del borde
				.filter(pos -> !isBlocked(pos)) // Se filtra, manteniendo solo las que no se encuentran bloqueadas.
				.collect(Collectors.toList()); // Se construye la lista final

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	public boolean isBlocked(HexPosition position) {
		// TODO: Verificar si una posición está en el conjunto de bloqueadas
		// Método simple de consulta

		return blockedPositions.contains(position);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	/**
	 * E.OSORIO
	 * Verifica si la posición está en el borde del tablero. El borde son las
	 * posiciones donde el gato puede escapar. El jugador no puede seleccionar el
	 * borde
	 */
	public boolean isAtBorder(HexPosition position) {
		return Math.abs(position.getQ()) == size || Math.abs(position.getR()) == size
				|| Math.abs(position.getS()) == size;
	}

	// Método auxiliar que los estudiantes pueden implementar
	private List<HexPosition> getAllPossiblePositions() {
		// TODO: Generar todas las posiciones válidas del tablero
		// Usar doble loop para q y r, calcular s = -q - r
		// Filtrar posiciones que estén dentro de los límites

		List<HexPosition> elements = new ArrayList<>();
		/*
		 * E.OSORIO
		 * Se asegura que q y r no tomen el borde, pero no se sabe si al calcular el s
		 * va a estar dentro del los límites.
		 */
		for (int q = (-size + 1); q < size; q++) {
			for (int r = (-size + 1); r < size; r++) {
				elements.add(new HexPosition(q, r));
			}
		}
		/*
		 * E.OSORIO
		 * Filtra todas las posiciones válidas y que no estén en el borde
		 */
		return elements.stream().filter(p -> isPositionInBounds(p) && !isAtBorder(p)).collect(Collectors.toList());
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	// Hook method override - ejemplo de extensibilidad
	@Override
	protected void onMoveExecuted(HexPosition position) {
		// TODO: Los estudiantes pueden agregar lógica adicional aquí
		// Ejemplos: logging, notificaciones, validaciones post-movimiento
		super.onMoveExecuted(position);
	}
}
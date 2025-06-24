package com.atraparalagato.impl.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

/**
 * Implementación esqueleto de estrategia de movimiento usando algoritmo A*.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar: - Algoritmos: A* pathfinding - Programación
 * Funcional: Function, Predicate - Estructuras de Datos: PriorityQueue, Map,
 * Set
 */
public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

	public AStarCatMovement(GameBoard<HexPosition> board) {
		super(board);
	}

	@Override
	protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
		// TODO: Obtener movimientos válidos desde la posición actual
		// Usar board.getAdjacentPositions() y filtrar posiciones válidas
		// No incluir posiciones bloqueadas
		//
		// Pista: Usar streams para filtrar
		// return board.getAdjacentPositions(currentPosition).stream()
		// .filter(pos -> !board.isBlocked(pos))
		// .collect(Collectors.toList());

		/*
		 * E.OSORIO
		 * 
		 * Se buscan las posiciones adjacentes del gato, para se usadas después en
		 * determinar el mejor camino a una salida.
		 */
		return board.getAdjacentPositions(currentPosition).stream().filter(p -> !board.isBlocked(currentPosition))
				.collect(Collectors.toList());
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, HexPosition currentPosition,
			HexPosition targetPosition) {
		// TODO: Implementar selección del mejor movimiento usando A*
		// Calcular f(n) = g(n) + h(n) para cada movimiento posible
		// g(n) = costo desde inicio hasta n
		// h(n) = heurística desde n hasta objetivo
		// Retornar el movimiento con menor f(n)
		//
		// Pista: Usar Function para calcular costos y comparar

		/*
		 * E.OSORIO
		 * 
		 * Primero se habilita la función de heurística en base al targePosition Segundo
		 * se crea la función fScore que pide de parámetro la posición actual y la
		 * destino para calcular el costo de movers y la heurística.
		 */
		Function<HexPosition, Double> heuristicFunction = getHeuristicFunction(targetPosition);
		BiFunction<HexPosition, HexPosition, Double> fScoreFun = (currentPos, toPos) -> getMoveCost(currentPos, toPos)
				+ heuristicFunction.apply(toPos);

		/*
		 * E.OSORIO
		 * 
		 * Se recorre el stream de posiciones, se mapean a AStarNode y se busca el
		 * mínimo de los fScore y se retorna un Opcional que contiene la posición de ese
		 * elemento o nulo.
		 */
		return possibleMoves.stream()
				// Se crean AStarNode falso por cada posición
				.map(p -> new AStarNode(p, 0, fScoreFun.apply(currentPosition, p), null))
				// Se busca el menor en base al fScore calculado en el punto anterior
				.min(Comparator.comparing(a -> a.fScore))
				// Se mapea el AStarNode ganador a su posición.
				.map(o -> o.position);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
		// TODO: Implementar función heurística
		// Para tablero hexagonal, usar distancia hexagonal
		// La heurística debe ser admisible (nunca sobreestimar el costo real)
		//
		// Ejemplo:
		// return position -> position.distanceTo(targetPosition);

		/*
		 * E.OSORIO
		 * 
		 * Positon ya tiene implementado el cálculo de distancia, por lo tanto solo lo
		 * usamos. Esta distancia se considera como la heurística (h)
		 */
		return position -> position.distanceTo(targetPosition);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected Predicate<HexPosition> getGoalPredicate() {
		// TODO: Definir qué posiciones son objetivos válidos
		// Para "atrapar al gato", el objetivo son las posiciones del borde
		//
		// Pista: Una posición está en el borde si está en el límite del tablero
		// return position -> Math.abs(position.getQ()) == board.getSize() ||
		// Math.abs(position.getR()) == board.getSize() ||
		// Math.abs(position.getS()) == board.getSize();

		/*
		 * E.OSORIO
		 * 
		 * Cómo Position tiene implementado el isWithinBoudnds, el que indica si ls
		 * posición está en el borde sabiendo el tamñao del tablero, entonces lo usamos
		 * pasando el tamaño del tablero que me dieron al construir mi instancia.
		 */
		Predicate<HexPosition> isBorder = position -> position.isWithinBounds(this.board.getSize());
		return isBorder;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	protected double getMoveCost(HexPosition from, HexPosition to) {
		// TODO: Calcular costo de moverse entre dos posiciones
		// Para tablero hexagonal, normalmente es 1.0 para posiciones adyacentes
		// Puede variar según reglas específicas del juego

		/*
		 * E.OSORIO
		 * 
		 * Asumiendo que el costo de moverse a una posiciópn adyacente es 1.0, entonces
		 * basta con calcular la distancia entre las dos posiciones.
		 */
		return from.distanceTo(to);
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	public boolean hasPathToGoal(HexPosition currentPosition) {
		// TODO: Verificar si existe camino desde posición actual hasta cualquier
		// objetivo
		// Usar BFS o A* para explorar hasta encontrar una posición objetivo
		// Retornar true si se encuentra camino, false si no
		//
		// Pista: Usar getGoalPredicate() para identificar objetivos

		/*
		 * E.OSORIO
		 * 
		 * Se construye una lista enlazada para contener los elementos.
		 * 
		 * Estos elementos se sacan en el orden que fueron agrgados.
		 * 
		 * Se utiliza BFS (Breadth-First Search) búsqueda en anchura: Dado un nodo se
		 * analizan todos los adyacentes primero.
		 * 
		 * 1. Se toma el nodo de cola, se analiza nodo es meta => se termina
		 * 
		 * 2. Se agregan todos los vecinos del nodo que se está analizando al final de
		 * la cola.
		 * 
		 * 3. Se repite paso 1 hasta que no hayan nodos que revisar.
		 * 
		 */
		Predicate<HexPosition> isGoal = getGoalPredicate();
		LinkedList<HexPosition> queue = new LinkedList<>();

		queue.add(currentPosition);
		while (!queue.isEmpty()) {
			HexPosition current = queue.poll();

			if (isGoal.test(current))
				return true;

			for (HexPosition neighbor : getPossibleMoves(current)) {
				if (!queue.contains(neighbor)) {
					queue.add(neighbor);
				}
			}
		}

		return false; // no hay camino hacia ninguna meta

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	@Override
	public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
		// TODO: Implementar A* completo para obtener el camino completo
		// Usar PriorityQueue para nodos a explorar
		// Mantener Map de padres para reconstruir el camino
		// Retornar lista de posiciones desde inicio hasta objetivo
		//
		// Estructura sugerida:
		// 1. Inicializar estructuras de datos (openSet, closedSet, gScore, fScore,
		// cameFrom)
		// 2. Agregar posición inicial a openSet
		// 3. Mientras openSet no esté vacío:
		// a. Tomar nodo con menor fScore
		// b. Si es objetivo, reconstruir y retornar camino
		// c. Mover a closedSet
		// d. Para cada vecino válido, calcular scores y actualizar
		// 4. Si no se encuentra camino, retornar lista vacía

		/*
		 * E.OSORIO
		 * 
		 * Obtengo la función que calcula la heurística con respecto a targetPosition.
		 * Se hace acá, porque se crea cada vez que se ejecuta esta función.
		 */
		Function<HexPosition, Double> heuristicFunc = getHeuristicFunction(targetPosition);

		AStarNode startNode = new AStarNode(currentPosition, 0, 0, null);

		/*
		 * E.OSORIO Para que una cola de prioridad funcione, debe existir algún
		 * mecanismo de determinar el orden de los elementos. En este caso, los fScore
		 * menores irán primero.
		 */

		// 1. Inicializa openSet y closedSet
		PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.fScore));
		// Se crea un mapa en base a la posición con el nodo estrella correspondiente.
		Map<HexPosition, AStarNode> openSetMap = new HashMap<>();

		Set<AStarNode> closedSet = new HashSet<>();

		// 2. Agrega posición inicial a openSet
		openSet.add(startNode);

		// 3 Mientras openset no está vacío
		while (!openSet.isEmpty()) {
			// 3.a Se selecciona el nodo con menor fScor (Cola de prioridad por fScore)
			AStarNode current = openSet.poll(); // Toma el primer elemento en la cola vacía y lo quita

			// 3.b si es el objetivo, reconstruimos el camino y lo retornamos.
			if (current.position.equals(targetPosition))
				return reconstructPath(current);

			// 3.c agrega el current al closedSet
			closedSet.add(current);

			// Obtiene los nodos adyacentes
			List<HexPosition> adjacentPositions = board.getAdjacentPositions(currentPosition);
			// 3.d Por cada vecino calculo su score y actualizo (Agrego a la lista openSet.
			for (HexPosition pos : adjacentPositions) {

				/*
				 * E.OSORIO
				 * 
				 * Se calcula el costo de moverse desde donde está hacia la siguiente posición.
				 * 
				 * Luego se calcula el gScore a asignar, valor gScroe del padre más el costo de
				 * moverse a dicha posición.
				 */
				double moveCost = getMoveCost(current.position, pos);
				double gScore = current.gScore + moveCost;

				double hScore = heuristicFunc.apply(pos);

				double fScore = gScore + hScore; // (f = g + h)

				/*
				 * E.OSORIO
				 * 
				 * Se crea un nodo estrella por cada uno de los vecinos y se agrega al openSet
				 * teniendo cuidado de reemplazarlo cuando el nuevo nodo tengo un f menor.
				 */
				AStarNode neighbor = new AStarNode(pos, moveCost, fScore, current);
				// Tengo que validar qué si el nodo está en el openSet, dejo que el que tenga
				// menor f
				AStarNode oldNeighbor = openSetMap.get(pos);
				if (oldNeighbor == null || oldNeighbor.fScore > neighbor.fScore) {
					// quiere decir que el nuevo es mejor, o que no estaba en el openSet
					openSet.remove(oldNeighbor); // No es problema si oldNeighbor es nulo
					openSet.add(neighbor);
					openSetMap.put(neighbor.position, neighbor);
				}

			}
		}
		/*
		 * E.OSORIO
		 * 
		 * Si llega aquí, quiere decir que no hubo solución
		 */
		return new ArrayList<>();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// este método");
	}

	// Clase auxiliar para nodos del algoritmo A*
	private static class AStarNode {
		public final HexPosition position;
		public final double gScore; // Costo desde inicio
		public final double fScore; // gScore + heurística
		public final AStarNode parent;

		public AStarNode(HexPosition position, double gScore, double fScore, AStarNode parent) {
			this.position = position;
			this.gScore = gScore;
			this.fScore = fScore;
			this.parent = parent;
		}

	}

	// Método auxiliar para reconstruir el camino
	private List<HexPosition> reconstructPath(AStarNode goalNode) {
		// TODO: Reconstruir camino desde nodo objetivo hasta inicio
		// Seguir la cadena de padres hasta llegar al inicio
		// Retornar lista en orden correcto (desde inicio hasta objetivo)

		/*
		 * E.OSORIO
		 * 
		 * Se hace el while, miestras el nodo actual tenga padre. Se asegura finalmente
		 * agregar el nodo que no tiene padre.
		 */
		AStarNode actualNode = goalNode;
		List<HexPosition> nodes = new LinkedList<>();
		while (actualNode.parent != null) {
			nodes.add(actualNode.position);
			actualNode = actualNode.parent;
		}
		nodes.add(actualNode.position);
		Collections.reverse(nodes);
		return nodes;

		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	// Hook methods - los estudiantes pueden override para debugging
	@Override
	protected void beforeMovementCalculation(HexPosition currentPosition) {
		// TODO: Opcional - logging, métricas, etc.
		super.beforeMovementCalculation(currentPosition);
	}

	@Override
	protected void afterMovementCalculation(Optional<HexPosition> selectedMove) {
		// TODO: Opcional - logging, métricas, etc.
		super.afterMovementCalculation(selectedMove);
	}
}
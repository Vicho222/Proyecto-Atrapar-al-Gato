package com.atraparalagato.example.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato basada en BFS.
 * Busca siempre el camino más corto al borde del tablero.
 */
public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    public BFSCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }

    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        return board.getAdjacentPositions(currentPosition).stream()
                .filter(pos -> !board.isBlocked(pos))
                .toList();
    }

    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves,
                                                   HexPosition currentPosition,
                                                   HexPosition targetPosition) {
        List<HexPosition> path = findShortestPathToBorder(currentPosition);
        return (path.size() >= 2) ? Optional.of(path.get(1)) : Optional.empty();
    }

    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        return pos -> 0.0;
    }

    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        int size = board.getSize();
        return p -> Math.abs(p.getQ()) >= size ||
                    Math.abs(p.getR()) >= size ||
                    Math.abs(p.getS()) >= size;
    }

    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        return 1.0;
    }

    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        return !findShortestPathToBorder(currentPosition).isEmpty();
    }

    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        return findShortestPathToBorder(currentPosition);
    }

    /* ---------------- Métodos auxiliares ---------------- */

    private List<HexPosition> findShortestPathToBorder(HexPosition start) {
        Queue<HexPosition> queue = new LinkedList<>();
        Map<HexPosition, HexPosition> parent = new HashMap<>();
        Set<HexPosition> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            HexPosition current = queue.poll();

            if (getGoalPredicate().test(current)) {
                return reconstructPath(parent, current);
            }

            for (HexPosition neighbor : board.getAdjacentPositions(current)) {
                if (!visited.contains(neighbor) && !board.isBlocked(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<HexPosition> reconstructPath(Map<HexPosition, HexPosition> parent,
                                              HexPosition goal) {
        List<HexPosition> path = new ArrayList<>();
        HexPosition cur = goal;
        while (cur != null) {
            path.add(cur);
            cur = parent.get(cur);
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    protected void beforeMovementCalculation(HexPosition currentPosition) {
        System.out.println("Calculando movimiento del gato desde: " + currentPosition);
    }

    @Override
    protected void afterMovementCalculation(Optional<HexPosition> selectedMove) {
        System.out.println(selectedMove.map(p -> "Gato se mueve a: " + p)
                                       .orElse("¡Gato no puede moverse! Está atrapado."));
    }
}

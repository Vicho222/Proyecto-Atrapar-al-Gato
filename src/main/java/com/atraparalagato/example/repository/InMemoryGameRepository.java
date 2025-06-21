package com.atraparalagato.example.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.example.model.ExampleGameState;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación de ejemplo de DataRepository usando almacenamiento en memoria.
 * 
 * NOTA PARA ESTUDIANTES:
 * Esta es una implementación BÁSICA de ejemplo. Los estudiantes deben:
 * 1. Estudiar esta implementación para entender los conceptos
 * 2. Crear implementaciones con bases de datos reales (H2, MongoDB, etc.)
 * 3. Implementar manejo de transacciones más sofisticado
 * 4. Agregar validaciones y manejo de errores más robusto
 * 
 * Esta implementación usa ConcurrentHashMap para thread-safety básico.
 */
public class InMemoryGameRepository extends DataRepository<ExampleGameState, String> {
    
    // Almacenamiento en memoria thread-safe
    private final Map<String, ExampleGameState> storage = new ConcurrentHashMap<>();
    
    @Override
    public ExampleGameState save(ExampleGameState entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        
        beforeSave(entity);
        
        // Guardar en el mapa usando el gameId como clave
        storage.put(entity.getGameId(), entity);
        
        afterSave(entity);
        
        return entity;
    }
    
    @Override
    public Optional<ExampleGameState> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public List<ExampleGameState> findAll() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public List<ExampleGameState> findWhere(Predicate<ExampleGameState> condition) {
        return storage.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }
    
    @Override
    public <R> List<R> findAndTransform(Predicate<ExampleGameState> condition, 
                                       Function<ExampleGameState, R> transformer) {
        return storage.values().stream()
                .filter(condition)
                .map(transformer)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countWhere(Predicate<ExampleGameState> condition) {
        return storage.values().stream()
                .filter(condition)
                .count();
    }
    
    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        
        return storage.remove(id) != null;
    }
    
    @Override
    public long deleteWhere(Predicate<ExampleGameState> condition) {
        List<String> toDelete = storage.values().stream()
                .filter(condition)
                .map(ExampleGameState::getGameId)
                .collect(Collectors.toList());
        
        long deletedCount = 0;
        for (String id : toDelete) {
            if (storage.remove(id) != null) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    @Override
    public boolean existsById(String id) {
        return id != null && storage.containsKey(id);
    }
    
    @Override
    public <R> R executeInTransaction(Function<DataRepository<ExampleGameState, String>, R> operation) {
        // Implementación básica sin transacciones reales
        // En una implementación real, aquí se manejarían transacciones de BD
        
        try {
            return operation.apply(this);
        } catch (Exception e) {
            // En una implementación real, aquí se haría rollback
            System.err.println("Error en transacción: " + e.getMessage());
            throw new RuntimeException("Transaction failed", e);
        }
    }
    
    @Override
    public List<ExampleGameState> findWithPagination(int page, int size) {
        if (page < 0 || size <= 0) {
            return Collections.emptyList();
        }
        
        List<ExampleGameState> allGames = findAll();
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allGames.size());
        
        if (startIndex >= allGames.size()) {
            return Collections.emptyList();
        }
        
        return allGames.subList(startIndex, endIndex);
    }
    
    @Override
    public List<ExampleGameState> findAllSorted(Function<ExampleGameState, ? extends Comparable<?>> sortKeyExtractor, 
                                               boolean ascending) {
        @SuppressWarnings("unchecked")
        Comparator<ExampleGameState> comparator = (Comparator<ExampleGameState>) Comparator.comparing(
            (Function<ExampleGameState, Comparable<Object>>) sortKeyExtractor
        );
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        return storage.values().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
    
    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        // Implementación básica que simula consultas personalizadas
        // En una implementación real, aquí se ejecutarían consultas SQL/NoSQL
        
        System.out.println("Ejecutando consulta personalizada: " + query);
        
        // Ejemplo: consulta para obtener juegos terminados
        if ("finished_games".equals(query)) {
            return storage.values().stream()
                    .filter(ExampleGameState::isGameFinished)
                    .map(game -> resultMapper.apply(game))
                    .collect(Collectors.toList());
        }
        
        // Ejemplo: consulta para obtener juegos ganados
        if ("won_games".equals(query)) {
            return storage.values().stream()
                    .filter(ExampleGameState::hasPlayerWon)
                    .map(game -> resultMapper.apply(game))
                    .collect(Collectors.toList());
        }
        
        return Collections.emptyList();
    }
    
    @Override
    protected void initialize() {
        // Inicialización básica - limpiar storage si es necesario
        System.out.println("Inicializando repositorio en memoria...");
        // En una implementación real, aquí se configurarían conexiones de BD
    }
    
    @Override
    protected void cleanup() {
        // Limpieza básica
        System.out.println("Limpiando repositorio en memoria...");
        storage.clear();
        // En una implementación real, aquí se cerrarían conexiones de BD
    }
    
    @Override
    protected boolean validateEntity(ExampleGameState entity) {
        // Validaciones básicas
        return entity != null && 
               entity.getGameId() != null && 
               !entity.getGameId().trim().isEmpty();
    }
    
    @Override
    protected void beforeSave(ExampleGameState entity) {
        // Hook para validaciones adicionales antes de guardar
        if (!validateEntity(entity)) {
            throw new IllegalArgumentException("Invalid game state entity");
        }
        
        System.out.println("Guardando juego: " + entity.getGameId());
    }
    
    @Override
    protected void afterSave(ExampleGameState entity) {
        // Hook para acciones después de guardar
        System.out.println("Juego guardado exitosamente: " + entity.getGameId());
    }
    
    // Métodos adicionales específicos para este repositorio
    
    /**
     * Obtiene estadísticas del repositorio.
     */
    public Map<String, Object> getRepositoryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalGames = storage.size();
        long finishedGames = countWhere(ExampleGameState::isGameFinished);
        long wonGames = countWhere(ExampleGameState::hasPlayerWon);
        
        stats.put("totalGames", totalGames);
        stats.put("finishedGames", finishedGames);
        stats.put("wonGames", wonGames);
        stats.put("inProgressGames", totalGames - finishedGames);
        stats.put("winRate", totalGames > 0 ? (double) wonGames / totalGames * 100 : 0);
        
        return stats;
    }
    
    /**
     * Limpia juegos antiguos (ejemplo de operación de mantenimiento).
     */
    public long cleanupOldGames(long maxAgeMillis) {
        long currentTime = System.currentTimeMillis();
        
        return deleteWhere(game -> {
            long gameTime = game.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return currentTime - gameTime > maxAgeMillis;
        });
    }
} 
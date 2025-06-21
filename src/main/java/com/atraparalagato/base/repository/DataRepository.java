package com.atraparalagato.base.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Repositorio abstracto para persistencia de datos.
 * 
 * Design Patterns:
 * - Repository Pattern: Abstrae la capa de persistencia
 * - Strategy Pattern: Permite diferentes implementaciones de BD
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo maneja persistencia de datos
 * - Dependency Inversion: Depende de abstracciones, no implementaciones
 * - Interface Segregation: Métodos específicos para cada tipo de operación
 * 
 * Conceptos a implementar:
 * - Programación Funcional: Function, Predicate para consultas
 * - Modularización: Separación de lógica de negocio y persistencia
 * - Abstracción: Independencia del tipo de base de datos
 */
public abstract class DataRepository<T, ID> {
    
    /**
     * Guarda una entidad en el repositorio.
     * Debe manejar tanto creación como actualización.
     */
    public abstract T save(T entity);
    
    /**
     * Busca una entidad por su identificador.
     * Retorna Optional para manejar casos donde no existe.
     */
    public abstract Optional<T> findById(ID id);
    
    /**
     * Obtiene todas las entidades del repositorio.
     * Los estudiantes deben considerar paginación para grandes datasets.
     */
    public abstract List<T> findAll();
    
    /**
     * Busca entidades que cumplan con un predicado.
     * Ejemplo de programación funcional aplicada a consultas.
     */
    public abstract List<T> findWhere(Predicate<T> condition);
    
    /**
     * Busca entidades y las transforma usando una función.
     * Combina consulta y transformación en una operación.
     */
    public abstract <R> List<R> findAndTransform(Predicate<T> condition, 
                                                Function<T, R> transformer);
    
    /**
     * Cuenta las entidades que cumplen con una condición.
     * Útil para estadísticas sin cargar todas las entidades.
     */
    public abstract long countWhere(Predicate<T> condition);
    
    /**
     * Elimina una entidad por su identificador.
     * Retorna true si se eliminó, false si no existía.
     */
    public abstract boolean deleteById(ID id);
    
    /**
     * Elimina entidades que cumplan con una condición.
     * Retorna el número de entidades eliminadas.
     */
    public abstract long deleteWhere(Predicate<T> condition);
    
    /**
     * Verifica si existe una entidad con el identificador dado.
     * Más eficiente que findById cuando solo necesitas verificar existencia.
     */
    public abstract boolean existsById(ID id);
    
    /**
     * Ejecuta una operación en transacción.
     * Los estudiantes deben implementar manejo de transacciones.
     */
    public abstract <R> R executeInTransaction(Function<DataRepository<T, ID>, R> operation);
    
    /**
     * Obtiene entidades con paginación.
     * Fundamental para manejar grandes volúmenes de datos.
     */
    public abstract List<T> findWithPagination(int page, int size);
    
    /**
     * Busca entidades ordenadas según un criterio.
     * Los estudiantes definen cómo implementar el ordenamiento.
     */
    public abstract List<T> findAllSorted(Function<T, ? extends Comparable<?>> sortKeyExtractor, 
                                         boolean ascending);
    
    /**
     * Realiza una consulta personalizada.
     * Permite a los estudiantes implementar consultas específicas de su BD.
     */
    public abstract <R> List<R> executeCustomQuery(String query, 
                                                  Function<Object, R> resultMapper);
    
    /**
     * Inicializa el repositorio.
     * Los estudiantes configuran conexiones, esquemas, etc.
     */
    protected abstract void initialize();
    
    /**
     * Limpia recursos del repositorio.
     * Importante para manejo adecuado de conexiones.
     */
    protected abstract void cleanup();
    
    /**
     * Valida una entidad antes de persistirla.
     * Hook method para validaciones específicas.
     */
    protected boolean validateEntity(T entity) {
        return entity != null;
    }
    
    /**
     * Se ejecuta antes de guardar una entidad.
     * Permite transformaciones o validaciones adicionales.
     */
    protected void beforeSave(T entity) {
        // Default: no operation
    }
    
    /**
     * Se ejecuta después de guardar una entidad.
     * Útil para logging, notificaciones, etc.
     */
    protected void afterSave(T entity) {
        // Default: no operation
    }
} 
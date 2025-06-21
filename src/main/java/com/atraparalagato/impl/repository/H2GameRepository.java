package com.atraparalagato.impl.repository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.JsonUtils;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

/**
 * Implementación esqueleto de DataRepository usando base de datos H2.
 * 
 * Los estudiantes deben completar los métodos marcados con TODO.
 * 
 * Conceptos a implementar: - Conexión a base de datos H2 - Operaciones CRUD con
 * SQL - Manejo de transacciones - Mapeo objeto-relacional - Consultas
 * personalizadas - Manejo de errores de BD
 */
public class H2GameRepository extends DataRepository<GameState<HexPosition>, String> {

	// TODO: Los estudiantes deben definir la configuración de la base de datos
	// Ejemplos: DataSource, JdbcTemplate, EntityManager, etc.

	private final JdbcTemplate jdbcTemplate;

	public H2GameRepository(JdbcTemplate jdbcTemplate) {
		// TODO: Inicializar conexión a H2 y crear tablas si no existen
		// Pista: Usar spring.datasource.url configurado en application.properties
		this.jdbcTemplate = jdbcTemplate;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar el
		// constructor");
	}

	@Override
	public HexGameState save(GameState<HexPosition> entity) {
		// TODO: Implementar guardado en base de datos H2
		// Considerar:
		// 1. Validar entidad antes de guardar
		// 2. Usar INSERT o UPDATE según si existe
		// 3. Serializar el estado del juego
		// 4. Manejar errores de BD
		// 5. Llamar hooks beforeSave/afterSave

		HexGameState hexEntity = (HexGameState) entity;

		String sql = "MERGE INTO gamesStates (id, state) VALUES (?, ?)";
		jdbcTemplate.update(sql, hexEntity.getGameId(), serializeGameState(hexEntity));
		return hexEntity;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// save");
	}

	@Override
	public Optional<GameState<HexPosition>> findById(String id) {
		// TODO: Buscar juego por ID en la base de datos
		// 1. Ejecutar consulta SQL SELECT
		// 2. Mapear resultado a HexGameState
		// 3. Deserializar estado del juego
		// 4. Retornar Optional.empty() si no existe

		String sql = "SELECT data FROM gamesStates WHERE id = ?";

		try {
			String jsonData = jdbcTemplate.queryForObject(sql, new Object[] { id }, String.class);
			HexGameState gameState = deserializeGameState(jsonData, id);
			return Optional.of(gameState);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		} catch (Exception e) {
			throw new RuntimeException("Error al buscar juego por ID: " + id, e);
		}
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// findById");
	}

	@Override
	public List<GameState<HexPosition>> findAll() {
		// TODO: Obtener todos los juegos de la base de datos
		// Considerar paginación para grandes volúmenes de datos

		String sql = "SELECT data FROM gamesStates";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			String id = rs.getString("id");
			String jsonData = rs.getString("data");
			return deserializeGameState(jsonData, id);
		});

		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// findAll");
	}

	@Override
	public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> condition) {
		// TODO: Implementar búsqueda con condiciones
		// Opciones:
		// 1. Cargar todos y filtrar en memoria (simple pero ineficiente)
		// 2. Convertir Predicate a SQL WHERE (avanzado)
		// 3. Usar consultas predefinidas para casos comunes

		/*
		 * E.OSORIO
		 * 
		 * Dado que los datos se alamacenan como un JSON en la BD, no se puede hacer
		 * consultas específicas en la misma base de datos.
		 * 
		 * Toca traer todos los registros y filtrar usando el predicado.
		 */
		List<GameState<HexPosition>> records = findAll();
		return records.stream().filter(r -> condition.test(r)).toList();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// findWhere");
	}

	@Override
	public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> condition,
			Function<GameState<HexPosition>, R> transformer) {
		// TODO: Buscar y transformar en una operación
		// Puede optimizarse para hacer la transformación en SQL

		/*
		 * E.OSORIO
		 * 
		 * Dado que no se pueden hacer consultas específicas en la BD, se aplica el
		 * findWhere y luego se transforma.
		 */
		return findWhere(condition).stream().map(r -> transformer.apply(r)).toList();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// findAndTransform");
	}

	@Override
	public long countWhere(Predicate<GameState<HexPosition>> condition) {
		// TODO: Contar registros que cumplen condición
		// Preferiblemente usar COUNT(*) en SQL para eficiencia
		
		return findAll().stream().filter(r -> condition.test(r)).count();
		//throw new UnsupportedOperationException("Los estudiantes deben implementar countWhere");
	}

	@Override
	public boolean deleteById(String id) {
		// TODO: Eliminar juego por ID
		// Retornar true si se eliminó, false si no existía

		String sql = "DELETE FROM gamesStates WHERE id = ?";
		int rowsAffected = jdbcTemplate.update(sql, id);
		return rowsAffected > 0;
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// deleteById");
	}

	@Override
	public long deleteWhere(Predicate<GameState<HexPosition>> condition) {
		// TODO: Eliminar múltiples registros según condición
		// Retornar número de registros eliminados
		List<GameState<HexPosition>> toDelete = findWhere(condition);
		long registersToDelete = toDelete != null ? toDelete.size() : 0;
		Iterator<GameState<HexPosition>> iterator = toDelete.iterator();
		while(iterator.hasNext())
		{
			GameState<HexPosition> item = iterator.next();
			deleteById(item.getGameId());
		}
		return registersToDelete;
		//throw new UnsupportedOperationException("Los estudiantes deben implementar deleteWhere");
	}

	@Override
	public boolean existsById(String id) {
		// TODO: Verificar si existe un juego con el ID dado
		// Usar SELECT COUNT(*) para eficiencia
		throw new UnsupportedOperationException("Los estudiantes deben implementar existsById");
	}

	@Override
	public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> operation) {
		// TODO: Ejecutar operación en transacción
		// 1. Iniciar transacción
		// 2. Ejecutar operación
		// 3. Commit si exitoso, rollback si error
		// 4. Manejar excepciones apropiadamente
		throw new UnsupportedOperationException("Los estudiantes deben implementar executeInTransaction");
	}

	@Override
	public List<GameState<HexPosition>> findWithPagination(int page, int size) {
		// TODO: Implementar paginación con LIMIT y OFFSET
		// Validar parámetros de entrada
		throw new UnsupportedOperationException("Los estudiantes deben implementar findWithPagination");
	}

	@Override
	public List<GameState<HexPosition>> findAllSorted(
			Function<GameState<HexPosition>, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
		// TODO: Implementar ordenamiento
		// Convertir sortKeyExtractor a ORDER BY SQL
		throw new UnsupportedOperationException("Los estudiantes deben implementar findAllSorted");
	}

	@Override
	public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
		// TODO: Ejecutar consulta SQL personalizada
		// 1. Validar consulta SQL
		// 2. Ejecutar consulta
		// 3. Mapear resultados usando resultMapper
		// 4. Manejar errores SQL
		throw new UnsupportedOperationException("Los estudiantes deben implementar executeCustomQuery");
	}

	@Override
	protected void initialize() {
		// TODO: Inicializar base de datos
		// 1. Crear tablas si no existen
		// 2. Configurar índices
		// 3. Insertar datos de prueba si es necesario

		// 1.0
		this.createSchema();
		// throw new UnsupportedOperationException("Los estudiantes deben implementar
		// initialize");
	}

	@Override
	protected void cleanup() {
		// TODO: Limpiar recursos
		// 1. Cerrar conexiones
		// 2. Limpiar cache si existe
		// 3. Liberar recursos
		throw new UnsupportedOperationException("Los estudiantes deben implementar cleanup");
	}

	// Métodos auxiliares que los estudiantes pueden implementar

	/**
	 * TODO: Crear el esquema de la base de datos. Definir tablas, columnas, tipos
	 * de datos, restricciones.
	 */
	private void createSchema() {
		jdbcTemplate.execute("""
				    CREATE TABLE IF NOT EXISTS gamesStates (
				        id VARCHAR(255) PRIMARY KEY,
				        state TEXT NOT NULL
				    )
				""");
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Serializar HexGameState a formato de BD. Puede usar JSON, XML, o campos
	 * separados.
	 */
	private String serializeGameState(HexGameState gameState) {
		return (String) gameState.getSerializableState();
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Deserializar desde formato de BD a HexGameState. Debe ser compatible
	 * con serializeGameState.
	 */
	private HexGameState deserializeGameState(String serializedData, String gameId) {
		HexGameState state = JsonUtils.fromJson(serializedData, HexGameState.class);
		return state != null && state.getGameId().equals(gameId) ? state : null;
		// throw new UnsupportedOperationException("Método auxiliar para implementar");
	}

	/**
	 * TODO: Convertir Predicate a cláusula WHERE SQL. Implementación avanzada
	 * opcional.
	 */
	private String predicateToSql(Predicate<HexGameState> predicate) {
		throw new UnsupportedOperationException("Método auxiliar avanzado para implementar");
	}
}
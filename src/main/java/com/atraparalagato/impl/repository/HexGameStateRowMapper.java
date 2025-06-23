package com.atraparalagato.impl.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.model.GameState.GameStatus;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexGameState.LEVEL_OF_DIFFICULTY;
import com.atraparalagato.impl.model.HexPosition;

public  class HexGameStateRowMapper implements RowMapper<GameState<HexPosition>> {
    @Override
    public GameState<HexPosition> mapRow(ResultSet rs, int rowNum) throws SQLException {
		HexPosition catPosition = new HexPosition(rs.getInt("CAT_POSITION_Q"), rs.getInt("CAT_POSITION_R"));

		String gameId = rs.getString("ID");
		int boardSize = rs.getInt("BOARD_SIZE");
		int invalidMovementes = rs.getInt("INVALID_MOVEMENTS");
		int maxMovements = rs.getInt("MAX_MOVEMENTES");
		int moveCount = rs.getInt("MOVE_COUNT");
		GameStatus gameStatus =  GameStatus.valueOf(rs.getString("STATUS"));
		Timestamp createdAt = rs.getTimestamp("CREATED_AT");
		String player = rs.getString("PLAYER");
		Timestamp finishedAt = rs.getTimestamp("FINISHED_AT");
		Timestamp pausedAt = rs.getTimestamp("PAUSED_AT");
		LEVEL_OF_DIFFICULTY levlOfDifficulty = LEVEL_OF_DIFFICULTY.valueOf(rs.getString("LEVEL_OF_DIFFICULTY"));
		
		/*
		 * El estado se calcula con los datos
		 */
		// GameStatus status = GameStatus.valueOf(rs.getString("STATUS"));
		
		/*
		 * La fecha se crea cuando se instancia el HexGameState 
		 */
		// LocalDateTime date = rs.getTimestamp("CREATED_AT").toLocalDateTime();

		HexGameState gameState = new HexGameState(gameId, boardSize, maxMovements);
		gameState.setCatPosition(catPosition);
		gameState.setInvalidMovements(invalidMovementes);
		gameState.setMoveCount(moveCount);
		return gameState;
    }
}

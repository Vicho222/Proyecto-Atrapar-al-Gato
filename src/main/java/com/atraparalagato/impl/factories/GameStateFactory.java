package com.atraparalagato.impl.factories;

import org.springframework.stereotype.Component;

import com.atraparalagato.impl.model.HexGameState;

@Component
public class GameStateFactory {

	public HexGameState create(String gameId, int boardSize) {
		return new HexGameState(gameId, boardSize);
	}
	
	public HexGameState create(String gameId, int boardSize, int maxMovements) {
		return new HexGameState(gameId, boardSize, maxMovements);
	}
}

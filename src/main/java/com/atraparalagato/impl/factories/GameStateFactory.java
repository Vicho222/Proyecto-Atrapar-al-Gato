package com.atraparalagato.impl.factories;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;

@Component
public class GameStateFactory  implements Function<String, GameState<HexPosition>> {

	@Override
	public GameState<HexPosition> apply(String gameId) {
		return new HexGameState(gameId);
	}
}

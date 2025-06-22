package com.atraparalagato.impl.factories;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexPosition;

@Component
public class HexBoardFactory implements Function<Integer, GameBoard<HexPosition>>{

	@Override
	public GameBoard<HexPosition> apply(Integer size) {
		// TODO Auto-generated method stub
		return new HexGameBoard(size);
	}
    
}

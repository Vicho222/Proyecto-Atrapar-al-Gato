package com.atraparalagato.impl.factories;

import org.springframework.stereotype.Component;

import com.atraparalagato.impl.model.HexGameBoard;

@Component
public class HexBoardFactory {
    public HexGameBoard create(int size) {
        return new HexGameBoard(size);
    }
    
}

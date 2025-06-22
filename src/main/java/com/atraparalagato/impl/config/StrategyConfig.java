package com.atraparalagato.impl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.factories.HexBoardFactory;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.strategy.AStarCatMovement;
import com.atraparalagato.impl.strategy.BFSCatMovement;

@Configuration
public class StrategyConfig {

    @Value("${game.strategy}")
    private String strategyName;

    @Bean
    public CatMovementStrategy<HexPosition> catMovementStrategy(HexBoardFactory factory) {
        GameBoard<HexPosition> board = factory.apply(11);

        return switch (strategyName) {
            case "aStar" -> new AStarCatMovement(board);
            case "bfs" -> new BFSCatMovement(board);
            default -> throw new IllegalArgumentException("Estrategia desconocida: " + strategyName);
        };
    }
}

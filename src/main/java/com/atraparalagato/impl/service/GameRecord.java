package com.atraparalagato.impl.service;

import java.time.LocalDateTime;

/**
 * Clase utilitaria para poder obtener solo los atributos necesarios al momento
 * de obtener las estadísticas del usuario.
 */
public class GameRecord {
	private String id;
	private String status; // "GANADOR" , "PERDEDO", "EMPATE".
	private int moveCount;
	private int points;
	private LocalDateTime createdAt;
	private LocalDateTime finishedAt;
	private String playerId;

	public GameRecord(String id, String playerId, String status, int moveCount, int points, LocalDateTime createdAt, LocalDateTime finishedAt) {
		this.id = id;
		this.setPlayerId(playerId);
		this.status = status;
		this.moveCount = moveCount;
		this.points = points;
		this.createdAt = createdAt;
		this.finishedAt =  finishedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

}

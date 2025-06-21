package com.atraparalagato.impl.factories;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class GameIdGenerator {

	public String generateId() {
		return UUID.randomUUID().toString();
	}

}

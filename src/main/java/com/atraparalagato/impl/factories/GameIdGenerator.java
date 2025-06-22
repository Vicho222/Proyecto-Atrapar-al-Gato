package com.atraparalagato.impl.factories;

import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

@Component
public class GameIdGenerator  implements Supplier<String>{


	@Override
	public String get() {
		return UUID.randomUUID().toString();
	}

}

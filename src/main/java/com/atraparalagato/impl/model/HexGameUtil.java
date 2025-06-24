package com.atraparalagato.impl.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilitario de transformación de la clase HexPisition a String y vice versa.
 */
public final class HexGameUtil {

	private HexGameUtil() {

	}

	public static String serializeHexPositions(Set<HexPosition> positions) {
		if (positions == null || positions.isEmpty())
			return "";

		return positions.stream().map(p -> String.format("%d,%d,%d", p.getQ(), p.getR(), p.getS()))
				.collect(Collectors.joining(";"));
	}

	public static Set<HexPosition> deserializeHexPositions(String text) {
		if (text == null || text.isBlank())
			return new HashSet<>();

		return Arrays.stream(text.split(";")).map(entry -> {
			String[] parts = entry.split(",");
			int q = Integer.parseInt(parts[0]);
			int r = Integer.parseInt(parts[1]);
			return new HexPosition(q, r);
		}).collect(Collectors.toSet());
	}
}

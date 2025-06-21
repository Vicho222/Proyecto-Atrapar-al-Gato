package com.atraparalagato.impl.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ejemplo de tests para HexPosition.
 * Los estudiantes deben crear tests similares para sus implementaciones.
 * 
 * Conceptos de testing:
 * - Casos normales y casos límite
 * - Validación de contratos de métodos
 * - Tests de inmutabilidad
 */
class HexPositionTest {
    
    private HexPosition origin;
    private HexPosition adjacent;
    private HexPosition distant;
    
    @BeforeEach
    void setUp() {
        origin = new HexPosition(0, 0);
        adjacent = new HexPosition(1, 0);
        distant = new HexPosition(3, 2);
    }
    
    @Test
    void testConstructorAndGetters() {
        // Test: Constructor y getters básicos
        HexPosition pos = new HexPosition(2, -1);
        
        assertEquals(2, pos.getQ());
        assertEquals(-1, pos.getR());
        assertEquals(-1, pos.getS()); // s = -q - r = -2 - (-1) = -1
    }
    
    @Test
    void testAxialCoordinateConstraint() {
        // Test: Verificar que q + r + s = 0 siempre
        HexPosition pos = new HexPosition(3, -2);
        
        assertEquals(0, pos.getQ() + pos.getR() + pos.getS());
    }
    
    @Test
    void testDistanceCalculation() {
        // Test: Distancia hexagonal correcta
        assertEquals(0.0, origin.distanceTo(origin));
        assertEquals(1.0, origin.distanceTo(adjacent));
        // Corrección: distancia hexagonal = (|q1-q2| + |q1+r1-q2-r2| + |r1-r2|) / 2
        // = (|0-3| + |0+0-3-2| + |0-2|) / 2 = (3 + 5 + 2) / 2 = 5
        assertEquals(5.0, origin.distanceTo(distant));
    }
    
    @Test
    void testDistanceSymmetry() {
        // Test: Distancia debe ser simétrica
        assertEquals(adjacent.distanceTo(origin), origin.distanceTo(adjacent));
        assertEquals(distant.distanceTo(origin), origin.distanceTo(distant));
    }
    
    @Test
    void testAddition() {
        // Test: Suma de posiciones
        HexPosition result = (HexPosition) origin.add(adjacent);
        
        assertEquals(1, result.getQ());
        assertEquals(0, result.getR());
        assertEquals(-1, result.getS());
    }
    
    @Test
    void testSubtraction() {
        // Test: Resta de posiciones
        HexPosition result = (HexPosition) adjacent.subtract(origin);
        
        assertEquals(1, result.getQ());
        assertEquals(0, result.getR());
        assertEquals(-1, result.getS());
    }
    
    @Test
    void testImmutability() {
        // Test: Las operaciones no modifican el objeto original
        HexPosition originalQ = new HexPosition(origin.getQ(), origin.getR());
        
        origin.add(adjacent);
        
        assertEquals(originalQ.getQ(), origin.getQ());
        assertEquals(originalQ.getR(), origin.getR());
    }
    
    @Test
    void testAdjacency() {
        // Test: Verificar adyacencia correcta
        assertTrue(origin.isAdjacentTo(adjacent));
        assertFalse(origin.isAdjacentTo(distant));
        assertFalse(origin.isAdjacentTo(origin)); // Una posición no es adyacente a sí misma
    }
    
    @Test
    void testWithinBounds() {
        // Test: Verificar límites del tablero
        assertTrue(origin.isWithinBounds(5));
        assertTrue(adjacent.isWithinBounds(5));
        assertTrue(distant.isWithinBounds(5));
        
        HexPosition outOfBounds = new HexPosition(6, 0);
        assertFalse(outOfBounds.isWithinBounds(5));
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Test: Contrato equals/hashCode
        HexPosition same = new HexPosition(0, 0);
        HexPosition different = new HexPosition(1, 0);
        
        // Reflexividad
        assertEquals(origin, origin);
        
        // Simetría
        assertEquals(origin, same);
        assertEquals(same, origin);
        
        // Consistencia
        assertEquals(origin.hashCode(), same.hashCode());
        
        // Diferencia
        assertNotEquals(origin, different);
        assertNotEquals(origin, null);
        assertNotEquals(origin, "not a position");
    }
    
    @Test
    void testToString() {
        // Test: Representación en string
        String str = origin.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("0")); // Debe contener las coordenadas
        assertTrue(str.contains("HexPosition")); // Debe identificar el tipo
    }
    
    @Test
    void testInvalidOperations() {
        // Test: Operaciones con tipos incompatibles
        // Nota: Este test asume que HexPosition valida tipos
        // Los estudiantes pueden decidir cómo manejar esto
        
        // Si implementan validación de tipos:
        // assertThrows(IllegalArgumentException.class, () -> {
        //     origin.distanceTo(new CartesianPosition(1, 1));
        // });
    }
} 
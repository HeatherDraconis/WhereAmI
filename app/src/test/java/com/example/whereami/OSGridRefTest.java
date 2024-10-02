package com.example.whereami;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OSGridRefTest {
    private static final double phi = Math.toRadians(((27.2531 / 60) + 39) / 60 + 52);
    private static final double lambda = Math.toRadians(((4.5177 / 60) + 43) / 60 + 1);

    @Test
    public void testNu() {
        assertEquals(6332382.44116279, OSGridRef.getNu(phi));
    }
    @Test
    public void testRho() {
        assertEquals(6289916.221266916, OSGridRef.getRho(phi));
    }
    @Test
    public void testEta() {
        assertEquals(0.006751476236247944, OSGridRef.getEtaSquared(phi));
    }
    @Test
    public void testM() {
        assertEquals(406688.29594722425, OSGridRef.getM(phi));
    }
    @Test
    public void testI() {
        assertEquals(306688.29594722425, OSGridRef.getI(phi));
    }
    @Test
    public void testII() {
        assertEquals(1526876.173443704, OSGridRef.getII(phi));
    }
    @Test
    public void testIII() {
        assertEquals(156501.3999566287, OSGridRef.getIII(phi));
    }
    @Test
    public void testIIIA() {
        assertEquals(-20489.53722874393, OSGridRef.getIIIA(phi));
    }
    @Test
    public void testIV() {
        assertEquals(3841079.5215343274, OSGridRef.getIV(phi));
    }
    @Test
    public void testV() {
        assertEquals(-167499.1179249739, OSGridRef.getV(phi));
    }
    @Test
    public void testVI() {
        assertEquals(-102042.930553659, OSGridRef.getVI(phi));
    }
    @Test
    public void testE() {
        assertEquals(649201.6626419136, OSGridRef.getE(phi, lambda));
    }
    @Test
    public void testN() {
        assertEquals(313120.29981839104, OSGridRef.getN(phi, lambda));
    }
    @Test
    public void testGetGridRef() {
        assertEquals("TG41", OSGridRef.getGridRef(Math.toDegrees(phi), Math.toDegrees(lambda), 2));
        assertEquals("TG4913", OSGridRef.getGridRef(Math.toDegrees(phi), Math.toDegrees(lambda), 4));
        assertEquals("TG492131", OSGridRef.getGridRef(Math.toDegrees(phi), Math.toDegrees(lambda), 6));
        assertEquals("TG49201312", OSGridRef.getGridRef(Math.toDegrees(phi), Math.toDegrees(lambda), 8));
        assertEquals("TG4920113120", OSGridRef.getGridRef(Math.toDegrees(phi), Math.toDegrees(lambda), 10));
    }
    @Test
    public void testGetGridRefError() {
        assertEquals("Location Not Found", OSGridRef.getGridRef(37.4219983, -122.084, 8));
    }
    @Test
    public void testGetSixDigits() {
        assertEquals("123456", OSGridRef.getSixDigits("123456"));
        assertEquals("000001", OSGridRef.getSixDigits("1"));
    }

}
package com.example.whereami;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OSGridRefTest {
    private static final double phi = Math.toRadians(((27.2531 / 60) + 39) / 60 + 52);
    private static final double lambda = Math.toRadians(((4.5177 / 60) + 43) / 60 + 1);

    private static final double _4DP = 0.0001;

    @Test
    public void testNu() {
        assertEquals(6388502.3339, OSGridRef.getNu(phi), _4DP);
    }
    @Test
    public void testRho() {
        assertEquals(6372756.4398, OSGridRef.getRho(phi), _4DP);
    }
    @Test
    public void testEta() {
        assertEquals(0.0024708137334281677, OSGridRef.getEtaSquared(phi));
    }
    @Test
    public void testM() {
        assertEquals(406688.29595, OSGridRef.getM(phi), _4DP);
    }
    @Test
    public void testI() {
        assertEquals(306688.29595, OSGridRef.getI(phi), _4DP);
    }
    @Test
    public void testII() {
        assertEquals(1540407.9094, OSGridRef.getII(phi), _4DP);
    }
    @Test
    public void testIII() {
        assertEquals(156068.75430, OSGridRef.getIII(phi), _4DP);
    }
    @Test
    public void testIIIA() {
        assertEquals(-20671.123013, OSGridRef.getIIIA(phi), _4DP);
    }
    @Test
    public void testIV() {
        assertEquals(3875120.5752, OSGridRef.getIV(phi), _4DP);
    }
    @Test
    public void testV() {
        assertEquals(-170000.78207, OSGridRef.getV(phi), _4DP);
    }
    @Test
    public void testVI() {
        assertEquals(-101344.70437, OSGridRef.getVI(phi), _4DP);
    }
    @Test
    public void testE() {
        assertEquals(651409.9029631378, OSGridRef.getE(phi, lambda));
    }
    @Test
    public void testN() {
        assertEquals(313177.2703307828, OSGridRef.getN(phi, lambda));
    }
    @Test
    public void testGetGridRef2() {
        assertEquals("TG 5 1", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 2));
        assertEquals("TG 51 13", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 4));
        assertEquals("TG 514 131", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 6));
        assertEquals("TG 5140 1317", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 8));
        assertEquals("TG 51409 13177", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 10));
    }
    @Test
    public void testGetGridRefError() {
        assertEquals("Location Not Found", OSGridRef.getGridRef2(37.4219983, -122.084, 8));
    }
    @Test
    public void testGetSixDigits() {
        assertEquals("123456", OSGridRef.getSixDigits("123456"));
        assertEquals("000001", OSGridRef.getSixDigits("1"));
    }

    @Test
    public void testGetGridRef() {
        assertEquals("SJ 9046 8354", OSGridRef.getGridRef(53.348663, -2.1447546, 8));
        assertEquals("SJ 90461 83540", OSGridRef.getGridRef(53.348663, -2.1447546, 10));

        assertEquals("NN 3054 9929", OSGridRef.getGridRef(57.053429, -4.7955322, 8));
        assertEquals("NN 30542 99291", OSGridRef.getGridRef(57.053429, -4.7955322, 10));

        assertEquals("NC 3537 5514", OSGridRef.getGridRef(58.453481, -4.8229980, 8));
        assertEquals("NC 35374 55149", OSGridRef.getGridRef(58.453481, -4.8229980, 10));

        assertEquals("NY 2183 1038", OSGridRef.getGridRef(54.482805, -3.2080078, 8));
        assertEquals("NY 21832 10389", OSGridRef.getGridRef(54.482805, -3.2080078, 10));

        assertEquals("SH 6758 5262", OSGridRef.getGridRef(53.054422, -3.9770508, 8));
        assertEquals("SH 67586 52624", OSGridRef.getGridRef(53.054422, -3.9770508, 10));

        assertEquals("SN 2019 2450", OSGridRef.getGridRef(51.890054, -4.6142578, 8));
        assertEquals("SN 20196 24508", OSGridRef.getGridRef(51.890054, -4.6142578, 10));
    }

}
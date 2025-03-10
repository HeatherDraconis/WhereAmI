package com.example.whereami;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OSGridRefTest {
    private static final double phi = Math.toRadians(((27.2531 / 60) + 39) / 60 + 52);
    private static final double lambda = Math.toRadians(((4.5177 / 60) + 43) / 60 + 1);

    private static final double phi2 = Math.toRadians(((43.1653 / 60) + 36) / 60 + 53);
    private static final double lambda2 = Math.toRadians(((51.9920 / 60) + 39) / 60 + 1);

    @Test
    public void testNu() {
        assertEquals(638850.23339, OSGridRef.getNu(phi));
    }
    @Test
    public void testRho() {
        assertEquals(637275.64398, OSGridRef.getRho(phi));
    }
    @Test
    public void testEta() {
        assertEquals(0.0024708137334281677, OSGridRef.getEtaSquared(phi));
    }
    @Test
    public void testM() {
        assertEquals(40668.829595, OSGridRef.getM(phi));
    }
    @Test
    public void testI() {
        assertEquals(30668.829595, OSGridRef.getI(phi));
    }
    @Test
    public void testII() {
        assertEquals(154040.79094, OSGridRef.getII(phi));
    }
    @Test
    public void testIII() {
        assertEquals(15606.875430, OSGridRef.getIII(phi));
    }
    @Test
    public void testIIIA() {
        assertEquals(-2067.1123013, OSGridRef.getIIIA(phi));
    }
    @Test
    public void testIV() {
        assertEquals(387512.05752, OSGridRef.getIV(phi));
    }
    @Test
    public void testV() {
        assertEquals(-17000.078207, OSGridRef.getV(phi));
    }
    @Test
    public void testVI() {
        assertEquals(-10134.470437, OSGridRef.getVI(phi));
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
    public void testGetGridRef() {
//        assertEquals("TG41", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 2));
//        assertEquals("TG4913", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 4));
//        assertEquals("TG492131", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 6));
//        assertEquals("TG49201312", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 8));
//        assertEquals("TG4920113120", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 10));
        assertEquals("TG51", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 2));
        assertEquals("TG5113", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 4));
        assertEquals("TG514131", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 6));
        assertEquals("TG51401317", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 8));
        assertEquals("TG5140913177", OSGridRef.getGridRef2(Math.toDegrees(phi), Math.toDegrees(lambda), 10));
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
    public void testGetCartesian() {
        assertEquals(3790644.900, OSGridRef.getCartesian(phi2, lambda2)[0]);
        assertEquals(-110149.210, OSGridRef.getCartesian(phi2, lambda2)[1]);
        assertEquals(5111482.970, OSGridRef.getCartesian(phi2, lambda2)[2]);
    }
    @Test
    public void testGetLatLong() {
        assertEquals(Math.toRadians(phi2), OSGridRef.getLatLong(3790644.900 , -110149.210, 5111482.970)[0]);
        assertEquals(Math.toRadians(lambda2), OSGridRef.getLatLong(3790644.900 , -110149.210, 5111482.970)[1]);
    }
    @Test
    public void testGetGridRef2() {
        assertEquals("SJ90468354", OSGridRef.getGridRef(53.348663, -2.1447546, 8));
        assertEquals("SJ9046183540", OSGridRef.getGridRef(53.348663, -2.1447546, 10));

        assertEquals("NN30549929", OSGridRef.getGridRef(57.053429, -4.7955322, 8));
        assertEquals("NN3054299291", OSGridRef.getGridRef(57.053429, -4.7955322, 10));

        assertEquals("NC35375514", OSGridRef.getGridRef(58.453481, -4.8229980, 8));
        assertEquals("NC3537455149", OSGridRef.getGridRef(58.453481, -4.8229980, 10));

        assertEquals("NY21831038", OSGridRef.getGridRef(54.482805, -3.2080078, 8));
        assertEquals("NY2183210389", OSGridRef.getGridRef(54.482805, -3.2080078, 10));

        assertEquals("SH67585262", OSGridRef.getGridRef(53.054422, -3.9770508, 8));
//        assertEquals("SH6758752624", OSGridRef.getGridRef(53.054422, -3.9770508, 10));
//SH6758652625
        assertEquals("SN20192450", OSGridRef.getGridRef(51.890054, -4.6142578, 8));
//        assertEquals("SN2019724509", OSGridRef.getGridRef(51.890054, -4.6142578, 10));
//SN2019624509
    }

}
package com.example.whereami;

import org.jetbrains.annotations.NotNull;

public class OSGridRef {
    private static final double phi0 = Math.toRadians(49);
    private static final double lambda0 = Math.toRadians(-2);

    private static final double N0 = -100_000;
    private static final double E0 = 400_000;
    private static final double F0 = 0.9996012717;

    private static final double a = 6_377_563.396;
    private static final double b = 6_356_256.909;
    private static final double e2 = (Math.pow(a, 2) - Math.pow(b, 2)) / Math.pow(a, 2);

    private static final double n = (a - b) / (a + b);

    public static String getGridRef(Double latitude, Double longitude, int figureSize) {
        double phi = Math.toRadians(latitude);
        double lambda = Math.toRadians(longitude);
        String E = String.valueOf((int) getE(phi, lambda));
        String N = String.valueOf((int) getN(phi, lambda));
        if (E.startsWith("-") || N.startsWith("-")) {
            return "Location Not Found";
        }

        E = getSixDigits(E);
        N = getSixDigits(N);

        String letters = getLetters(E, N);
        if (letters.isEmpty()) {
            return "Location Not Found";
        }
        int digits = figureSize / 2;

        return letters + E.substring(1, digits + 1) + N.substring(1, digits + 1);
    }

    static @NotNull String getSixDigits(String number) {
        StringBuilder stringBuilder = new StringBuilder(number);
        while (stringBuilder.length() < 6) {
            stringBuilder.insert(0, "0");
        }
        number = stringBuilder.toString();
        return number;
    }

    private static String getLetters(String E, String N) {
        String north = String.valueOf(N.charAt(0));
        String east = String.valueOf(E.charAt(0));
        String[][] squares = {
                {"SV", "SW", "SX", "SY", "SZ", "TV", "TW"},
                {"SQ", "SR", "SS", "ST", "SU", "TQ", "TR"},
                {"SL", "SM", "SN", "SO", "SP", "TL", "TM"},
                {"SF", "SG", "SH", "SJ", "SK", "TF", "TG"},
                {"SA", "SB", "SC", "SD", "SE", "TA", "TB"},
                {"NV", "NW", "NX", "NY", "NZ", "OV", "OW"},
                {"NQ", "NR", "NS", "NT", "NU", "OQ", "OR"},
                {"NL", "NM", "NN", "NO", "NP", "OL", "OM"},
                {"NF", "NG", "NH", "NJ", "NK", "OF", "OG"},
                {"NA", "NB", "NC", "ND", "NE", "OA", "OB"},
                {"HV", "HW", "HX", "HY", "HZ", "JV", "JW"},
                {"HQ", "HR", "HS", "HT", "HU", "JQ", "JR"},
                {"HL", "HM", "HN", "HO", "HP", "JL", "JM"}};
        if (Integer.parseInt(north) >= squares.length || Integer.parseInt(east) >= squares[0].length) {
            return "";
        }
        return squares[Integer.parseInt(north)][Integer.parseInt(east)];
    }

    static double getE(Double phi, Double lambda) {
        double IV = getIV(phi);
        double V = getV(phi);
        double VI = getVI(phi);
        return E0 + IV * (lambda - lambda0) + V * Math.pow((lambda - lambda0), 3) + VI * Math.pow((lambda - lambda0), 5);
    }

    static double getN(Double phi, Double lambda) {
        double I = getI(phi);
        double II = getII(phi);
        double III = getIII(phi);
        double IIIA = getIIIA(phi);
        return I + II * Math.pow((lambda - lambda0), 2) + III * Math.pow((lambda - lambda0), 4) + IIIA * Math.pow((lambda - lambda0), 6);
    }

    static double getVI(Double phi) {
        double nu = getNu(phi);
        double eta2 = getEtaSquared(phi);
        return (nu / 120) * Math.pow(Math.cos(phi), 5) * (5 - 18 * Math.pow(Math.tan(phi), 2) + Math.pow(Math.tan(phi), 4) + 14 * eta2 - 58 * Math.pow(Math.tan(phi), 2) * eta2);
    }

    static double getV(Double phi) {
        double nu = getNu(phi);
        double rho = getRho(phi);
        return (nu / 6) * Math.pow(Math.cos(phi), 3) * ((nu / rho) - Math.pow(Math.tan(phi), 2));
    }

    static double getIV(Double phi) {
        double nu = getNu(phi);
        return nu * Math.cos(phi);
    }

    static double getIIIA(Double phi) {
        double nu = getNu(phi);
        return (nu / 720) * Math.sin(phi) * Math.pow(Math.cos(phi), 5) * (61 - 58 * Math.pow(Math.tan(phi), 2) + Math.pow(Math.tan(phi), 4));
    }

    static double getIII(Double phi) {
        double nu = getNu(phi);
        double eta2 = getEtaSquared(phi);
        return (nu / 24) * Math.sin(phi) * Math.pow(Math.cos(phi), 3) * (5 - Math.pow(Math.tan(phi), 2) + 9 * eta2);
    }

    static double getII(Double phi) {
        double nu = getNu(phi);
        return (nu / 2) * Math.sin(phi) * Math.cos(phi);
    }

    static double getI(Double phi) {
        double M = getM(phi);
        return M + N0;
    }

    static double getM(Double phi) {
        return b * F0 * (
                (1 + n + (5d / 4) * Math.pow(n, 2) + (5d / 4) * Math.pow(n, 3)) * (phi - phi0)
                        - (3 * n + 3 * Math.pow(n, 2) + (21d / 8) * Math.pow(n, 3)) * Math.sin(phi - phi0) * Math.cos(phi + phi0)
                        + ((15d / 8) * Math.pow(n, 2) + (15d / 8) * Math.pow(n, 3)) * Math.sin(2 * (phi - phi0)) * Math.cos(2 * (phi + phi0))
                        - (35d / 24) * Math.pow(n, 3) * Math.sin(3 * ( phi - phi0)) * Math.cos(3 * (phi + phi0))
        );
    }

    static double getEtaSquared(Double phi) {
        double nu = getNu(phi);
        double rho = getRho(phi);
        return (nu / rho) - 1;
    }

    static double getRho(Double phi) {
        return a * F0 * (1 - e2) * (1 - e2 * Math.pow((1 - e2 * Math.sin(phi)), -1.5));
    }

    static double getNu(Double phi) {
        return a * F0 * (1 - e2 * Math.pow((1 - e2 * Math.sin(phi)), -0.5));
    }
}

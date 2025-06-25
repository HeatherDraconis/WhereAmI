package com.example.whereami;

import org.jetbrains.annotations.NotNull;

/**
 * Implements lat/long to os grid ref according to:
 * https://www.ordnancesurvey.co.uk/documents/resources/guide-coordinate-systems-great-britain.pdf
 */
public class OSGridRef {
    private static final double phi0 = Math.toRadians(49);
    private static final double lambda0 = Math.toRadians(-2);

    private static final double N0 = -100_000;
    private static final double E0 = 400_000;
    private static final double F0 = 0.9996012717;

    private static final double a = 6_377_563.396;
    private static final double b = 6_356_256.909;
    private static final double e2 = 1 - (b * b) / (a * a);

    private static final double n = (a - b) / (a + b);
    private static final double n2 = n * n;
    private static final double n3 = n * n * n;

    public static String getGridRef(double latitude, double longitude, int figureSize) {
        double[] coordinates = doHelmertTransformation(Math.toRadians(latitude), Math.toRadians(longitude));

        String E = String.valueOf((int) getE(coordinates[0], coordinates[1]));
        String N = String.valueOf((int) getN(coordinates[0], coordinates[1]));
        return doGetGridRef(figureSize, E, N);
    }

    public static String getGridRef2(double latitude, double longitude, int figureSize) {
        double phi = Math.toRadians(latitude);
        double lambda = Math.toRadians(longitude);
        String E = String.valueOf((int) getE(phi, lambda));
        String N = String.valueOf((int) getN(phi, lambda));
        return doGetGridRef(figureSize, E, N);
    }

    private static @NotNull String doGetGridRef(int figureSize, String E, String N) {
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

        return letters + " " + E.substring(1, digits + 1) + " " + N.substring(1, digits + 1);
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

    static double getE(double phi, double lambda) {
        double IV = getIV(phi);
        double V = getV(phi);
        double VI = getVI(phi);
        double lambdaDiff = lambda - lambda0;
        double lambdaPow3 = lambdaDiff * lambdaDiff * lambdaDiff;
        double lambdaPow5 = lambdaDiff * lambdaDiff * lambdaDiff * lambdaDiff * lambdaDiff;
        return E0 + IV * lambdaDiff + V * lambdaPow3 + VI * lambdaPow5;
    }

    static double getN(double phi, double lambda) {
        double I = getI(phi);
        double II = getII(phi);
        double III = getIII(phi);
        double IIIA = getIIIA(phi);
        return I + II * Math.pow(lambda - lambda0, 2) + III * Math.pow(lambda - lambda0, 4) + IIIA * Math.pow(lambda - lambda0, 6);
    }

    static double getVI(double phi) {
        double nu = getNu(phi);
        double eta2 = getEtaSquared(phi);
        return (nu / 120) * Math.pow(Math.cos(phi), 5) * (5 - 18 * Math.pow(Math.tan(phi), 2) + Math.pow(Math.tan(phi), 4) + 14 * eta2 - 58 * Math.pow(Math.tan(phi), 2) * eta2);
    }

    static double getV(double phi) {
        double nu = getNu(phi);
        double rho = getRho(phi);
        return (nu / 6) * Math.pow(Math.cos(phi), 3) * ((nu / rho) - Math.pow(Math.tan(phi), 2));
    }

    static double getIV(double phi) {
        double nu = getNu(phi);
        return nu * Math.cos(phi);
    }

    static double getIIIA(double phi) {
        double nu = getNu(phi);
        return (nu / 720) * Math.sin(phi) * Math.pow(Math.cos(phi), 5) * (61 - 58 * Math.pow(Math.tan(phi), 2) + Math.pow(Math.tan(phi), 4));
    }

    static double getIII(double phi) {
        double nu = getNu(phi);
        double eta2 = getEtaSquared(phi);
        return (nu / 24) * Math.sin(phi) * Math.pow(Math.cos(phi), 3) * (5 - Math.pow(Math.tan(phi), 2) + 9 * eta2);
    }

    static double getII(double phi) {
        double nu = getNu(phi);
        return (nu / 2) * Math.sin(phi) * Math.cos(phi);
    }

    static double getI(double phi) {
        double M = getM(phi);
        return M + N0;
    }

    static double getM(double phi) {
        return b * F0 * (
                (1 + n + (5d / 4) * n2 + (5d / 4) * n3) * (phi - phi0)
                        - (3 * n + 3 * n2 + (21d / 8) * n3) * Math.sin(phi - phi0) * Math.cos(phi + phi0)
                        + ((15d / 8) * n2 + (15d / 8) * n3) * Math.sin(2 * (phi - phi0)) * Math.cos(2 * (phi + phi0))
                        - (35d / 24) * n3 * Math.sin(3 * (phi - phi0)) * Math.cos(3 * (phi + phi0))
        );
    }

    static double getEtaSquared(double phi) {
        double nu = getNu(phi);
        double rho = getRho(phi);
        return (nu / rho) - 1;
    }

    static double getRho(double phi) {
        return a * F0 * (1 - e2) * Math.pow(1 - e2 * Math.pow(Math.sin(phi), 2), -1.5);
    }

    static double getNu(double phi) {
        return a * F0 * Math.pow(1 - e2 * Math.pow(Math.sin(phi), 2), -0.5);
    }

    static double[] getCartesian(double phi, double lambda) {
        double a = 6_378_137.000;
        double b = 6_356_752.3141;
        double e2 = 1 - (b * b) / (a * a);
        double F0 = 0.9996;

        double nu = a * F0 * Math.pow(1 - e2 * Math.pow(Math.sin(phi), 2), -0.5);
        double H = 299.800;
        double x = (nu + H) * Math.cos(phi) * Math.cos(lambda);
        double y = (nu + H) * Math.cos(phi) * Math.sin(lambda);
        double z = ((1 - e2) * nu + H) * Math.sin(phi);
        return new double[]{x, y, z};
    }

    static double[] getLatLong(double x, double y, double z) {
        double phi = Math.atan(z / (Math.sqrt(x * x + y * y) * (1 - e2)));
        phi = Math.atan((z + e2 * getNu(phi) * Math.sin(phi)) * Math.pow(x * x + y * y, -0.5));
        double lambda = Math.atan(y / x);
        return new double[]{phi, lambda};
    }

    /**
     * c.f. p36
     */
    static double[] doHelmertTransformation(double phi, double lambda) {
        double[] cart = getCartesian(phi, lambda);
        double second = Math.PI / (180 * 60 * 60);
        double[] t = new double[]{-446.448, 125.157, -542.060};
        double s = 20.4894 / 1_000_000;
        double rx = -0.1502 * second;
        double ry = -0.2470 * second;
        double rz = -0.8421 * second;
        double[][] h = new double[][]
                {{1 + s, -rz, ry},
                {rz, 1 + s, -rx},
                {-ry, rx, 1 + s}};
        double[] result = add(multiply(h, cart), t);
        return getLatLong(result[0], result[1], result[2]);
    }

    private static double @NotNull [] multiply(double[][] matrix, double[] colVector) {
        return new double[]{
                matrix[0][0] * colVector[0] + matrix[0][1] * colVector[1] + matrix[0][2] * colVector[2],
                matrix[1][0] * colVector[0] + matrix[1][1] * colVector[1] + matrix[1][2] * colVector[2],
                matrix[2][0] * colVector[0] + matrix[2][1] * colVector[1] + matrix[2][2] * colVector[2]};
    }

    private static double[] add(double[] a, double[] b) {
        return new double[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }
}

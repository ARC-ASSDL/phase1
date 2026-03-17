package com.evtol.trajectoryengine.fitting;

import com.evtol.trajectoryengine.domain.Waypoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LeastSquaresFitter {

    @Value("${trajectory.ls.degree:3}")
    private int degree;

    @Value("${trajectory.ls.controlPoints:20}")
    private int controlPointCount;

    public List<Waypoint> fit(List<Waypoint> waypoints) {

        int n = waypoints.size();
        if (n < 2) return waypoints;

        double tMin = waypoints.get(0).getT();
        double tMax = waypoints.get(n - 1).getT();
        double range = tMax - tMin;

        double[] tNorm = new double[n];
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];

        for (int i = 0; i < n; i++) {
            Waypoint wp = waypoints.get(i);
            tNorm[i] = (wp.getT() - tMin) / range;
            x[i] = wp.getX();
            y[i] = wp.getY();
            z[i] = wp.getZ();
        }

        double[] coeffX = fitPolynomial(tNorm, x, degree);
        double[] coeffY = fitPolynomial(tNorm, y, degree);
        double[] coeffZ = fitPolynomial(tNorm, z, degree);

        List<Waypoint> controlPoints = new ArrayList<>();

        for (int i = 0; i < controlPointCount; i++) {
            double tN = (double) i / (controlPointCount - 1);
            double tActual = tMin + tN * range;

            double cx = eval(coeffX, tN);
            double cy = eval(coeffY, tN);
            double cz = eval(coeffZ, tN);

            controlPoints.add(new Waypoint(tActual, cx, cy, cz));
        }

        return controlPoints;
    }

    // -------------------------------
    // Polynomial Least Squares
    // -------------------------------
    private double[] fitPolynomial(double[] t, double[] values, int degree) {

        int n = t.length;
        int m = degree + 1;

        double[][] A = new double[m][m];
        double[] B = new double[m];

        for (int row = 0; row < m; row++) {
            for (int col = 0; col < m; col++) {
                double sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += Math.pow(t[i], row + col);
                }
                A[row][col] = sum;
            }

            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += values[i] * Math.pow(t[i], row);
            }
            B[row] = sum;
        }

        return solveGaussian(A, B);
    }

    // -------------------------------
    // Gaussian Elimination
    // -------------------------------
    private double[] solveGaussian(double[][] A, double[] B) {

        int n = B.length;

        for (int i = 0; i < n; i++) {

            // Pivot
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = k;
                }
            }

            // Swap
            double[] temp = A[i];
            A[i] = A[maxRow];
            A[maxRow] = temp;

            double t = B[i];
            B[i] = B[maxRow];
            B[maxRow] = t;

            // Eliminate
            for (int k = i + 1; k < n; k++) {
                double factor = A[k][i] / A[i][i];

                for (int j = i; j < n; j++) {
                    A[k][j] -= factor * A[i][j];
                }
                B[k] -= factor * B[i];
            }
        }

        // Back substitution
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            double sum = B[i];

            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }

            x[i] = sum / A[i][i];
        }

        return x;
    }

    // -------------------------------
    // Evaluate polynomial
    // -------------------------------
    private double eval(double[] coeff, double t) {
        double result = 0;
        double power = 1;

        for (double c : coeff) {
            result += c * power;
            power *= t;
        }

        return result;
    }
}
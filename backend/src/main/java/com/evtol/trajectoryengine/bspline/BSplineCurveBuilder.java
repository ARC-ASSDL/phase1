package com.evtol.trajectoryengine.bspline;

import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.Waypoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BSplineCurveBuilder {

    private static final int DEGREE = 3;

    private final KnotVectorGenerator knotVectorGenerator;

    // ✅ NEW: smoothing factor
    @Value("${trajectory.bspline.lambda:0.1}")
    private double lambda;

    public BSplineCurveBuilder(KnotVectorGenerator knotVectorGenerator) {
        this.knotVectorGenerator = knotVectorGenerator;
    }

    public TrajectoryModel build(List<Waypoint> controlPoints) {

        if (controlPoints == null || controlPoints.size() < DEGREE + 1) {
            throw new IllegalArgumentException(
                    "Not enough control points for cubic B-Spline"
            );
        }

        int n = controlPoints.size();

        // ✅ STEP 1: Smooth control points (TRUE global smoothing)
        List<Waypoint> smoothedControlPoints = smooth(controlPoints);

        double[] knots = knotVectorGenerator.generateClampedUniform(
                n,
                DEGREE
        );

        double totalDuration =
                knots[knots.length - DEGREE - 1] - knots[DEGREE];

        return new TrajectoryModel(
                smoothedControlPoints, // ✅ smoothed control points
                knots,
                DEGREE,
                totalDuration
        );
    }

    // -------------------------------
    // TRUE smoothing: (I + λ DᵀD)P = P_input
    // -------------------------------
    private List<Waypoint> smooth(List<Waypoint> points) {

        int n = points.size();

        // Build D matrix (second difference)
        double[][] D = new double[n - 2][n];
        for (int i = 0; i < n - 2; i++) {
            D[i][i] = 1;
            D[i][i + 1] = -2;
            D[i][i + 2] = 1;
        }

        // Build A = I + λ DᵀD
        double[][] Dt = transpose(D);
        double[][] A = add(identity(n), scale(multiply(Dt, D), lambda));

        // Extract coordinates
        double[] bx = extract(points, 'x');
        double[] by = extract(points, 'y');
        double[] bz = extract(points, 'z');

        // Solve
        double[] Px = solve(A, bx);
        double[] Py = solve(A, by);
        double[] Pz = solve(A, bz);

        // Build smoothed points
        List<Waypoint> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(new Waypoint(
                    points.get(i).getT(),
                    Px[i],
                    Py[i],
                    Pz[i]
            ));
        }

        return result;
    }

    // -------------------------------
    // Matrix utilities
    // -------------------------------
    private double[][] identity(int n) {
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) {
            I[i][i] = 1.0;
        }
        return I;
    }

    private double[][] transpose(double[][] A) {
        int r = A.length, c = A[0].length;
        double[][] T = new double[c][r];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                T[j][i] = A[i][j];
        return T;
    }

    private double[][] multiply(double[][] A, double[][] B) {
        int r = A.length, c = B[0].length, n = B.length;
        double[][] M = new double[r][c];
        for (int i = 0; i < r; i++)
            for (int j = 0; j < c; j++)
                for (int k = 0; k < n; k++)
                    M[i][j] += A[i][k] * B[k][j];
        return M;
    }

    private double[][] add(double[][] A, double[][] B) {
        double[][] R = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[0].length; j++)
                R[i][j] = A[i][j] + B[i][j];
        return R;
    }

    private double[][] scale(double[][] A, double s) {
        double[][] R = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A[0].length; j++)
                R[i][j] = A[i][j] * s;
        return R;
    }

    private double[] extract(List<Waypoint> w, char axis) {
        double[] arr = new double[w.size()];
        for (int i = 0; i < w.size(); i++) {
            arr[i] = (axis == 'x') ? w.get(i).getX()
                    : (axis == 'y') ? w.get(i).getY()
                    : w.get(i).getZ();
        }
        return arr;
    }

    // Gaussian elimination
    private double[] solve(double[][] A, double[] B) {
        int n = B.length;

        for (int i = 0; i < n; i++) {
            int max = i;
            for (int k = i + 1; k < n; k++)
                if (Math.abs(A[k][i]) > Math.abs(A[max][i])) max = k;

            double[] tmp = A[i]; A[i] = A[max]; A[max] = tmp;
            double t = B[i]; B[i] = B[max]; B[max] = t;

            for (int k = i + 1; k < n; k++) {
                double f = A[k][i] / A[i][i];
                for (int j = i; j < n; j++)
                    A[k][j] -= f * A[i][j];
                B[k] -= f * B[i];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = B[i];
            for (int j = i + 1; j < n; j++)
                sum -= A[i][j] * x[j];
            x[i] = sum / A[i][i];
        }
        return x;
    }
}
package com.evtol.trajectoryengine.spline;

import com.evtol.trajectoryengine.domain.CubicSegment;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.Waypoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CubicSplineBuilder {

    public TrajectoryModel build(List<Waypoint> waypoints) {

        int n = waypoints.size();

        double[] t = new double[n];
        double[] x = new double[n];
        double[] y = new double[n];
        double[] z = new double[n];

        for (int i = 0; i < n; i++) {
            Waypoint wp = waypoints.get(i);
            t[i] = wp.getT();
            x[i] = wp.getX();
            y[i] = wp.getY();
            z[i] = wp.getZ();
        }

        List<CubicSegment> splineX = buildAxisSpline(t, x);
        List<CubicSegment> splineY = buildAxisSpline(t, y);
        List<CubicSegment> splineZ = buildAxisSpline(t, z);

        double duration = t[n - 1];

        return new TrajectoryModel(splineX, splineY, splineZ, duration);
    }

    private List<CubicSegment> buildAxisSpline(double[] t, double[] values) {

        int n = values.length;
        int segments = n - 1;

        double[] a = new double[segments];
        double[] b = new double[segments];
        double[] c = new double[n];
        double[] d = new double[segments];
        double[] h = new double[segments];

        for (int i = 0; i < segments; i++) {
            h[i] = t[i + 1] - t[i];
        }

        double[] alpha = new double[n];

        for (int i = 1; i < segments; i++) {
            alpha[i] =
                    (3 / h[i]) * (values[i + 1] - values[i]) -
                            (3 / h[i - 1]) * (values[i] - values[i - 1]);
        }

        double[] l = new double[n];
        double[] mu = new double[n];
        double[] z = new double[n];

        l[0] = 1;
        mu[0] = 0;
        z[0] = 0;

        for (int i = 1; i < segments; i++) {

            l[i] = 2 * (t[i + 1] - t[i - 1]) - h[i - 1] * mu[i - 1];

            mu[i] = h[i] / l[i];

            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        l[n - 1] = 1;
        z[n - 1] = 0;
        c[n - 1] = 0;

        for (int j = segments - 1; j >= 0; j--) {

            c[j] = z[j] - mu[j] * c[j + 1];

            b[j] =
                    (values[j + 1] - values[j]) / h[j]
                            - h[j] * (c[j + 1] + 2 * c[j]) / 3;

            d[j] =
                    (c[j + 1] - c[j]) / (3 * h[j]);

            a[j] = values[j];
        }

        List<CubicSegment> result = new ArrayList<>();

        for (int i = 0; i < segments; i++) {

            result.add(
                    new CubicSegment(
                            t[i],
                            t[i + 1],
                            a[i],
                            b[i],
                            c[i],
                            d[i]
                    )
            );
        }

        return result;
    }
}
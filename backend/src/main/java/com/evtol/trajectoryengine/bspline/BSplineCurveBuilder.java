package com.evtol.trajectoryengine.bspline;

import com.evtol.trajectoryengine.domain.TrajectoryModel;

import com.evtol.trajectoryengine.domain.Waypoint;

import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class BSplineCurveBuilder {

    private static final int DEGREE = 3;

    private final KnotVectorGenerator knotVectorGenerator;

    public BSplineCurveBuilder(KnotVectorGenerator knotVectorGenerator) {

        this.knotVectorGenerator = knotVectorGenerator;

    }

    public TrajectoryModel build(List<Waypoint> waypoints) {

        if (waypoints == null || waypoints.size() < DEGREE + 1) {

            throw new IllegalArgumentException(

                    "Not enough waypoints for cubic B-Spline"

            );

        }

        int numControlPoints = waypoints.size();

        double[] knots = knotVectorGenerator.generateClampedUniform(

                numControlPoints,

                DEGREE

        );

        /*

         * Total duration for sampling

         * For B-Splines we use the valid knot interval

         */

        double totalDuration =

                knots[knots.length - DEGREE - 1] - knots[DEGREE];

        return new TrajectoryModel(

                waypoints,

                knots,

                DEGREE,

                totalDuration

        );

    }

}

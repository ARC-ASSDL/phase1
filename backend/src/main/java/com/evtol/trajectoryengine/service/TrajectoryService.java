package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.datasource.CsvWaypointDataProvider;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import com.evtol.trajectoryengine.domain.Waypoint;
import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.spline.CubicSplineBuilder;
import com.evtol.trajectoryengine.bspline.BSplineCurveBuilder;
import com.evtol.trajectoryengine.validation.WaypointValidator;
import com.evtol.trajectoryengine.fitting.LeastSquaresFitter;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrajectoryService {

    private final CsvWaypointDataProvider dataProvider;
    private final WaypointValidator validator;

    private final CubicSplineBuilder cubicSplineBuilder;
    private final BSplineCurveBuilder bSplineCurveBuilder;

    private final SamplingService samplingService;

    private final LeastSquaresFitter leastSquaresFitter;

    @Value("${trajectory.sampling.interval}")
    private double samplingInterval;

    @Value("${trajectory.algorithm}")
    private String algorithm;

    public TrajectoryResponse generateTrajectory() {

        // 1. Load waypoints
        List<Waypoint> waypoints = dataProvider.loadWaypoints();

        // 2. Validate waypoints
        validator.validate(waypoints);

        // ✅ 3. Apply Least Squares to generate control points
        List<Waypoint> controlPoints = leastSquaresFitter.fit(waypoints);

        // 4. Build trajectory model
        TrajectoryModel trajectoryModel;

        if ("bspline".equalsIgnoreCase(algorithm)) {
            System.out.println("BSpline  ---- (using control points)");
            trajectoryModel = bSplineCurveBuilder.build(controlPoints);
        } else {
            System.out.println("cubicSpline  --- (using original waypoints)");
            trajectoryModel = cubicSplineBuilder.build(waypoints); // unchanged
        }

        // 5. Sample trajectory
        List<TrajectoryPoint> points =
                samplingService.sample(trajectoryModel, samplingInterval);

        // 6. Build response
        return new TrajectoryResponse(
                points,waypoints,
                trajectoryModel.getTotalDuration()
        );
    }
}
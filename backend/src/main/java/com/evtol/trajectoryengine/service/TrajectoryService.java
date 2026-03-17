package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.datasource.CsvWaypointDataProvider;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import com.evtol.trajectoryengine.domain.Waypoint;
import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.spline.CubicSplineBuilder;
import com.evtol.trajectoryengine.bspline.BSplineCurveBuilder;
import com.evtol.trajectoryengine.validation.WaypointValidator;

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

    @Value("${trajectory.sampling.interval}")
    private double samplingInterval;

    @Value("${trajectory.algorithm:cubic}")
    private String algorithm;

    public TrajectoryResponse generateTrajectory() {

        // 1. Load waypoints
        List<Waypoint> waypoints = dataProvider.loadWaypoints();
        System.out.println("Loaded waypoints: " + waypoints.size());

        // 2. Validate
        validator.validate(waypoints);

        // 3. Choose algorithm safely
        TrajectoryModel trajectoryModel;

        switch (algorithm.toLowerCase()) {

            case "bspline":
                System.out.println("Using B-Spline");
                trajectoryModel = bSplineCurveBuilder.build(waypoints);
                break;

            case "cubic":
            case "cubicspline":
            default:
                System.out.println("Using Cubic Spline");
                trajectoryModel = cubicSplineBuilder.build(waypoints);
                break;
        }

        // 4. Sample trajectory
        List<TrajectoryPoint> points =
                samplingService.sample(trajectoryModel, samplingInterval);

        System.out.println("Generated samples: " + points.size());
        System.out.println("Total duration: " + trajectoryModel.getTotalDuration());

        // 5. Return BOTH trajectory + original waypoints
        return new TrajectoryResponse(
                points,
                waypoints,   // ✅ added
                trajectoryModel.getTotalDuration()
        );
    }
}
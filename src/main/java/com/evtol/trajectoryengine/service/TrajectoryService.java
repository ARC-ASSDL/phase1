package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.datasource.CsvWaypointDataProvider;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import com.evtol.trajectoryengine.domain.Waypoint;
import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.spline.CubicSplineBuilder;
import com.evtol.trajectoryengine.validation.WaypointValidator;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.beans.factory.annotation.Value;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrajectoryService {

    private final CsvWaypointDataProvider dataProvider;
    private final WaypointValidator validator;
    private final CubicSplineBuilder splineBuilder;
    private final SamplingService samplingService;

    @Value("${trajectory.sampling.interval}")
    private  double samplingInterval;

    public TrajectoryResponse generateTrajectory() {

        // 1. Load data
        List<Waypoint> waypoints = dataProvider.loadWaypoints();

        // 2. Validate
        validator.validate(waypoints);

        // 3. Build spline
        TrajectoryModel trajectoryModel = splineBuilder.build(waypoints);

        // 4. sample spline
        List<TrajectoryPoint> points = samplingService.sample(trajectoryModel,samplingInterval);

        // 5. Build response
        TrajectoryResponse response = new TrajectoryResponse(points,trajectoryModel.getTotalDuration());
        return response;

    }
}

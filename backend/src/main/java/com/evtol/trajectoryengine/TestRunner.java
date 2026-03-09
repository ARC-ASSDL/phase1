//This class is just to Test loadWayPoints method from CsvWayPointDataProvider
package com.evtol.trajectoryengine;

import com.evtol.trajectoryengine.datasource.CsvWaypointDataProvider;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import com.evtol.trajectoryengine.domain.Waypoint;
import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.service.SamplingService;
import com.evtol.trajectoryengine.spline.CubicSplineBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestRunner implements CommandLineRunner {

    private final CsvWaypointDataProvider csvWaypointDataProvider;
    private final SamplingService samplingService;
    private final CubicSplineBuilder cubicSplineBuilder;

    public TestRunner(CsvWaypointDataProvider csvWaypointDataProvider,
                      SamplingService samplingService, CubicSplineBuilder csb, CubicSplineBuilder cubicSplineBuilder) {
        this.csvWaypointDataProvider = csvWaypointDataProvider;
        this.samplingService = samplingService;
        this.cubicSplineBuilder = cubicSplineBuilder;
    }

    @Override
    public void run(String... args) throws Exception {

        List<Waypoint> waypoints = csvWaypointDataProvider.loadWaypoints();

        TrajectoryModel model = cubicSplineBuilder.build(waypoints);

        List<TrajectoryPoint> points = samplingService.sample(model, 0.5);

//        System.out.println(points);

        for(TrajectoryPoint point : points)
            System.out.println(point);
    }
}
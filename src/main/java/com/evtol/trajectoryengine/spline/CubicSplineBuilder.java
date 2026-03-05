package com.evtol.trajectoryengine.spline;

import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.Waypoint;
import com.evtol.trajectoryengine.service.TrajectoryService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CubicSplineBuilder {

    public TrajectoryModel build(List<Waypoint> waypoints){
        return new TrajectoryModel(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                0.0
        );
    }
}

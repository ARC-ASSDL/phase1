package com.evtol.trajectoryengine.datasource;

import com.evtol.trajectoryengine.domain.Waypoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CsvWaypointDataProvider {

    public List<Waypoint> loadWaypoints(){
        List<Waypoint> waypoints = new ArrayList<>();
        return waypoints;
    }
}
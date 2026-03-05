//This class is just to Test loadWayPoints method from CsvWayPointDataProvider
package com.evtol.trajectoryengine;

import com.evtol.trajectoryengine.datasource.CsvWaypointDataProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final CsvWaypointDataProvider csvWaypointDataProvider;

    public TestRunner(CsvWaypointDataProvider csvWaypointDataProvider) {
        this.csvWaypointDataProvider = csvWaypointDataProvider;
    }

    @Override
    public void run(String... args) throws Exception {

        csvWaypointDataProvider.loadWaypoints();

    }
}
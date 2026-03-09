package com.evtol.trajectoryengine.domain;

import java.util.List;

public class TrajectoryModel {

    private final List<CubicSegment> xSegments;
    private final List<CubicSegment> ySegments;
    private final List<CubicSegment> zSegments;

    private final double totalDuration;

    public TrajectoryModel(
            List<CubicSegment> xSegments,
            List<CubicSegment> ySegments,
            List<CubicSegment> zSegments,
            double totalDuration) {

        this.xSegments = xSegments;
        this.ySegments = ySegments;
        this.zSegments = zSegments;
        this.totalDuration = totalDuration;
    }
}

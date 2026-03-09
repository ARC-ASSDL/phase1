package com.evtol.trajectoryengine.domain;

import com.evtol.trajectoryengine.domain.CubicSegment;
import lombok.Getter;

import java.util.List;

@Getter
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

    public List<CubicSegment> getXSegments() {
        return xSegments;
    }

    public List<CubicSegment> getYSegments() {
        return ySegments;
    }

    public List<CubicSegment> getZSegments() {
        return zSegments;
    }

    public double getTotalDuration(){
        return totalDuration;
    }
}

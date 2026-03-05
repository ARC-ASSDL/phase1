package com.evtol.trajectoryengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrajectoryModel {

    private final List<CubicSegment> splineX;
    private final List<CubicSegment> splineY;
    private final List<CubicSegment> splineZ;
    private final double totalDuration;
}

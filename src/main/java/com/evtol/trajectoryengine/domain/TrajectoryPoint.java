package com.evtol.trajectoryengine.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TrajectoryPoint {

    private double t;
    private double x;
    private double y;
    private double z;
}

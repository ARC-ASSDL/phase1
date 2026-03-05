package com.evtol.trajectoryengine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CubicSegment {
    private final double a;
    private final double b;
    private final double c;
    private final double d;
    private final double t0;
}

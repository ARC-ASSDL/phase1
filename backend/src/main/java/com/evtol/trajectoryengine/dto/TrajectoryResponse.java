package com.evtol.trajectoryengine.dto;

import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrajectoryResponse {

    private List<TrajectoryPoint> trajectory;
    private double totalDuration;
}

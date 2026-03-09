package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SamplingService {

    public List<TrajectoryPoint> sample(TrajectoryModel trajectoryModel,double samplingInterval){
        List<TrajectoryPoint> trajectoryPoints = new ArrayList<>();
        return trajectoryPoints;
    }
}


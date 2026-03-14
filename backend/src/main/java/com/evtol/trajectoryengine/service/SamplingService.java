package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.domain.CubicSegment;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
@Service

public class SamplingService {

    public List<TrajectoryPoint> sample(TrajectoryModel trajectoryModel, double samplingInterval) {

        List<TrajectoryPoint> samples = new ArrayList<>();

        double totalDuration = trajectoryModel.getTotalDuration();

        List<CubicSegment> xSegments = trajectoryModel.getXSegments();
        List<CubicSegment> ySegments = trajectoryModel.getYSegments();
        List<CubicSegment> zSegments = trajectoryModel.getZSegments();

        int segmentIndex = 0;

        for (double t = 0.0; t <= totalDuration; t += samplingInterval) {

            // Move to correct segment
            while (segmentIndex < xSegments.size() - 1 &&
                    t > xSegments.get(segmentIndex).getT1()) {
                segmentIndex++;
            }

            CubicSegment xSeg = xSegments.get(segmentIndex);
            CubicSegment ySeg = ySegments.get(segmentIndex);
            CubicSegment zSeg = zSegments.get(segmentIndex);

            double x = xSeg.evaluate(t);
            double y = ySeg.evaluate(t);
            double z = zSeg.evaluate(t);

            samples.add(new TrajectoryPoint(t, x, y, z));
        }

//        for (TrajectoryPoint point : samples) {
//            System.out.println(
//                    point.getT() + " " +
//                            point.getX() + " " +
//                            point.getY() + " " +
//                            point.getZ()
//            );
//        }

        return samples;
    }
}

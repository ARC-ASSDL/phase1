//package com.evtol.trajectoryengine.service;
//
//import com.evtol.trajectoryengine.bspline.DeBoorEvaluator;
//import com.evtol.trajectoryengine.domain.CubicSegment;
//import com.evtol.trajectoryengine.domain.TrajectoryModel;
//import com.evtol.trajectoryengine.domain.TrajectoryPoint;
//import com.evtol.trajectoryengine.domain.Waypoint;
//
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class SamplingService {
//
//    private final DeBoorEvaluator deBoorEvaluator = new DeBoorEvaluator();
//
//    public List<TrajectoryPoint> sample(TrajectoryModel trajectoryModel,
//                                        double samplingInterval) {
//
//        if (trajectoryModel.getControlPoints() != null) {
//            return sampleBSpline(trajectoryModel, samplingInterval);
//        } else {
//            return sampleCubicSpline(trajectoryModel, samplingInterval);
//        }
//    }
//
//    /*
//     * Sampling for Cubic Splines (existing logic)
//     */
//    private List<TrajectoryPoint> sampleCubicSpline(TrajectoryModel trajectoryModel,
//                                                    double samplingInterval) {
//
//        List<TrajectoryPoint> samples = new ArrayList<>();
//
//        double totalDuration = trajectoryModel.getTotalDuration();
//
//        List<CubicSegment> xSegments = trajectoryModel.getXSegments();
//        List<CubicSegment> ySegments = trajectoryModel.getYSegments();
//        List<CubicSegment> zSegments = trajectoryModel.getZSegments();
//
//        int segmentIndex = 0;
//
//        for (double t = 0.0; t <= totalDuration; t += samplingInterval) {
//
//            while (segmentIndex < xSegments.size() - 1 &&
//                    t > xSegments.get(segmentIndex).getT1()) {
//                segmentIndex++;
//            }
//
//            CubicSegment xSeg = xSegments.get(segmentIndex);
//            CubicSegment ySeg = ySegments.get(segmentIndex);
//            CubicSegment zSeg = zSegments.get(segmentIndex);
//
//            double x = xSeg.evaluate(t);
//            double y = ySeg.evaluate(t);
//            double z = zSeg.evaluate(t);
//
//            samples.add(new TrajectoryPoint(t, x, y, z));
//        }
//
//        return samples;
//    }
//
//    /*
//     * Sampling for B-Spline using De Boor
//     */
//    private List<TrajectoryPoint> sampleBSpline(TrajectoryModel trajectoryModel,
//                                                double samplingInterval) {
//
//        List<TrajectoryPoint> samples = new ArrayList<>();
//
//        List<Waypoint> controlPoints = trajectoryModel.getControlPoints();
//        double[] knots = trajectoryModel.getKnots();
//        int degree = trajectoryModel.getDegree();
//
//        double tStart = knots[degree];
//        double tEnd = knots[knots.length - degree - 1];
//
//        for (double t = tStart; t <= tEnd; t += samplingInterval) {
//
//            Waypoint p = deBoorEvaluator.evaluate(
//                    t,
//                    degree,
//                    knots,
//                    controlPoints
//            );
//
//            samples.add(
//                    new TrajectoryPoint(
//                            t,
//                            p.getX(),
//                            p.getY(),
//                            p.getZ()
//                    )
//            );
//        }
//
//        return samples;
//    }
//}



package com.evtol.trajectoryengine.service;

import com.evtol.trajectoryengine.bspline.DeBoorEvaluator;
import com.evtol.trajectoryengine.domain.CubicSegment;
import com.evtol.trajectoryengine.domain.TrajectoryModel;
import com.evtol.trajectoryengine.domain.TrajectoryPoint;
import com.evtol.trajectoryengine.domain.Waypoint;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SamplingService {

    private final DeBoorEvaluator deBoorEvaluator = new DeBoorEvaluator();

    public List<TrajectoryPoint> sample(TrajectoryModel trajectoryModel,
                                        double samplingInterval) {

        if (trajectoryModel.getControlPoints() != null) {
            return sampleBSpline(trajectoryModel, samplingInterval);
        } else {
            return sampleCubicSpline(trajectoryModel, samplingInterval);
        }
    }

    /*
     * Sampling for Cubic Splines (existing logic)
     */
    private List<TrajectoryPoint> sampleCubicSpline(TrajectoryModel trajectoryModel,
                                                    double samplingInterval) {

        List<TrajectoryPoint> samples = new ArrayList<>();

        double totalDuration = trajectoryModel.getTotalDuration();

        List<CubicSegment> xSegments = trajectoryModel.getXSegments();
        List<CubicSegment> ySegments = trajectoryModel.getYSegments();
        List<CubicSegment> zSegments = trajectoryModel.getZSegments();

        int segmentIndex = 0;

        for (double t = 0.0; t <= totalDuration; t += samplingInterval) {

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

        return samples;
    }

    /*
     * Sampling for B-Spline using De Boor
     */
    private List<TrajectoryPoint> sampleBSpline(TrajectoryModel trajectoryModel,
                                                double samplingInterval) {

        List<TrajectoryPoint> samples = new ArrayList<>();

        List<Waypoint> controlPoints = trajectoryModel.getControlPoints();
        double[] knots = trajectoryModel.getKnots();
        int degree = trajectoryModel.getDegree();

        double tStart = knots[degree];
        double tEnd = knots[knots.length - degree - 1];
        double duration = tEnd - tStart;
        samplingInterval=0.001;

        for (double u = 0.0; u <= 1.0; u += samplingInterval) {

            double t = tStart + u * duration;

            Waypoint p = deBoorEvaluator.evaluate(
                    t,
                    degree,
                    knots,
                    controlPoints
            );

            samples.add(
                    new TrajectoryPoint(
                            t,
                            p.getX(),
                            p.getY(),
                            p.getZ()
                    )
            );
        }

        return samples;
    }
}
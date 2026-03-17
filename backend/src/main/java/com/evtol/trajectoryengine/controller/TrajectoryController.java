package com.evtol.trajectoryengine.controller;

import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.service.TrajectoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TrajectoryController {

    private final TrajectoryService trajectoryService;

    @GetMapping("/trajectory")
    public TrajectoryResponse getTrajectory(
            @RequestParam(defaultValue = "0.1") double lambda) {

        return trajectoryService.generateTrajectory(lambda);
    }
}
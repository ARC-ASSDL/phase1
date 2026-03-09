package com.evtol.trajectoryengine.controller;

import com.evtol.trajectoryengine.dto.TrajectoryResponse;
import com.evtol.trajectoryengine.service.TrajectoryService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trajectory")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class TrajectoryController {

    private final TrajectoryService trajectoryService;

    @GetMapping
    public ResponseEntity<TrajectoryResponse> getTrajectory(){
        return ResponseEntity.ok(trajectoryService.generateTrajectory());
    }

}

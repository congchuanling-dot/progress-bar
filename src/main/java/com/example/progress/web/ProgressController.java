package com.example.progress.web;

import com.example.progress.model.ProgressState;
import com.example.progress.service.ProgressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/status")
    public ProgressState getStatus() {
        return progressService.getCurrentState();
    }

    @PostMapping("/config")
    public ResponseEntity<ProgressState> updateConfig(@RequestBody ProgressState request) {
        if (request.getBehaviorName() == null || request.getBehaviorName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getStepPercent() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ProgressState state = progressService.updateConfig(request.getBehaviorName(), request.getStepPercent());
        return ResponseEntity.ok(state);
    }

    @PostMapping("/click")
    public ProgressState clickOnce() {
        return progressService.clickOnce();
    }
}


package com.example.progress.web;

import com.example.progress.model.ProgressState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ProgressController {

    private ProgressState state = new ProgressState("默认行为", 10.0);

    @GetMapping("/status")
    public ProgressState getStatus() {
        return state;
    }

    @PostMapping("/config")
    public ResponseEntity<ProgressState> updateConfig(@RequestBody ProgressState request) {
        if (request.getBehaviorName() == null || request.getBehaviorName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getStepPercent() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        state.setBehaviorName(request.getBehaviorName());
        state.setStepPercent(request.getStepPercent());
        state.setProgressPercent(0.0);
        state.setCompletedCount(0);
        return ResponseEntity.ok(state);
    }

    @PostMapping("/click")
    public ProgressState clickOnce() {
        double newProgress = state.getProgressPercent() + state.getStepPercent();
        if (newProgress > 100.0) {
            newProgress = 100.0;
        }
        state.setProgressPercent(newProgress);
        state.setCompletedCount(state.getCompletedCount() + 1);
        return state;
    }
}


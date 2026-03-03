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
    public ResponseEntity<ProgressState> getStatus(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(progressService.getCurrentState(token.trim()));
    }

    @PostMapping("/config")
    public ResponseEntity<ProgressState> updateConfig(@RequestParam("token") String token,
                                                      @RequestBody ProgressState request) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getBehaviorName() == null || request.getBehaviorName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (request.getStepPercent() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ProgressState state = progressService.updateConfig(token.trim(), request.getBehaviorName(), request.getStepPercent());
        return ResponseEntity.ok(state);
    }

    @PostMapping("/click")
    public ResponseEntity<ProgressState> clickOnce(@RequestParam("token") String token) {
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(progressService.clickOnce(token.trim()));
    }

    /**
     * 注册一个新的令牌
     */
    public static class TokenRegisterRequest {
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @PostMapping("/token/register")
    public ResponseEntity<ProgressState> registerToken(@RequestBody TokenRegisterRequest request) {
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            ProgressState state = progressService.registerToken(request.getToken().trim());
            return ResponseEntity.ok(state);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}


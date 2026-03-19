package com.revworkforce.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "Auth Service is currently unavailable. Please try again later.",
                "service", "auth-service"
        ));
    }

    @GetMapping("/employee")
    public ResponseEntity<Map<String, Object>> employeeFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "Employee Service is currently unavailable. Please try again later.",
                "service", "employee-service"
        ));
    }

    @GetMapping("/leave")
    public ResponseEntity<Map<String, Object>> leaveFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "Leave Service is currently unavailable. Please try again later.",
                "service", "leave-service"
        ));
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> performanceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "success", false,
                "message", "Performance Service is currently unavailable. Please try again later.",
                "service", "performance-service"
        ));
    }
}

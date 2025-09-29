package com.eslirodrigues.service_app_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private ResponseEntity<Map<String, Object>> createFallbackResponse(
            String serviceName,
            String errorMessage
    ) {
        Map<String, Object> map = Map.of(
                "error", errorMessage,
                "message", "Please try again later or contact support",
                "timestamp", LocalDateTime.now(),
                "service", serviceName
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(map);
    }

    @GetMapping("/member-service")
    @PostMapping("/member-service")
    public ResponseEntity<Map<String, Object>> memberServiceFallback() {
        return createFallbackResponse(
                "member-service", "Member service is temporarily unavailable"
        );
    }

    @GetMapping("/pricing-service")
    @PostMapping("/pricing-service")
    public ResponseEntity<Map<String, Object>> pricingServiceFallback() {
        return createFallbackResponse(
                "pricing-service", "Pricing service is temporarily unavailable"
        );
    }
}
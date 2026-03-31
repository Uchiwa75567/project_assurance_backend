package com.ma_sante_assurance.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@RestController
@Tag(name = "Health", description = "Endpoint de supervision simple pour UptimeRobot et autres moniteurs")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check public", description = "Retourne un statut minimal pour le monitoring externe.")
    public HealthResponse health() {
        return new HealthResponse(
                "ok",
                ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0,
                Instant.now()
        );
    }

    public record HealthResponse(String status, double uptime, Instant timestamp) {
    }
}

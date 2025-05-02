package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.service.VisitStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Visit Statistics", description = "API для получения статистики посещений")
public class VisitStatsController {
    private final VisitStatsService statsService;

    public VisitStatsController(VisitStatsService statsService) {
        this.statsService = statsService;
    }

    @Operation(summary = "Получить статистику посещений")
    @GetMapping
    public Map<String, Long> getStats() {
        return statsService.getStats();
    }
}
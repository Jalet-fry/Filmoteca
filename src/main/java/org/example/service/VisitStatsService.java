package org.example.service;

import java.util.Map;

public interface VisitStatsService {
    void recordVisit(String endpoint);
    Map<String, Long> getStats();
}
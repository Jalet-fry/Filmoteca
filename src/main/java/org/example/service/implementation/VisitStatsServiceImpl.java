package org.example.service.implementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.example.service.VisitStatsService;
import org.springframework.stereotype.Service;

@Service
public class VisitStatsServiceImpl implements VisitStatsService {
    private final ConcurrentHashMap<String, AtomicLong> stats = new ConcurrentHashMap<>();

    @Override
    public void recordVisit(String endpoint) {
        stats.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
    }

    @Override
    public Map<String, Long> getStats() {
        return stats.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get()
                ));
    }

    public void resetStats() {
        stats.clear(); // Очищаем всю статистику
    }
}
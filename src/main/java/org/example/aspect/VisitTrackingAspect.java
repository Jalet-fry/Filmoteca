package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.service.VisitStatsService;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class VisitTrackingAspect {
    private final VisitStatsService statsService;

    public VisitTrackingAspect(VisitStatsService statsService) {
        this.statsService = statsService;
    }

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {}

    @Before("restController() && execution(* *(..))")
    public void trackVisit(JoinPoint jp) {
        String endpoint = jp.getSignature().toShortString(); // Пример: "ActorController.getAllActors()"
        statsService.recordVisit(endpoint);
    }
}
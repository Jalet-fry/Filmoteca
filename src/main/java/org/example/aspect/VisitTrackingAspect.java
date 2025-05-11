package org.example.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.service.VisitStatsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class VisitTrackingAspect {
    private final VisitStatsService statsService;

    public VisitTrackingAspect(VisitStatsService statsService) {
        this.statsService = statsService;
    }

    // Точка среза для всех RestController'ов, кроме VisitStatsController

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && "
            + "!within(org.example.controller.VisitStatsController)")
    public void allControllersExceptStats() {}

    @Before("allControllersExceptStats() && execution(* *(..))")
    public void trackVisit(JoinPoint jp) {
        HttpServletRequest request =
                ((ServletRequestAttributes)
                        RequestContextHolder.currentRequestAttributes()).getRequest();
        String url = request.getRequestURI();
        // Получаем строку параметров (например, "title=Openheimer")
        String queryString = request.getQueryString();

        // Добавляем "?" к URL, если параметры есть
        String method = request.getMethod(); // "GET", "POST"
        String key = method + " " + (queryString != null ? url + "?" : url);
        statsService.recordVisit(key);
    }
}
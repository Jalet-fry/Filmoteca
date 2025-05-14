package org.example.events;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import org.example.controller.LogController;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

@Component
public class LogEventHandler implements ApplicationListener<LogEvent> {
    @Override
    public void onApplicationEvent(LogEvent event) {
        new Thread(() -> {
            try {
                // Добавляем задержку 50*10^3 милисекунд для тестирования параллельной работы
                Thread.sleep(20000);

                Path logPath = Paths.get("app.log");
                String filteredLogs = filterLogs(logPath, event.getDate(), event.getTime());
                // Store the filtered logs with the event ID
                LogController.putResource(event.getId(),
                        new ByteArrayResource(filteredLogs.getBytes()));

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String filterLogs(Path logPath, String dateParam, String timeParam) throws IOException {
        return Files.lines(logPath)
                .filter(line -> line != null && !line.trim().isEmpty())
                .filter(line -> {
                    if (!line.matches(LogController.LOG_PATTERN)) {
                        return false;
                    }

                    try {
                        boolean matches = true;

                        if (dateParam != null) {
                            LocalDate logDate = LogController.parseDateFromLog(line);
                            LocalDate filterDate = LocalDate.parse(dateParam,
                                    LogController.DATE_FORMATTER);
                            matches = logDate.equals(filterDate);
                        }

                        if (matches && timeParam != null) {
                            LocalTime logTime = LogController.parseTimeFromLog(line);
                            LocalTime filterTime = LocalTime.parse(timeParam,
                                    LogController.TIME_FORMATTER);
                            matches = logTime.equals(filterTime);
                        }

                        return matches;
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
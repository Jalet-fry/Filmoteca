//package org.example.controller;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.stream.Collectors;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/logs")
//public class LogController {
//
//    private static final String LOG_FILE = "app.log";
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
//            .ofPattern("yyyy-MM-dd");
//    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
//            .ofPattern("HH:mm:ss");
//
//    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<Resource> getLogs(
//            @RequestParam(required = false) String date,
//            @RequestParam(required = false) String time) throws IOException {
//
//        Path logPath = Paths.get(LOG_FILE);
//        String filteredLogs = "";
//
//        if (Files.exists(logPath)) {
//            filteredLogs = filterLogs(logPath, date, time);
//        }
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"filtered_logs.log\"")
//                .contentLength(filteredLogs.getBytes().length)
//                .body(new ByteArrayResource(filteredLogs.getBytes()));
//    }
//
//    private String filterLogs(Path logPath, String dateParam, String timeParam) throws IOException {
//        return Files.lines(logPath)
//                .filter(line -> {
//                    try {
//                        boolean matches = true;
//
//                        if (dateParam != null) {
//                            LocalDate logDate = parseDateFromLog(line);
//                            LocalDate filterDate = LocalDate.parse(dateParam, DATE_FORMATTER);
//                            matches = logDate.equals(filterDate);
//                        }
//
//                        if (matches && timeParam != null) {
//                            LocalTime logTime = parseTimeFromLog(line);
//                            LocalTime filterTime = LocalTime.parse(timeParam, TIME_FORMATTER);
//                            matches = logTime.equals(filterTime);
//                        }
//
//                        return matches;
//                    } catch (DateTimeParseException e) {
//                        return false;
//                    }
//                })
//                .collect(Collectors.joining(System.lineSeparator()));
//    }
//
//    private LocalDate parseDateFromLog(String logLine) throws DateTimeParseException {
//        return LocalDate.parse(logLine.substring(0, 10), DATE_FORMATTER);
//    }
//
//    private LocalTime parseTimeFromLog(String logLine) throws DateTimeParseException {
//        return LocalTime.parse(logLine.substring(11, 19), TIME_FORMATTER);
//    }
//
//    @ExceptionHandler(IOException.class)
//    public ResponseEntity<String> handleIoException(IOException e) {
//        return ResponseEntity.status(404).body(e.getMessage());
//    }
//
//    @ExceptionHandler(DateTimeParseException.class)
//    public ResponseEntity<String> handleDateTimeParseException() {
//        return ResponseEntity.badRequest().body(
//                "Invalid date/time format. "
//                        + "Use yyyy-MM-dd for date and HH:mm:ss for time");
//    }
//}

package org.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String LOG_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*";

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getLogs(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String time) throws IOException {

        Path logPath = Paths.get(LOG_FILE);
        String filteredLogs = "";

        if (Files.exists(logPath)) {
            filteredLogs = filterLogs(logPath, date, time);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"filtered_logs.log\"")
                .contentLength(filteredLogs.getBytes().length)
                .body(new ByteArrayResource(filteredLogs.getBytes()));
    }

    private String filterLogs(Path logPath, String dateParam, String timeParam) throws IOException {
        return Files.lines(logPath)
                .filter(line -> line != null && !line.trim().isEmpty())
                .filter(line -> {
                    // Пропускаем строки, которые не соответствуют шаблону лога
                    if (!line.matches(LOG_PATTERN)) {
                        return false;
                    }

                    try {
                        boolean matches = true;

                        if (dateParam != null) {
                            LocalDate logDate = parseDateFromLog(line);
                            LocalDate filterDate = LocalDate.parse(dateParam, DATE_FORMATTER);
                            matches = logDate.equals(filterDate);
                        }

                        if (matches && timeParam != null) {
                            LocalTime logTime = parseTimeFromLog(line);
                            LocalTime filterTime = LocalTime.parse(timeParam, TIME_FORMATTER);
                            matches = logTime.equals(filterTime);
                        }

                        return matches;
                    } catch (DateTimeParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private LocalDate parseDateFromLog(String logLine) throws DateTimeParseException {
        try {
            return LocalDate.parse(logLine.substring(0, 10), DATE_FORMATTER);
        } catch (StringIndexOutOfBoundsException e) {
            throw new DateTimeParseException("Invalid log format", logLine, 0);
        }
    }

    private LocalTime parseTimeFromLog(String logLine) throws DateTimeParseException {
        try {
            return LocalTime.parse(logLine.substring(11, 19), TIME_FORMATTER);
        } catch (StringIndexOutOfBoundsException e) {
            throw new DateTimeParseException("Invalid log format", logLine, 11);
        }
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIoException(IOException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException() {
        return ResponseEntity.badRequest().body(
                "Invalid date/time format. Use yyyy-MM-dd for date and HH:mm:ss for time");
    }
}
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API for accessing and filtering application logs")
public class LogController {

    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm:ss");
    private static final String LOG_PATTERN = ""
            + "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*";

    @Operation(summary = "Get application logs with optional filters",
            description = "Returns application logs filtered by date and/or time. "
                    + "Logs are returned as a downloadable text file.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Logs retrieved successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400",
                    description = "Invalid date/time format",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Log file not found or empty",
                    content = @Content),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getLogs(
            @Parameter(description = "Filter logs by date (format: yyyy-MM-dd)",
                    example = "2023-05-15",
                    style = ParameterStyle.SIMPLE)
            @RequestParam(required = false) String date,

            @Parameter(description = "Filter logs by time (format: HH:mm:ss)",
                    example = "14:30:00",
                    style = ParameterStyle.SIMPLE)
            @RequestParam(required = false) String time) throws IOException {

        Path logPath = Paths.get(LOG_FILE);
        String filteredLogs = "";

        if (Files.exists(logPath)) {
            filteredLogs = filterLogs(logPath, date, time);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"filtered_logs.log\"")
                .contentLength(filteredLogs.getBytes().length)
                .body(new ByteArrayResource(filteredLogs.getBytes()));
    }

    private String filterLogs(Path logPath, String dateParam, String timeParam) throws IOException {
        return Files.lines(logPath)
                .filter(line -> line != null && !line.trim().isEmpty())
                .filter(line -> {
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

    @Operation(hidden = true)
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIoException(IOException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @Operation(hidden = true)
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException() {
        return ResponseEntity.badRequest().body(
                "Invalid date/time format. Use yyyy-MM-dd for date and HH:mm:ss for time");
    }
}
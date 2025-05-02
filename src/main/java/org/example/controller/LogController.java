package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.events.LogEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Log Controller", description = "API for accessing and filtering application logs")
@RequiredArgsConstructor
public class LogController {
    private final ApplicationEventPublisher eventPublisher;
    private static Long lastProcessId = 0L;
    private static Resource lastLogResource = null;
    private static boolean isProcessing = false;

    public static synchronized void putResource(Long id, Resource resource) {
        lastProcessId = id;
        lastLogResource = resource;
        isProcessing = false; // Mark as processed
    }

    public static final String LOG_FILE = "app.log";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm:ss");
    public static final String LOG_PATTERN =
            "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*";

    @Operation(summary = "Start log filtering process",
            description = "Initiates asynchronous log filtering and returns a process ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Process started successfully",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400",
                    description = "Invalid date/time format",
                    content = @Content),
        @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content)
    })
    @PostMapping("/filter")
    public ResponseEntity<Map<String, Long>> startLogFiltering(
            @Parameter(description = "Filter logs by date (format: yyyy-MM-dd)",
                    example = "2023-05-15",
                    style = ParameterStyle.SIMPLE)
            @RequestParam(required = false) String date,

            @Parameter(description = "Filter logs by time (format: HH:mm:ss)",
                    example = "14:30:00",
                    style = ParameterStyle.SIMPLE)
            @RequestParam(required = false) String time) {

        synchronized (this) {
            // Clear previous resource on new request
            lastLogResource = null;
            lastProcessId++;
            isProcessing = true;

            eventPublisher.publishEvent(new LogEvent(this, date, time, lastProcessId));

            return ResponseEntity.ok(Map.of("processId", lastProcessId));
        }
    }

    @Operation(summary = "Get processing status",
            description = "Returns the status of a log filtering process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Status retrieved",
                    content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404",
                    description = "Process ID not found",
                    content = @Content)
    })
    @GetMapping("/status/{id}")
    public ResponseEntity<Map<String, String>> getStatus(
            @Parameter(description = "Process ID", example = "1")
            @PathVariable Long id) {

        synchronized (this) {
            if (!id.equals(lastProcessId)) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(Map.of(
                    "status", isProcessing ? "PROCESSING" : "READY",
                    "processId", id.toString()
            ));
        }
    }

    @Operation(summary = "Download filtered logs",
            description = "Downloads the filtered logs for a completed process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                    description = "Logs downloaded successfully",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404",
                    description = "Process not found or not completed",
                    content = @Content)
    })
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFilteredLogs(
            @Parameter(description = "Process ID", example = "1")
            @PathVariable Long id) {

        synchronized (this) {
            if (!id.equals(lastProcessId) || lastLogResource == null) {
                return ResponseEntity.notFound().build();
            }

            try {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"filtered_logs_" + id + ".log\"")
                        .contentLength(lastLogResource.contentLength())
                        .body(lastLogResource);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    public static LocalDate parseDateFromLog(String logLine) throws DateTimeParseException {
        try {
            return LocalDate.parse(logLine.substring(0, 10), DATE_FORMATTER);
        } catch (StringIndexOutOfBoundsException e) {
            throw new DateTimeParseException("Invalid log format", logLine, 0);
        }
    }

    public static LocalTime parseTimeFromLog(String logLine) throws DateTimeParseException {
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
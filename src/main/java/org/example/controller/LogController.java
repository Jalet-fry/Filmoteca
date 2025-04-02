package org.example.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_FILE = "app.log";

    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getLogsByDate(@RequestParam String date) throws IOException {
        Path filePath = Paths.get(LOG_FILE);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Log file not found!");
        }
        String str = String.join("", Files.lines(filePath)
                .filter(line -> line.startsWith(date))
                .collect(Collectors.toList()));
        Resource resource = new ByteArrayResource(str.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                  "attachment; filename=\"Filtered.log\"")
                .contentLength(str.length())
                .body(resource);
    }
}

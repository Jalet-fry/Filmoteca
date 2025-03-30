package org.example.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

    @GetMapping
    public List<String> getLogsByDate(@RequestParam String date) throws IOException {
        Path filePath = Paths.get(LOG_FILE);
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Log file not found!");
        }

        return Files.lines(filePath)
                .filter(line -> line.startsWith(date))
                .collect(Collectors.toList());
    }
}

//    @GetMapping(value = "/api/logs/download", produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<Resource> downloadLogFile() throws IOException {
//        // Полный путь к файлу
//        Path filePath = Paths.get(LOG_DIR + LOG_FILE).toAbsolutePath().normalize();
//        File logFile = filePath.toFile();
//
//        // Проверяем, существует ли файл
//        if (!logFile.exists()) {
//            throw new IOException("Log file not found: " + filePath);
//        }
//
//        // Создаем Resource для файла
//        Resource resource = new UrlResource(filePath.toUri());
//
//        // Настраиваем заголовки для скачивания
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                  "attachment; filename=\"" + logFile.getName() + "\"")
//                .contentLength(logFile.length())
//                .body(resource);
//    }
//}

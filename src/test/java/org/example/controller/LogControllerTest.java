//package org.example.controller;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.Resource;
//import org.springframework.http.ResponseEntity;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class LogControllerTest {
//
//    @InjectMocks
//    private LogController logController;
//
//    @Test
//    void getLogs_ShouldReturnLogs_WhenFileExists() throws IOException {
//        // Создаем временный файл логов для теста
//        Path tempLogFile = Files.createTempFile("test-log", ".log");
//        Files.writeString(tempLogFile, "Test log content");
//
//        // Используем рефлексию для установки значения LOG_FILE
//        try {
//            var field = LogController.class.getDeclaredField("LOG_FILE");
//            field.setAccessible(true);
//            field.set(logController, tempLogFile.toString());
//        } catch (Exception e) {
//            fail("Failed to set LOG_FILE path");
//        }
//
//        ResponseEntity<Resource> response = logController.getLogs(null, null);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//
//        // Удаляем временный файл
//        Files.deleteIfExists(tempLogFile);
//    }
//}
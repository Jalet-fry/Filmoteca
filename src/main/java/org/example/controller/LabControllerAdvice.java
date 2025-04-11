package org.example.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.BulkOperation;
import org.example.exception.DirectorAlreadyExists;
import org.example.exception.FilmAlreadyExists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class LabControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(LabControllerAdvice.class);

    // 1. Переопределяем обработку MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.error("Validation error (body): " + errors + '\n' +  HttpStatus.BAD_REQUEST);
        System.err.println("Validation error (body): " + errors);
        return new ResponseEntity<>("Validation error (body): " + errors, HttpStatus.BAD_REQUEST);
    }

    // 2. Обработка ConstraintViolationException для параметров запроса
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    return path.substring(
                            path.lastIndexOf('.') + 1)
                            + ": " + violation.getMessage();
                })
                .collect(Collectors.joining("; "));
        logger.error("Validation error (parameters): "
                + errors + '\n' + HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Validation error (parameters): "
                + errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Object> handleEntityNotFoundEx(Exception ex) {
        String apiError = "Entity Not Found Exception\n" + ex.getMessage() + ' ';
        logger.error(apiError + HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActorAlreadyExists.class)
    public ResponseEntity<Object> handleActorAlreadyExists(ActorAlreadyExists ex) {
        String apiError = String.format("Actor '%s' already exists", ex.getMessage()) + ' ';
        logger.error(apiError + HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DirectorAlreadyExists.class)
    public ResponseEntity<Object> handleDirectorAlreadyExists(DirectorAlreadyExists ex) {
        String apiError = String.format("Director '%s' already exists", ex.getMessage()) + ' ';
        logger.error(apiError + HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FilmAlreadyExists.class)
    public ResponseEntity<Object> handleFilmAlreadyExists(
            FilmAlreadyExists ex) {
        String apiError = ex.getCustomMessage() != null
                ? String.format("Film '%s' already exists", ex.getCustomMessage() + ' ')
                : "Film already exists" + ' ';
        logger.error(apiError + HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BulkOperation.class)
    public ResponseEntity<Object> handleBulkOperation(BulkOperation ex) {
        String apiError = String.format("Bulk %s creation failed",
                ex.getCustomMessage() != null ? ex.getMessage() : "unknown operation");
        logger.error(apiError  + ' ' + HttpStatus.CONFLICT);
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}

//package org.example.controller;
//
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolationException;
//import java.util.NoSuchElementException;
//import java.util.stream.Collectors;
//import org.example.exception.ActorAlreadyExists;
//import org.example.exception.BulkOperation;
//import org.example.exception.DirectorAlreadyExists;
//import org.example.exception.FilmAlreadyExists;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@ControllerAdvice
//public class LabControllerAdvice extends ResponseEntityExceptionHandler
//        implements LabControllerAdviceInterface {
//
//    // 1. Переопределяем обработку MethodArgumentNotValidException
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//        String apiError = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> error.getField() + ": " + error.getDefaultMessage())
//                .collect(Collectors.joining("; "));
//        return new ResponseEntity<>("Validation error (body): " + apiError, HttpStatus.BAD_REQUEST);
//    }
//
//    // 2. Обработка ConstraintViolationException для параметров запроса
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
//        String apiError = ex.getConstraintViolations().stream()
//                .map(violation -> {
//                    String path = violation.getPropertyPath().toString();
//                    return path.substring(
//                            path.lastIndexOf('.') + 1)
//                            + ": " + violation.getMessage();
//                })
//                .collect(Collectors.joining("; "));
//        return new ResponseEntity<>("Validation error (parameters): "
//                + apiError, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
//    protected ResponseEntity<Object> handleEntityNotFoundEx(Exception ex) {
//        String apiError = "Entity Not Found Exception\n" + ex.getMessage();
//        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ActorAlreadyExists.class)
//    protected ResponseEntity<Object> handleActorAlreadyExists(Exception ex) {
//        String apiError = String.format("Actor '%s' already exists", ex.getMessage());
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(DirectorAlreadyExists.class)
//    protected ResponseEntity<Object> handleDirectorAlreadyExists(Exception ex) {
//        String apiError = String.format("Director '%s' already exists", ex.getMessage());
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(FilmAlreadyExists.class)
//    protected ResponseEntity<Object> handleFilmAlreadyExists(
//            FilmAlreadyExists ex) {
//        String apiError = ex.getCustomMessage() != null
//                ? String.format("Film '%s' already exists", ex.getCustomMessage())
//                : "Film already exists";
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(BulkOperation.class)
//    protected ResponseEntity<Object> handleBulkOperation(BulkOperation ex) {
//        String apiError = String.format("Bulk %s creation failed",
//                ex.getCustomMessage() != null ? ex.getMessage() : "unknown operation");
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//}

//package org.example.controller;
//
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolationException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import java.util.stream.Collectors;
//import lombok.extern.slf4j.Slf4j;
//import org.example.exception.ActorAlreadyExists;
//import org.example.exception.BulkOperation;
//import org.example.exception.DirectorAlreadyExists;
//import org.example.exception.FilmAlreadyExists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//@ControllerAdvice
//@Slf4j
//public class LabControllerAdvice extends ResponseEntityExceptionHandler
//        implements LabControllerAdviceInterface {
//
//    private static final Logger logger = LoggerFactory.getLogger(LabControllerAdvice.class);
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @Override
//    public ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex,
//            HttpHeaders headers,
//            HttpStatus status,
//            WebRequest request) {
//
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(field ->
//                errors.put(field.getField(), field.getDefaultMessage())
//        );
//
//        logger.warn("Validation error for @RequestBody: {}", errors);
//
//        return ResponseEntity.badRequest().body(errors);
//    }
//
//    // 2. Обработка ConstraintViolationException (для @RequestParam/@PathVariable)
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
//        String errors = ex.getConstraintViolations().stream()
//                .map(violation -> {
//                    String path = violation.getPropertyPath().toString();
//                    return path.substring(path.lastIndexOf('.') + 1) + ": " + violation.getMessage();
//                })
//                .collect(Collectors.joining("; "));
//
//        logger.warn("Validation error for parameters: {}", errors);
//
//        return ResponseEntity.badRequest().body(errors);
//    }
//    // Остальные обработчики остаются без изменений
//    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
//    protected ResponseEntity<Object> handleEntityNotFoundEx(Exception ex) {
//        logger.warn("Entity not found: {}", ex.getMessage());
//        return new ResponseEntity<>("Entity Not Found Exception\n" + ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ActorAlreadyExists.class)
//    protected ResponseEntity<Object> handleActorAlreadyExists(ActorAlreadyExists ex) {
//        logger.warn("Actor creation conflict: {}", ex.getMessage());
//        return new ResponseEntity<>(String.format("Actor '%s' already exists", ex.getMessage()), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(DirectorAlreadyExists.class)
//    protected ResponseEntity<Object> handleDirectorAlreadyExists(DirectorAlreadyExists ex) {
//        logger.warn("Director creation conflict: {}", ex.getMessage());
//        return new ResponseEntity<>(String.format("Director '%s' already exists", ex.getMessage()), HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(FilmAlreadyExists.class)
//    protected ResponseEntity<Object> handleFilmAlreadyExists(FilmAlreadyExists ex) {
//        String message = ex.getCustomMessage() != null ? ex.getCustomMessage() : "unknown film";
//        logger.warn("Film creation conflict: {}", message);
//
//        String apiError = ex.getCustomMessage() != null
//                ? String.format("Film '%s' already exists", ex.getCustomMessage())
//                : "Film already exists";
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(BulkOperation.class)
//    protected ResponseEntity<Object> handleBulkOperation(BulkOperation ex) {
//        String operation = ex.getCustomMessage() != null ? ex.getMessage() : "unknown operation";
//        logger.warn("Bulk operation failed: {}", operation);
//
//        String apiError = String.format("Bulk %s creation failed", operation);
//        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
//    }
//}
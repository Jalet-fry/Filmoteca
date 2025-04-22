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
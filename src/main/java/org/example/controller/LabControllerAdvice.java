package org.example.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.DirectorAlreadyExists;
import org.example.exception.FilmAlreadyExists;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LabControllerAdvice extends ResponseEntityExceptionHandler
        implements LabControllerAdviceInterface {

    // 1. Переопределяем обработку MethodArgumentNotValidException

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
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
        return new ResponseEntity<>("Validation error (parameters): "
                + errors, HttpStatus.BAD_REQUEST);
    }

    // 3. Сохраняем ваши существующие обработчики
    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    protected ResponseEntity<Object> handleEntityNotFoundEx(Exception ex, WebRequest request) {
        String apiError = "Entity Not Found Exception\n" + ex.getMessage();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActorAlreadyExists.class)
    protected ResponseEntity<Object> handleActorAlreadyExists(Exception ex, WebRequest request) {
        String apiError = String.format("Actor '%s' already exists", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DirectorAlreadyExists.class)
    protected ResponseEntity<Object> handleDirectorAlreadyExists(Exception ex, WebRequest request) {
        String apiError = String.format("Director '%s' already exists", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FilmAlreadyExists.class)
    protected ResponseEntity<Object> handleFilmAlreadyExists(Exception ex, WebRequest request) {
        String apiError;
        if (ex.getMessage() == null) {
            apiError = "This film is already exists";
        } else {
            apiError = String.format("Film '%s' already exists", ex.getMessage());
        }
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
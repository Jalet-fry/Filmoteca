package org.example.controller;

import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.DirectorAlreadyExists;
import org.example.exception.FilmAlreadyExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LabControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    protected ResponseEntity<Object> handleEntityNotFoundEx(Exception ex, WebRequest request) {
        String apiError = "Entity Not Found Exception\n" + ex.getMessage();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ActorAlreadyExists.class)
    protected ResponseEntity<Object> handleActorAlreadyExists(Exception ex, WebRequest request) {
        String apiError = "Actor '{0}' already exists\n".formatted(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DirectorAlreadyExists.class)
    protected ResponseEntity<Object> handleDirectorAlreadyExists(Exception ex, WebRequest request) {
        String apiError = "Director '{0}' already exists\n".formatted(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FilmAlreadyExists.class)
    protected ResponseEntity<Object> handleFilmAlreadyExists(Exception ex, WebRequest request) {
        String apiError = "Film '{0}' already exists\n".formatted(ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}
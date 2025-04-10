package org.example.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.example.exception.ActorAlreadyExists;
import org.example.exception.BulkOperation;
import org.example.exception.DirectorAlreadyExists;
import org.example.exception.FilmAlreadyExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LabControllerAdviceTest {

    private LabControllerAdvice labControllerAdvice;
    private WebRequest mockRequest;

    @BeforeEach
    void setUp() {
        labControllerAdvice = new LabControllerAdvice();
        mockRequest = mock(WebRequest.class);
    }

    @Test
    void handleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("fieldName");
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<Object> response = labControllerAdvice.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Validation error (parameters)"));
        assertTrue(response.getBody().toString().contains("fieldName: must not be null"));
    }

    @Test
    void handleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        ResponseEntity<Object> response = labControllerAdvice.handleEntityNotFoundEx(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Entity Not Found Exception"));
        assertTrue(response.getBody().toString().contains("Entity not found"));
    }

    @Test
    void handleNoSuchElementException() {
        NoSuchElementException ex = new NoSuchElementException("No value present");
        ResponseEntity<Object> response = labControllerAdvice.handleEntityNotFoundEx(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Entity Not Found Exception"));
        assertTrue(response.getBody().toString().contains("No value present"));
    }

    @Test
    void handleActorAlreadyExists() {
        ActorAlreadyExists ex = new ActorAlreadyExists("John Doe");
        ResponseEntity<Object> response = labControllerAdvice.handleActorAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Actor 'John Doe' already exists", response.getBody());
    }

    @Test
    void handleDirectorAlreadyExists() {
        DirectorAlreadyExists ex = new DirectorAlreadyExists("Quentin Tarantino");
        ResponseEntity<Object> response = labControllerAdvice.handleDirectorAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Director 'Quentin Tarantino' already exists", response.getBody());
    }

    @Test
    void handleFilmAlreadyExistsWithCustomMessage() {
        FilmAlreadyExists ex = new FilmAlreadyExists("Pulp Fiction");
        ResponseEntity<Object> response = labControllerAdvice.handleFilmAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Film 'Pulp Fiction' already exists", response.getBody());
    }

    @Test
    void handleFilmAlreadyExistsWithoutCustomMessage() {
        FilmAlreadyExists ex = new FilmAlreadyExists(null);
        ResponseEntity<Object> response = labControllerAdvice.handleFilmAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Film already exists", response.getBody());
    }

    @Test
    void handleBulkOperationWithCustomMessage() {
        BulkOperation ex = new BulkOperation("actor");
        ResponseEntity<Object> response = labControllerAdvice.handleBulkOperation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Bulk actor creation failed", response.getBody());
    }

    @Test
    void handleBulkOperationWithoutCustomMessage() {
        BulkOperation ex = new BulkOperation(null);
        ResponseEntity<Object> response = labControllerAdvice.handleBulkOperation(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Bulk unknown operation creation failed", response.getBody());
    }
}
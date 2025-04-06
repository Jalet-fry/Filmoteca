package org.example.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmAlreadyExists extends Exception {
    public FilmAlreadyExists(String message, Throwable cause) {
        super(message, cause);
        log.error("Constructor FilmAlreadyExists with cause, message: {}", message);
    }

    public FilmAlreadyExists(String message) {
        super(message);
        log.error("Constructor FilmAlreadyExists, message: {}", message);
    }
}
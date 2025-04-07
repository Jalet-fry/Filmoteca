package org.example.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmAlreadyExists extends Exception {
    private final String customMessage;

    public FilmAlreadyExists(String message, Throwable cause) {
        super(message, cause);
        this.customMessage = message;
        log.error("Constructor FilmAlreadyExists with cause, message: {}", message);
    }

    public FilmAlreadyExists(String message) {
        super(message);
        this.customMessage = message;
        log.error("Constructor FilmAlreadyExists, message: {}", message);
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
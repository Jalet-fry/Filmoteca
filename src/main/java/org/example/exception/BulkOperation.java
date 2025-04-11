package org.example.exception;

import lombok.Getter;

@Getter
public class BulkOperation extends Exception {
    private final String customMessage;

    public BulkOperation(String message) {
        super(message);
        this.customMessage = message;
    }
}
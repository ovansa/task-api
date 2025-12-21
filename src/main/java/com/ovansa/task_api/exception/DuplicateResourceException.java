package com.ovansa.task_api.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException () {
        super("Resource already exists");
    }

    public DuplicateResourceException (String message) {
        super(message != null ? message : "Resource already exists");
    }
}
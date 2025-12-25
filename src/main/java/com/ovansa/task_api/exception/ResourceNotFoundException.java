package com.ovansa.task_api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException () {
        super("Resource not found.");
    }

    public ResourceNotFoundException (String message) {
        super(message != null ? message : "Resource not found.");
    }
}
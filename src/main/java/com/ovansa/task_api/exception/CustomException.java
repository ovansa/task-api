package com.ovansa.task_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final String errorCode;

    public CustomException (String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}

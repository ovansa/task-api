package com.ovansa.task_api.exception;

import com.ovansa.task_api.domain.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException () {
        super(Messages.INVALID_CREDENTIALS);
    }
}
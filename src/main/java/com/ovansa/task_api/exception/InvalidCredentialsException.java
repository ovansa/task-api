package com.ovansa.task_api.exception;

import com.ovansa.task_api.domain.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidCredentialsException extends CustomException {

    public InvalidCredentialsException() {
        super(Messages.INVALID_CREDENTIALS, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }
}
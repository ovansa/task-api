package com.ovansa.task_api.exception.handler;

import com.ovansa.task_api.domain.response.ErrorResponse;
import com.ovansa.task_api.exception.CustomException;
import com.ovansa.task_api.exception.DuplicateResourceException;
import com.ovansa.task_api.exception.InvalidCredentialsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Caught exception", ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        return buildErrorResponse(new CustomException(ex.getMessage(), "DUPLICATE_RESOURCE"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(new CustomException(ex.getMessage(), "INVALID_CREDENTIALS"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(InvalidCredentialsException ex) {
        return buildErrorResponse(new CustomException(ex.getMessage(), "INVALID_CREDENTIALS"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<> ();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        log.warn(
                "Validation failed: {}",
                fieldErrors
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .errorCode("VALIDATION_ERROR")
                .errors(fieldErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(CustomException ex, HttpStatus status) {
        log.error(
                "Handled application exception: type={}, status={}, message={}, errorCode={}",
                ex.getClass().getSimpleName(),
                status,
                ex.getMessage(),
                ex.getErrorCode(),
                ex
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}





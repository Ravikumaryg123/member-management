package com.surest.member.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static class ErrorResponse {
        private int statusCode;
        private String message;
        private LocalDateTime timestamp;
        private String path;

        public ErrorResponse(int statusCode, String message, LocalDateTime timestamp, String path) {
            this.statusCode = statusCode;
            this.message = message;
            this.timestamp = timestamp;
            this.path = path;
        }
    }

    @ExceptionHandler(BusinessServiceException.class)
    public ResponseEntity<ErrorResponse> handleBusinessServiceException(BusinessServiceException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred: " + ex.getMessage(),
                LocalDateTime.now(),
                request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


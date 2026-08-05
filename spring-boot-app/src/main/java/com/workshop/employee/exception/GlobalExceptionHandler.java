package com.workshop.employee.exception;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ApiError> response(HttpStatus status, String message, String code) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(message, code, Instant.now()));
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> notFound(NotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND");
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class,
        HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<ApiError> badRequest(Exception ex) {
        return response(HttpStatus.BAD_REQUEST, ex instanceof BadRequestException
            ? ex.getMessage() : "Invalid request", "BAD_REQUEST");
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ApiError> unauthorized(UnauthorizedException ex) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> fallback(Exception ex) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "INTERNAL_ERROR");
    }
}

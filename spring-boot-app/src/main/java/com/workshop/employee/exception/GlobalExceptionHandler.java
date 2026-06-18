package com.workshop.employee.exception;

import com.workshop.employee.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GoalsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGoalsNotFound(GoalsNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), "GOALS_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

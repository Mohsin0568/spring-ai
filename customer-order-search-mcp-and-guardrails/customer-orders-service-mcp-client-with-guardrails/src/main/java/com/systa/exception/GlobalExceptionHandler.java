package com.systa.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GuardrailViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleGuardrailViolation(GuardrailViolationException ex) {
        return Map.of(
            "error", "GUARDRAIL_VIOLATION",
            "message", ex.getMessage(),
            "timestamp", Instant.now().toString()
        );
    }

    // Handles violations raised by @ValidSearchQuery on @RequestParam fields.
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
            .map(v -> v.getMessage())
            .findFirst()
            .orElse("Invalid query");
        return Map.of(
            "error", "GUARDRAIL_VIOLATION",
            "message", message,
            "timestamp", Instant.now().toString()
        );
    }
}

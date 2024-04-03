package com.adm.lucas.posts.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage().startsWith("Email or username are unavailable.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or username are unavailable.");
        }
        if (ex.getMessage().startsWith("User must be at least 12 years old.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must be at least 12 years old.");
        }
        if (ex.getMessage().startsWith("Invalid password.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> uniqueValueAlreadyExists(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private record validationError(String field, String message) {
        public validationError(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity validateField(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream().map(validationError::new).toList());
    }

}
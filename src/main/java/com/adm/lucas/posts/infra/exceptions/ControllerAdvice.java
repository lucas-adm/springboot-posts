package com.adm.lucas.posts.infra.exceptions;

import jakarta.persistence.EntityExistsException;
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
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email or username are unavailable.");
        }
        if (ex.getMessage().startsWith("User must be at least 12 years old.")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User must be at least 12 years old.");
        }
        if (ex.getMessage().startsWith("Invalid password.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password.");
        }
        if (ex.getMessage().startsWith("Only the post user can edit this post.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the post user can edit this post.");
        }
        if (ex.getMessage().startsWith("Only the comment user can edit this comment.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the comment user can edit this comment.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> uniqueValueAlreadyExists(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> entitAlreadyExists(EntityExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only one upvote per user.");
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
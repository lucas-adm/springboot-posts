package com.adm.lucas.posts.infra.exceptions;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
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
        if (ex.getMessage().startsWith("Only the user owner can edit this account.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the user owner can edit this account.");
        }
        if (ex.getMessage().startsWith("Only the post user can delete this post.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the post user can delete this post.");
        }
        if (ex.getMessage().startsWith("Only the post user can edit this post.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the post user can edit this post.");
        }
        if (ex.getMessage().startsWith("This post is closed.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This post is closed.");
        }
        if (ex.getMessage().startsWith("Only the comment user can edit this comment.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the comment user can edit this comment.");
        }
        if (ex.getMessage().startsWith("Only the comment user can delete this comment.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the comment user can delete this comment.");
        }
        if (ex.getMessage().startsWith("Only the answer user can edit this answer.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the answer user can edit this answer.");
        }
        if (ex.getMessage().startsWith("Only the answer user can delete this answer.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only the answer user can delete this answer.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> uniqueValueAlreadyExists(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("username")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
        }
        if (ex.getMessage().contains("email")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + ex.getMessage());
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<String> findTwoReferences(IncorrectResultSizeDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or username are unavailable.");
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
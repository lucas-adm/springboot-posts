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
        if (ex.getMessage().startsWith("Email ou usuário indisponível.")) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Email ou usuário indisponível.");
        }
        if (ex.getMessage().startsWith("Usuário precisa ter ao menos 12 anos.")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário precisa ter ao menos 12 anos.");
        }
        if (ex.getMessage().startsWith("Senha inválida.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha inválida.");
        }
        if (ex.getMessage().startsWith("Apenas o próprio criador pode editar esta conta.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o próprio criador pode editar esta conta.");
        }
        if (ex.getMessage().startsWith("Apenas o criador do post pode editar o post.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o criador do post pode editar o post.");
        }
        if (ex.getMessage().startsWith("Este post está fechado.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este post está fechado.");
        }
        if (ex.getMessage().startsWith("Apenas o criador do comentário pode editar o comentário.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o criador do comentário pode editar o comentário.");
        }
        if (ex.getMessage().startsWith("Apenas o criador do comentário pode editar o comentário.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o criador do comentário pode editar o comentário.");
        }
        if (ex.getMessage().startsWith("Apenas o criador da resposta pode editar a resposta.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o criador da resposta pode editar a resposta.");
        }
        if (ex.getMessage().startsWith("Apenas o criador da resposta pode deletar a resposta.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Apenas o criador da resposta pode deletar a resposta.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> uniqueValueAlreadyExists(DataIntegrityViolationException ex) {
        if (ex.getMessage().contains("username")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe.");
        }
        if (ex.getMessage().contains("email")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já existe.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation: " + ex.getMessage());
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<String> findTwoReferences(IncorrectResultSizeDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email ou usuário indisponível.");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> entitAlreadyExists(EntityExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Apenas um like por usuário.");
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
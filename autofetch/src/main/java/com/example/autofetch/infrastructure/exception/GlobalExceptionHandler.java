package com.example.autofetch.infrastructure.exception;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.autofetch.shared.ApiResponse;
import com.example.autofetch.shared.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException er) {

        ErrorResponse error = ErrorResponse.builder()
            .error("ENTITY_NOT_FOUND")
            .message(er.getMessage())
            .build();
            
        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException er) {
        List<DataErrors> erros = er.getFieldErrors().stream()
                .map(DataErrors::new)
                .toList();

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(erros)
                .error(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException er) {
        ErrorResponse error = ErrorResponse.builder()
            .error("ILLEGAL_STATE")
            .message(er.getMessage())
            .build();

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException er) {
        ErrorResponse error = ErrorResponse.builder()
            .error("ILLEGAL_ARGUMENT")
            .message(er.getMessage())
            .build();

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException er) {
        String message = "Data integrity error.";
        String rootCauseMessage = er.getMostSpecificCause() != null ? er.getMostSpecificCause().getMessage() : er.getMessage();

        if (rootCauseMessage != null) {
            if (rootCauseMessage.toLowerCase().contains("duplicate key") || rootCauseMessage.toLowerCase().contains("violates unique constraint")) {
                message = "A user with the given email or username already exists.";
            }
        }

        ErrorResponse error = ErrorResponse.builder()
            .error("CONFLICT")
            .message(message)
            .build();

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    public record DataErrors(String field, String message) {

        public DataErrors(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleInternalServerError(Exception er) {
        ErrorResponse error = ErrorResponse.builder()
            .error("INTERNAL_SERVER_ERROR")
            .message(er.getMessage())
            .build();

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .data(null)
                .error(error)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

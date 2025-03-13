package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {
    record ErrorBody(String problem) {
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorBody> handleRuntimeException(final RuntimeException runtimeException) {
        return new ResponseEntity<>(new ErrorBody(runtimeException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity handleUserException(final EntityNotFoundException entityNotFoundException) {
        return new ResponseEntity<>(new ErrorBody(entityNotFoundException.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(final IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(new ErrorBody(illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleNullPointerException(final NullPointerException nullPointerException) {
        return new ResponseEntity<>(new ErrorBody(nullPointerException.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Créer une map pour retourner les erreurs de validation
        Map<String, String> errors = new HashMap<>();

        // Parcours des erreurs de validation
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        // Retourne les erreurs avec un status BAD_REQUEST
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



}

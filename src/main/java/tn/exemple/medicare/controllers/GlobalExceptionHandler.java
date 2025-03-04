package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {
    record ErrorBody(String problem) {
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


}

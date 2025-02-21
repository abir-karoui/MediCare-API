package tn.exemple.medicare.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tn.exemple.medicare.exceptions.UserException;

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

}

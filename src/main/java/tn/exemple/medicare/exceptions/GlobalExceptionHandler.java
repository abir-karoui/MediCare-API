package tn.exemple.medicare.exceptions;

import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tn.exemple.medicare.exceptions.ExceptionResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.ietf.jgss.GSSException.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.*;
import static tn.exemple.medicare.exceptions.BusinessErrorCode.*;


@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorBody(String problem) {
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException ex) {
        BusinessErrorCode code = ex.getErrorCode();
        ExceptionResponse response = ExceptionResponse.builder()
                .businessErrorCode(code.getCode())
                .businessErrorDescription(code.getDescription())
                .error(code.getHttpStatus().getReasonPhrase())
                .build();
        return new ResponseEntity<>(response, code.getHttpStatus());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BadCredentialsException exp) {
        var error = BusinessErrorCode.BAD_CREDENTIALS;

        return ResponseEntity
                .status(error.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(error.getCode()) // ✅ Ton code métier
                                .businessErrorDescription(error.getDescription())
                                .error(error.getDescription())
                                .build()
                );
    }


    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ExceptionResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ACCOUNT_LOCKED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );

    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ExceptionResponse> handleException(DisabledException exp) {
        var error = BusinessErrorCode.ACCOUNT_DISABLED;
        return ResponseEntity
                .status(error.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ACCOUNT_DISABLED.getDescription())
                                .error(exp.getMessage())
                                .build()
                );

    }
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ExceptionResponse> handleException(MessagingException exp) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exp.getMessage())
                                .build()
                );

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exp) {
        Set<String> errors = new HashSet<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );

    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription(("INTERNAL ERROR , contact the admin"))
                                .error(exp.getMessage())
                                .build()
                );

    }


    /*
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorBody> handleRuntimeException(final RuntimeException runtimeException) {
        return new ResponseEntity<>(new ErrorBody(runtimeException.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity handleUserException(final EntityNotFoundException entityNotFoundException) {
        return new ResponseEntity<>(new ErrorBody(entityNotFoundException.getMessage()), BAD_REQUEST);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(final IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(new ErrorBody(illegalArgumentException.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleNullPointerException(final NullPointerException nullPointerException) {
        return new ResponseEntity<>(new ErrorBody(nullPointerException.getMessage()), BAD_REQUEST);
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }
    /*@ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorBody> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(new ErrorBody(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
   */

    /*
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exp) {
        var error = exp.getErrorCode();
        String errorMessage = error.getDescription();

        // Si c'est une erreur de compte non activé, afficher un message explicite
        if (error == BusinessErrorCode.ACCOUNT_DISABLED) {
            errorMessage = "Le compte n'est pas activé. Veuillez activer votre compte.";
        }

        return ResponseEntity
                .status(error.getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(error.getCode())
                                .businessErrorDescription(errorMessage)  // Message détaillé pour le compte désactivé
                                .error(errorMessage)
                                .build()
                );
    }*/







}

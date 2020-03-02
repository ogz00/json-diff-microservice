package com.oguz.waes.scalableweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PrettyException> handleValidationException(MethodArgumentNotValidException ex) {
        PrettyException error = PrettyException.getInstance("invalid content in the request body!").setStatus(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(PrettyException.class)
    public ResponseEntity<PrettyException> handlePrettyException(PrettyException ex) {
        return new ResponseEntity<>(ex, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PrettyException> handleInternalError(Exception ex) {
        PrettyException error = PrettyException.getInstance(ex.getMessage()).setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(error, error.getStatus());
    }
}

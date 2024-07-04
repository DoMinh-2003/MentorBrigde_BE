package com.BE.exception.handler;

import com.BE.exception.exceptions.NotAllowException;
import com.BE.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class NotAllowExceptionHandler {
    @ExceptionHandler(NotAllowException.class)
    public ResponseEntity<Object> handleNotAllowException(NotAllowException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

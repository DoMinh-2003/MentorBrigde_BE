package com.BE.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NullPointerExceptionHandler {
@ExceptionHandler(NullPointerException.class)
public ResponseEntity<?> nullPointer(NullPointerException nullPointerException) {
return  new ResponseEntity<String>(nullPointerException.getMessage(), HttpStatus.BAD_REQUEST);
}
}

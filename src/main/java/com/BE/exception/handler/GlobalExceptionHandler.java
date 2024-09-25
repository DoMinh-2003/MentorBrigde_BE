package com.BE.exception.handler;

import com.BE.exception.exceptions.EnumValidationException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.utils.EnumUtils;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String message = "Failed to parse JSON";

        if (mostSpecificCause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) mostSpecificCause;
            List<JsonMappingException.Reference> path = ife.getPath();
            String fieldName = path.get(path.size() - 1).getFieldName();
            Class<?> targetType = ife.getTargetType();
            String value = ife.getValue().toString();

            if (targetType.isEnum()) {
                String validValues = EnumUtils.getValidEnumValues(targetType.asSubclass(Enum.class));
                message = String.format("Field '%s' has invalid value '%s'. Expected one of: %s", fieldName, value, validValues);
            } else {
                message = String.format("Field '%s' has invalid value '%s'. Expected type: %s", fieldName, value, targetType.getSimpleName());
            }
        } else {
            message = mostSpecificCause.getMessage();
        }

        errors.put("message", message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EnumValidationException.class)
    public ResponseEntity<Map<String, String>> handleEnumValidationException(EnumValidationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<String> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}

package com.BE.exception;


import com.BE.exception.exceptions.EnumValidationException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;


public class EnumValidatorClass implements ConstraintValidator<EnumValidator, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;
    private String message;

    @Override
    public void initialize(EnumValidator annotation) {
        this.enumClass = annotation.enumClass();
        this.message = annotation.message();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are valid, use @NotNull for null checks
        }
        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(enumValue -> enumValue.equals(value));
        if (!isValid) {
            throw new EnumValidationException(message);
        }
        return true;
    }
}


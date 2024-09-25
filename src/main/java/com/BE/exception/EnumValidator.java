package com.BE.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidatorClass.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClass();
    String message() default "Must be a valid enum value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

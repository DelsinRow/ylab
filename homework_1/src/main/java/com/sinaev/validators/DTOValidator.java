package com.sinaev.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator for Data Transfer Objects (DTOs).
 * <p>
 * This class provides a method to validate DTOs using Jakarta Bean Validation (JSR 380).
 * If any constraint violations are found, a ValidationException is thrown.
 * </p>
 */
public class DTOValidator {
    private final Validator validator;

    /**
     * Constructs a DTOValidator and initializes the Validator instance.
     */
    public DTOValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Validates the specified DTO.
     *
     * @param <T> the type of the DTO
     * @param dto the DTO to validate
     * @throws ValidationException if any constraint violations are found
     */
    public <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            throw new ValidationException("Validation failed: " + errors);
        }
    }
}

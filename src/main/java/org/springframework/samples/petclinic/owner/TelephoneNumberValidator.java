package org.springframework.samples.petclinic.owner;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom validator for telephone numbers.
 * Implements Business Rule BR-030: allows digits, dashes, and parentheses.
 * Before checking length, strips all non-digit characters, then ensures the resulting
 * string has between 10 and 15 digits.
 * Provides specific error messages for invalid characters or incorrect length.
 */
public class TelephoneNumberValidator implements ConstraintValidator<ValidTelephoneNumber, String> {

    @Override
    public void initialize(ValidTelephoneNumber constraintAnnotation) {
        // No initialization needed for this validator
    }

    @Override
    public boolean isValid(String telephoneField, ConstraintValidatorContext context) {
        if (telephoneField == null || telephoneField.isBlank()) {
            return true; // @NotBlank handles null/empty, this validator focuses on format and length
        }

        // FR-077, BR-015: Permit digits, dashes, and parentheses
        // Check for allowed characters (digits, dashes, parentheses)
        // This regex ensures only these characters are present in the raw input.
        if (!telephoneField.matches("^[0-9()\\-]+$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{telephone.invalid.characters}")
                   .addConstraintViolation();
            return false;
        }

        // BR-030: Strip non-digit characters for length validation
        String strippedTelephone = telephoneField.replaceAll("[^0-9]", "");

        // BR-030: Check length of stripped string (10 to 15 digits)
        if (strippedTelephone.length() < 10 || strippedTelephone.length() > 15) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{telephone.invalid.length}")
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}

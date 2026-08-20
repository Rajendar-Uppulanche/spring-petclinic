package org.springframework.samples.petclinic.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.samples.petclinic.util.PhoneNumberUtil;

public class ValidPhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            // Let @NotBlank handle null or empty values
            return true;
        }

        String normalizedPhoneNumber = PhoneNumberUtil.normalizePhoneNumber(value);
        int length = normalizedPhoneNumber.length();

        if (length < 10 || length > 15) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
}

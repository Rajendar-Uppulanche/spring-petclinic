package org.springframework.samples.petclinic.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PastOrPresentDateValidator implements ConstraintValidator<PastOrPresentDate, LocalDate> {

    @Override
    public void initialize(PastOrPresentDate constraintAnnotation) {
        // No initialization needed for this validator
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return true; // Null dates should be handled by @NotNull if required
        }
        return !date.isAfter(LocalDate.now());
    }
}
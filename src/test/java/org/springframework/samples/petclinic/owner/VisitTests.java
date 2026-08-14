package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTests {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidateWhenDescriptionIsWithinLimit() {
        Visit visit = new Visit();
        visit.setDate(LocalDate.now().plusDays(1));
        visit.setDescription("This is a short description.");

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidateWhenDescriptionIsExactly500Characters() {
        Visit visit = new Visit();
        visit.setDate(LocalDate.now().plusDays(1));
        String longDescription = "a".repeat(500);
        visit.setDescription(longDescription);

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotValidateWhenDescriptionExceeds500Characters() {
        Visit visit = new Visit();
        visit.setDate(LocalDate.now().plusDays(1));
        String tooLongDescription = "a".repeat(501);
        visit.setDescription(tooLongDescription);

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertThat(violations).hasSize(1);
        ConstraintViolation<Visit> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).contains("size must be between 0 and 500");
    }

    @Test
    void shouldNotValidateWhenDescriptionIsBlank() {
        Visit visit = new Visit();
        visit.setDate(LocalDate.now().plusDays(1));
        visit.setDescription(""); // @NotBlank also checks for empty strings

        Set<ConstraintViolation<Visit>> violations = validator.validate(visit);
        assertThat(violations).hasSize(1);
        ConstraintViolation<Visit> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("description");
        assertThat(violation.getMessage()).contains("must not be blank");
    }
}

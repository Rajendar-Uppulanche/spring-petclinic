package org.springframework.samples.petclinic.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PastOrPresentDateValidatorTests {

    private PastOrPresentDateValidator validator;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    void setUp() {
        validator = new PastOrPresentDateValidator();
    }

    @Test
    void shouldBeValidForPastDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        assertThat(validator.isValid(pastDate, constraintValidatorContext)).isTrue();
    }

    @Test
    void shouldBeValidForPresentDate() {
        LocalDate presentDate = LocalDate.now();
        assertThat(validator.isValid(presentDate, constraintValidatorContext)).isTrue();
    }

    @Test
    void shouldBeInvalidForFutureDate() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThat(validator.isValid(futureDate, constraintValidatorContext)).isFalse();
    }

    @Test
    void shouldBeValidForNullDate() {
        assertThat(validator.isValid(null, constraintValidatorContext)).isTrue();
    }
}
package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

class VisitValidatorTests {

	private VisitValidator visitValidator;

	@BeforeEach
	void setUp() {
		visitValidator = new VisitValidator();
	}

	@Test
	void shouldSupportVisitClass() {
		assertThat(visitValidator.supports(Visit.class)).isTrue();
	}

	@Test
	void shouldNotSupportOtherClasses() {
		assertThat(visitValidator.supports(Object.class)).isFalse();
	}

	@Test
	void shouldValidateWhenDescriptionIsEmpty() {
		Visit visit = new Visit();
		visit.setDescription("");
		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		visitValidator.validate(visit, errors);
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("description").getCode()).isEqualTo("required");
	}

	@Test
	void shouldValidateWhenDescriptionIsTooLong() {
		Visit visit = new Visit();
		visit.setDescription("a".repeat(501)); // 501 characters
		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		visitValidator.validate(visit, errors);
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("description").getCode()).isEqualTo("tooLong");
	}

	@Test
	void shouldValidateWhenDescriptionIsExactly500Chars() {
		Visit visit = new Visit();
		visit.setDescription("a".repeat(500)); // 500 characters
		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		visitValidator.validate(visit, errors);
		assertThat(errors.hasErrors()).isFalse(); // Should pass
	}

	@Test
	void shouldValidateWhenDescriptionIsValid() {
		Visit visit = new Visit();
		visit.setDescription("This is a valid description.");
		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		visitValidator.validate(visit, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldValidateWhenDateIsNull() {
		Visit visit = new Visit();
		visit.setDescription("Valid description.");
		visit.setDate(null); // Set date to null
		Errors errors = new BeanPropertyBindingResult(visit, "visit");
		visitValidator.validate(visit, errors);
		assertThat(errors.hasErrors()).isTrue();
		assertThat(errors.getFieldError("date").getCode()).isEqualTo("required");
	}
}
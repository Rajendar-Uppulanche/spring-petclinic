package org.springframework.samples.petclinic.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * @author Michael Isvy
 * @author Mark Fisher
 */
class PetTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	void shouldNotValidateWhenNameEmpty() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Pet pet = new Pet();
		pet.setName("");
		pet.setBirthDate(LocalDate.now());

		Validator validator = createValidator();
		Set<ConstraintViolation<Pet>> constraintViolations = validator.validate(pet);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Pet> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldNotValidateWhenBirthDateInFuture() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().plusDays(1));

		Validator validator = createValidator();
		Set<ConstraintViolation<Pet>> constraintViolations = validator.validate(pet);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Pet> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("birthDate");
		assertThat(violation.getMessage()).isEqualTo("Birth date cannot be in the future");
	}

}
